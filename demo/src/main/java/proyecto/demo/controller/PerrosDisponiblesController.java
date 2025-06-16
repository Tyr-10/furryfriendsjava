package proyecto.demo.controller;

import proyecto.demo.model.Perros;
import proyecto.demo.repository.PerrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PerrosDisponiblesController {

    @Autowired
    private PerrosRepository perrosRepository;

    @GetMapping("/perrosdisponibles")
    public String mostrarPerrosDisponibles(
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tamanio,
            @RequestParam(required = false) String descripcion,
            Model model) {

                
        List<Perros> perros = perrosRepository.findByFiltros(edad, color, tamanio, descripcion);
        model.addAttribute("perros", perros);
        return "perrosdisponibles";
    }
}