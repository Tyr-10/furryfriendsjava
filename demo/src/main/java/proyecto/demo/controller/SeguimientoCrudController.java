package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import proyecto.demo.model.Seguimiento;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.SeguimientoRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    // Mostrar todos los seguimientos
    @GetMapping
    public String index(Model model) {
        List<Seguimiento> seguimientos = seguimientoRepository.findByActivoTrueOrderByIdDesc();
        model.addAttribute("seguimientos", seguimientos);
        return "seguimientocrud/index";
    }

    // Formulario para crear nuevo seguimiento
    @GetMapping("/crear")
    public String crear() {
        return "seguimientocrud/create";
    }

    // Guardar nuevo seguimiento
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
                Usuario usuario = (Usuario) auth.getPrincipal();

                Seguimiento nuevo = new Seguimiento();
                nuevo.setArchivo("/uploads/" + nombreArchivo);
                nuevo.setNombreOriginal(nombreOriginal);
                nuevo.setActivo(true);
                nuevo.setUsuarioId(usuario.getId());
                nuevo.setRolId(usuario.getRol().getId());

                seguimientoRepository.save(nuevo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/seguimientocrud";
    }

    // Formulario para editar seguimiento
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Seguimiento seguimiento = seguimientoRepository.findById(id).orElse(null);
        model.addAttribute("seguimiento", seguimiento);
        return "seguimientocrud/edit";
    }

    // Actualizar seguimiento
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        Seguimiento seguimiento = seguimientoRepository.findById(id).orElse(null);

        if (seguimiento != null && !archivo.isEmpty()) {
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
                Usuario usuario = (Usuario) auth.getPrincipal();

                seguimiento.setArchivo("/uploads/" + nombreArchivo);
                seguimiento.setNombreOriginal(nombreOriginal);
                seguimiento.setUsuarioId(usuario.getId());
                seguimiento.setRolId(usuario.getRol().getId());

                seguimientoRepository.save(seguimiento);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/seguimientocrud";
    }

    // Eliminar (desactivar) seguimiento
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Seguimiento seguimiento = seguimientoRepository.findById(id).orElse(null);
        if (seguimiento != null) {
            seguimiento.setActivo(false);
            seguimientoRepository.save(seguimiento);
        }
        return "redirect:/seguimientocrud";
    }
}