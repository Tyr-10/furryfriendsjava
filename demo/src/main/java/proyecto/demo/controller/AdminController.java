package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/tablasadmin")
    public String verPanelAdmin() {
        return "tablasadmin"; // Vista principal de administración
    }

    @GetMapping("/usuarioscrud")
    public String verUsuariosCrud() {
        return "usuarioscrud"; // Vista de usuarios
    }

    @GetMapping("/perroscrud")
    public String verPerrosCrud() {
        return "perroscrud"; // Vista de perros
    }

    @GetMapping("/seguimientocrud")
    public String verSeguimientoCrud() {
        return "seguimientocrud"; // Vista de seguimiento de visitas
    }

    @GetMapping("/viabilidadcrud")
    public String verViabilidadCrud() {
        return "viabilidadcrud"; // Vista de viabilidad de estudio
    }
}