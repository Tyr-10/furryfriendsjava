package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaAdminController {

    @GetMapping("/vistaadmin")
    public String mostrarVistaAdmin() {
        return "vistaadmin"; // Este debe coincidir con el nombre del archivo HTML: admin.html
    }
}

