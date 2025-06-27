package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Seguimiento;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.SeguimientoRepository;
import proyecto.demo.repository.UsuarioRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/seguimientocrud")
public class SeguimientoCrudController {

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String index(@RequestParam(required = false) String nombreOriginal, Model model) {
        List<Seguimiento> seguimientos = seguimientoRepository.buscarPorNombreOriginal(nombreOriginal);
        model.addAttribute("seguimientos", seguimientos);
        model.addAttribute("nombreOriginal", nombreOriginal);
        return "seguimientocrud/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear() {
        return "seguimientocrud/create";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("archivo") MultipartFile archivo) {
        if (!archivo.isEmpty()) {
            try {
                String nombreOriginal = archivo.getOriginalFilename();
                String nombreArchivo = UUID.randomUUID() + "_" + nombreOriginal;

                // Ruta absoluta a static/uploads
                String rutaAbsoluta = new File("src/main/resources/static/uploads").getAbsolutePath();
                File directorio = new File(rutaAbsoluta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                Path ruta = Paths.get(rutaAbsoluta, nombreArchivo);
                archivo.transferTo(ruta.toFile());

                // Obtener usuario autenticado
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String correo = auth.getName();
                Usuario usuario = usuarioRepository.findByCorreo(correo);

                // Crear y guardar seguimiento
                Seguimiento nuevo = new Seguimiento();
                nuevo.setArchivo("/uploads/" + nombreArchivo);
                nuevo.setNombreOriginal(nombreOriginal);
                nuevo.setActivo(true);
                if (usuario != null) {
                    nuevo.setUsuarioId(usuario.getId());
                    nuevo.setRolId(usuario.getRol().getId()); // ✅ Correcto
                }

                seguimientoRepository.save(nuevo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/seguimientocrud";
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Seguimiento seguimiento = seguimientoRepository.findById(id).orElse(null);
        model.addAttribute("seguimiento", seguimiento);
        return "seguimientocrud/edit";
    }
    @PostMapping("/eliminar/{id}")
public String eliminar(@PathVariable Long id) {
    Seguimiento seguimiento = seguimientoRepository.findById(id).orElse(null);
    if (seguimiento != null) {
        seguimiento.setActivo(false);
        seguimientoRepository.save(seguimiento);
    }
    return "redirect:/seguimientocrud";
}

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        Seguimiento seguimiento = seguimientoRepository.findById(id).orElse(null);

        if (seguimiento != null && !archivo.isEmpty()) {
            try {
                String nombreOriginal = archivo.getOriginalFilename();
                String nombreArchivo = UUID.randomUUID() + "_" + nombreOriginal;

                String rutaAbsoluta = new File("src/main/resources/static/uploads").getAbsolutePath();
                File directorio = new File(rutaAbsoluta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                Path ruta = Paths.get(rutaAbsoluta, nombreArchivo);
                archivo.transferTo(ruta.toFile());

                seguimiento.setArchivo("/uploads/" + nombreArchivo);
                seguimiento.setNombreOriginal(nombreOriginal);
                seguimientoRepository.save(seguimiento);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/seguimientocrud";
    }
}