package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Usuario;
import proyecto.demo.model.Viabilidad;
import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.repository.ViabilidadRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/viabilidadcrud")
public class ViabilidadCrudController {

    @Autowired
    private ViabilidadRepository viabilidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String index(Model model) {
        List<Viabilidad> viabilidades = viabilidadRepository.findByActivoTrueOrderByIdDesc();
        model.addAttribute("viabilidades", viabilidades);
        return "viabilidadcrud/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear() {
        return "viabilidadcrud/create";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("archivo") MultipartFile archivo) {
        if (!archivo.isEmpty()) {
            try {
                String nombreOriginal = archivo.getOriginalFilename();
                String nombreArchivo = UUID.randomUUID() + "_" + nombreOriginal;

                // Guardar en /static/uploads
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

                Viabilidad nueva = new Viabilidad();
                nueva.setArchivo("/uploads/" + nombreArchivo);
                nueva.setNombreOriginal(nombreOriginal);
                nueva.setActivo(true);
                if (usuario != null) {
                    nueva.setUsuarioId(usuario.getId());
                    nueva.setRolId(usuario.getRol().getId());
                }

                viabilidadRepository.save(nueva);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/viabilidadcrud";
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Viabilidad viabilidad = viabilidadRepository.findById(id).orElse(null);
        model.addAttribute("viabilidad", viabilidad);
        return "viabilidadcrud/edit";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        Viabilidad viabilidad = viabilidadRepository.findById(id).orElse(null);

        if (viabilidad != null && !archivo.isEmpty()) {
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

                viabilidad.setArchivo("/uploads/" + nombreArchivo);
                viabilidad.setNombreOriginal(nombreOriginal);
                viabilidadRepository.save(viabilidad);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "redirect:/viabilidadcrud";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Viabilidad viabilidad = viabilidadRepository.findById(id).orElse(null);
        if (viabilidad != null) {
            viabilidad.setActivo(false);
            viabilidadRepository.save(viabilidad);
        }
        return "redirect:/viabilidadcrud";
    }
}