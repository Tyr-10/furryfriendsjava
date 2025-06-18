package proyecto.demo.controller;

import proyecto.demo.model.Viabilidad;
import proyecto.demo.repository.ViabilidadRepository;
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
@RequestMapping("/viabilidad")
public class ViabilidadController {

    private final ViabilidadRepository viabilidadRepository;

    @Value("${upload.path}")
    private String uploadDir;

    // Mostrar archivos y buscar
    @GetMapping
    public String mostrar(@RequestParam(value = "archivo", required = false) String archivo, Model model,
                          @RequestParam(value = "success", required = false) String success,
                          @RequestParam(value = "error", required = false) String error) {

        List<Viabilidad> viabilidades = (archivo != null && !archivo.isEmpty())
                ? viabilidadRepository.findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(archivo)
                : viabilidadRepository.findByActivoTrueOrderByIdDesc();

        model.addAttribute("viabilidades", viabilidades);
        if (success != null) model.addAttribute("success", success);
        if (error != null) model.addAttribute("error", error);

        return "viabilidad";
    }

    // Subir archivo
    @PostMapping("/subir")
    public String subir(@RequestParam("archivo") MultipartFile archivo,
                        Principal principal,
                        Model model) throws IOException {

        if (archivo == null || archivo.isEmpty()) {
            model.addAttribute("error", "Debes subir un archivo.");
            model.addAttribute("viabilidades", viabilidadRepository.findByActivoTrueOrderByIdDesc());
            return "viabilidad";
        }

        String nombreOriginal = Path.of(archivo.getOriginalFilename()).getFileName().toString();
        Path destino = Paths.get(uploadDir).resolve(nombreOriginal);
        Files.createDirectories(destino.getParent());
        Files.write(destino, archivo.getBytes());

        Viabilidad viabilidad = new Viabilidad();
        viabilidad.setArchivo("/uploads/" + nombreOriginal);
        viabilidad.setNombreOriginal(nombreOriginal);
        viabilidad.setUsuarioId(obtenerUsuarioIdDesdePrincipal(principal));
        viabilidad.setRolId(obtenerRolIdDesdePrincipal(principal));
        viabilidad.setActivo(true);

        viabilidadRepository.save(viabilidad);

        model.addAttribute("success", "Archivo subido con éxito.");
        model.addAttribute("viabilidades", viabilidadRepository.findByActivoTrueOrderByIdDesc());

        return "viabilidad";
    }

    // Desactivar archivo
    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id) {
        Viabilidad estudio = viabilidadRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("ID no válido: " + id));
        estudio.setActivo(false);
        viabilidadRepository.save(estudio);
        return "redirect:/viabilidad?success=Documento desactivado con éxito.";
    }

    private Long obtenerUsuarioIdDesdePrincipal(Principal principal) {
        return 1L; // Ajusta esto con tu lógica de usuario
    }

    private Long obtenerRolIdDesdePrincipal(Principal principal) {
        return 1L; // Ajusta esto con tu lógica de roles
    }
}