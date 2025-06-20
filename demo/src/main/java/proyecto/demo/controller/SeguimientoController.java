package proyecto.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import proyecto.demo.model.Seguimiento;
import proyecto.demo.repository.SeguimientoRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seguimiento")
public class SeguimientoController {

    private final SeguimientoRepository seguimientoRepository;

    @GetMapping
    public String mostrarSeguimientos(Model model) {
        List<Seguimiento> seguimientos = seguimientoRepository.findAll();
        model.addAttribute("seguimientos", seguimientos);
        return "seguimiento";
    }
}