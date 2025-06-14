package proyecto.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CuidadosController {

    @GetMapping("/cuidados")
    public String mostrarCuidados() {
        return "cuidados"; // nombre del archivo HTML sin extensión
}
}