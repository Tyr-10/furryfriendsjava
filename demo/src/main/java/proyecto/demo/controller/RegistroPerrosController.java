package proyecto.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Perros;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.PerrosRepository;
import proyecto.demo.repository.UsuarioRepository;

import java.io.IOException;
import java.security.Principal;

@Controller
public class RegistroPerrosController {

    @Autowired
    private PerrosRepository perrosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/registroperros")
    public String mostrarFormularioRegistro(Model model,
            @RequestParam(value = "success", required = false) String success) {
        model.addAttribute("perro", new Perros());

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "registroperros";
    }

    @PostMapping("/registroperros")
    public String registrarPerro(@ModelAttribute Perros perro,
            @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
            Model model,
            Principal principal) {

        if (!imagenArchivo.isEmpty()) {
            try {
                perro.setImagenperro(imagenArchivo.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "❌ Error al subir la imagen.");
                model.addAttribute("perro", perro);
                return "registroperros";
            }
        }

        // Obtener usuario autenticado
        Usuario usuario = obtenerUsuarioDesdePrincipal(principal);
        perro.setUserId(usuario.getId()); // Guarda el ID del usuario que registró el perro
        perro.setDisponible(true); // Estado por defecto

        perrosRepository.save(perro);

        model.addAttribute("success", "🐶 ¡Perro registrado exitosamente!");
        model.addAttribute("perro", new Perros()); // Limpia el formulario
        return "registroperros";
    }

    // Método para obtener el usuario autenticado
    private Usuario obtenerUsuarioDesdePrincipal(Principal principal) {
        if (principal != null) {
            String correo = principal.getName();
            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario != null) {
                return usuario;
            }
        }
        throw new IllegalStateException("No se pudo obtener el usuario autenticado.");
    }
}