package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/tablasadmin")
    public String verPanelAdmin() {
        return "tablasadmin"; // Vista principal de administración
    }

}