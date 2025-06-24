package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;

import java.util.Random;

@Controller
public class RecuperacionController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 👉 Mostrar formulario de recuperación
    @GetMapping("/recuperar")
    public String mostrarFormularioRecuperacion() {
        return "recuperar"; // debe existir recuperar.html en /templates
    }

    // 👉 Procesar el formulario (POST)
    @PostMapping("/recuperar/enviar")
    public String procesarRecuperacion(
            @RequestParam("correo") String correo,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario != null && usuario.isDisponible()) {
            // ✅ Generar nueva contraseña aleatoria
            String nuevaPassword = generarPasswordAleatoria(8);
            String passwordCodificada = passwordEncoder.encode(nuevaPassword);

            usuario.setPassword(passwordCodificada);
            usuarioRepository.save(usuario);

            // ✅ Mostrar mensaje (en un proyecto real se mandaría por email)
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Tu nueva contraseña es: " + nuevaPassword + ". Cámbiala después de iniciar sesión.");
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito",
                "Si el correo está registrado, recibirás una nueva contraseña.");
        }

        return "redirect:/login";
    }

    // 🔒 Generador de contraseña simple
    private String generarPasswordAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            password.append(caracteres.charAt(index));
        }

        return password.toString();
    }
}