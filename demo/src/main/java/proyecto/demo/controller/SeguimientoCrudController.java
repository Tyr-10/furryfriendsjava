package proyecto.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/seguimientocrud")
public class SeguimientoCrudController {

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final String UPLOAD_DIR = "/uploads/";

    @GetMapping
    public String index(Model model) {
        List<Seguimiento> lista = seguimientoRepository.findByActivoTrueOrderByIdDesc();
        model.addAttribute("seguimientos", lista);
        return "seguimientocrud/index";
    }

    @GetMapping("/create")
    public String crearFormulario() {
        return "seguimientocrud/create";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam("nombreOriginal") String nombreOriginal,
            @RequestParam("archivo") MultipartFile archivo,
            HttpSession session
    ) throws IOException {

        if (!archivo.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
            File destino = new File(UPLOAD_DIR + filename);
            archivo.transferTo(destino);

            // Obtener usuario logueado
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            Seguimiento seguimiento = new Seguimiento();
            seguimiento.setArchivo(filename);
            seguimiento.setNombreOriginal(nombreOriginal);
            seguimiento.setUsuarioId(usuario != null ? usuario.getId() : null);
            seguimiento.setRolId(usuario != null ? usuario.getRol().getId() : null);
            seguimiento.setActivo(true);

            seguimientoRepository.save(seguimiento);
        }

        return "redirect:/seguimientocrud";
    }

    @GetMapping("/edit/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Optional<Seguimiento> seguimiento = seguimientoRepository.findById(id);
        seguimiento.ifPresent(s -> model.addAttribute("seguimiento", s));
        return "seguimientocrud/edit";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam("nombreOriginal") String nombreOriginal,
            @RequestParam("archivo") MultipartFile archivo
    ) throws IOException {
        Optional<Seguimiento> seguimientoOpt = seguimientoRepository.findById(id);

        if (seguimientoOpt.isPresent()) {
            Seguimiento seguimiento = seguimientoOpt.get();
            seguimiento.setNombreOriginal(nombreOriginal);

            if (!archivo.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
                File destino = new File(UPLOAD_DIR + filename);
                archivo.transferTo(destino);
                seguimiento.setArchivo(filename);
            }

            seguimientoRepository.save(seguimiento);
        }

        return "redirect:/seguimientocrud";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Optional<Seguimiento> seguimiento = seguimientoRepository.findById(id);
        seguimiento.ifPresent(s -> {
            s.setActivo(false);
            seguimientoRepository.save(s);
        });
        return "redirect:/seguimientocrud";
    }
}