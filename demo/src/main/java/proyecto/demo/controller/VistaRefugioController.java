package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaRefugioController {

    @GetMapping("/vistarefugio")
    public String mostrarVistaRefugio() {
        return "vistarefugio"; // El nombre debe coincidir exactamente con el archivo HTML en la carpeta
                               // templates
    }
}