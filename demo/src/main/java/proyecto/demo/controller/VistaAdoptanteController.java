package proyecto.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaAdoptanteController {

    @GetMapping("/vistaadoptante")
    public String mostrarVistaAdoptante() {
        return "vistaadoptante"; // Debe coincidir con el nombre del archivo HTML en la carpeta 'templates'
    }
}