package proyecto.demo.controller;

import proyecto.demo.model.Seguimiento;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.SeguimientoRepository;
import proyecto.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seguimiento")
public class SeguimientoController {

    private final SeguimientoRepository seguimientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${upload.path}")
    private String uploadDir;

    // Mostrar archivos SOLO si hay búsqueda
    @GetMapping
    public String mostrar(@RequestParam(value = "archivo", required = false) String archivo, 
                          Model model,
                          @RequestParam(value = "success", required = false) String success,
                          @RequestParam(value = "error", required = false) String error) {

        List<Seguimiento> seguimientos = null;

        if (archivo != null && !archivo.isEmpty()) {
            seguimientos = seguimientoRepository
                    .findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(archivo);
        }

        model.addAttribute("seguimientos", seguimientos);

        if (success != null) model.addAttribute("success", success);
        if (error != null) model.addAttribute("error", error);

        return "seguimiento";
    }

    // Subir archivo
    @PostMapping("/subir")
    public String subir(@RequestParam("archivo") MultipartFile archivo,
                        Principal principal,
                        Model model) throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            model.addAttribute("error", "Debes subir un archivo.");
            return "seguimiento";
        }

        // Guardar archivo físico
        String nombreOriginal = Path.of(archivo.getOriginalFilename()).getFileName().toString();
        Path destino = Paths.get("src/main/resources/static/uploads").resolve(nombreOriginal);
        Files.createDirectories(destino.getParent());
        Files.write(destino, archivo.getBytes());

        // Obtener usuario autenticado
        Usuario usuario = obtenerUsuarioDesdePrincipal(principal);

        // Guardar en base de datos
        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setArchivo("/uploads/" + nombreOriginal);
        seguimiento.setNombreOriginal(nombreOriginal);
        seguimiento.setUsuarioId(usuario.getId());
        seguimiento.setRolId(usuario.getRol().getId());
        seguimiento.setActivo(true);

        seguimientoRepository.save(seguimiento);

        model.addAttribute("success", "Archivo subido con éxito.");
        return "redirect:/seguimiento?success=Archivo subido con éxito.";
    }

    // Desactivar archivo
    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        Seguimiento seguimiento = seguimientoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("ID no válido: " + id));
        seguimiento.setActivo(false);
        seguimientoRepository.save(seguimiento);
        return "redirect:/seguimiento?success=Documento desactivado con éxito.";
    }

    // Obtener usuario autenticado desde Spring Security
    private Usuario obtenerUsuarioDesdePrincipal(Principal principal) {
        if (principal != null) {
            String correo = principal.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario != null) {
                return usuario;
            }
        }
        throw new IllegalStateException("No se pudo obtener el usuario autenticado.");
    }
}