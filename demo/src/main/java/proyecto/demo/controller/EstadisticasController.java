package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.repository.PerrosRepository;

@Controller
public class EstadisticasController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerrosRepository perrosRepository;

    // Muestra la vista HTML que contiene la imagen generada
    @GetMapping("/estadisticas")
    public String mostrarVistaEstadisticas(Model model) {
        long usuariosTotales = usuarioRepository.count();
        long perrosTotales = perrosRepository.count();

        model.addAttribute("usuariosTotales", usuariosTotales);
        model.addAttribute("perrosTotales", perrosTotales);

        return "estadisticas"; // estadisticas.html en templates
    }
}