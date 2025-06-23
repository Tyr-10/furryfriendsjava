package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.demo.model.Seguimiento;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.SeguimientoRepository;
import proyecto.demo.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/seguimientocrud")
public class SeguimientoCrudController {

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Mostrar solo seguimientos activos
    @GetMapping
    public String index(Model model) {
        List<Seguimiento> seguimientos = seguimientoRepository.findByActivoTrueOrderByIdDesc(); // ✅ Correcto
        model.addAttribute("seguimientos", seguimientos);
        return "seguimientocrud/index";
    }

    // ✅ Formulario para crear seguimiento
    @GetMapping("/create")
    public String crearFormulario(Model model) {
        model.addAttribute("seguimiento", new Seguimiento());
        model.addAttribute("usuarios", usuarioRepository.findByDisponibleTrue());
        return "seguimientocrud/create";
    }

    // ✅ Guardar nuevo seguimiento
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Seguimiento seguimiento) {
        seguimiento.setActivo(true); // Activo por defecto
        seguimientoRepository.save(seguimiento);
        return "redirect:/seguimientocrud";
    }

    // ✅ Formulario para editar seguimiento
    @GetMapping("/edit/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Optional<Seguimiento> seguimiento = seguimientoRepository.findById(id);
        if (seguimiento.isPresent()) {
            model.addAttribute("seguimiento", seguimiento.get());
            model.addAttribute("usuarios", usuarioRepository.findByDisponibleTrue());
            return "seguimientocrud/edit";
        } else {
            return "redirect:/seguimientocrud";
        }
    }

    // ✅ Actualizar seguimiento
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Seguimiento seguimientoForm) {
        Optional<Seguimiento> seguimientoBD = seguimientoRepository.findById(id);
        if (seguimientoBD.isPresent()) {
            Seguimiento seguimiento = seguimientoBD.get();
            seguimiento.setArchivo(seguimientoForm.getArchivo());
            seguimiento.setNombreOriginal(seguimientoForm.getNombreOriginal());
            seguimiento.setRolId(seguimientoForm.getRolId());
            seguimiento.setUsuarioId(seguimientoForm.getUsuarioId());
            seguimiento.setActivo(seguimientoForm.getActivo());

            seguimientoRepository.save(seguimiento);
        }
        return "redirect:/seguimientocrud";
    }

    // ✅ Eliminar lógicamente
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Optional<Seguimiento> seguimientoOpt = seguimientoRepository.findById(id);
        if (seguimientoOpt.isPresent()) {
            Seguimiento seguimiento = seguimientoOpt.get();
            seguimiento.setActivo(false);
            seguimientoRepository.save(seguimiento);
        }
        return "redirect:/seguimientocrud";
    }
}