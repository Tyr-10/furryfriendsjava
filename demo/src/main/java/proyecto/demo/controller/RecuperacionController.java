package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.security.SecureRandom;
import java.util.Random;

@Controller
public class RecuperacionController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // 👉 Mostrar formulario de recuperación
    @GetMapping("/recuperar")
    public String mostrarFormularioRecuperacion() {
        return "recuperar"; // debe existir recuperar.html en /templates
    }

    // 👉 Procesar el formulario (POST)
    @PostMapping("/recuperar/enviar")
    public String procesarRecuperacion(
            @RequestParam("correo") String correo,
            Model model) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario != null && usuario.isDisponible()) {
            // ✅ Generar nueva contraseña aleatoria
            String nuevaPassword = generarPasswordProvisional();
            String passwordCodificada = passwordEncoder.encode(nuevaPassword);

            usuario.setPassword(passwordCodificada);
            usuarioRepository.save(usuario);

            // Enviar correo
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(correo);
            mensaje.setSubject("Recuperación de contraseña - Furry Friends");
            mensaje.setText("Hola,\n\nTu nueva contraseña provisional es: " + nuevaPassword +
                    "\n\nPor favor, inicia sesión y cámbiala lo antes posible.\n\nFurry Friends");

            mailSender.send(mensaje);

            // ✅ Mostrar la nueva contraseña en la misma vista
            model.addAttribute("nuevaPassword", nuevaPassword);
            model.addAttribute("mensajeExito", "La nueva contraseña ha sido enviada a tu correo.");
        } else {
            model.addAttribute("nuevaPassword", null);
            model.addAttribute("mensajeExito", "Si el correo está registrado, recibirás una nueva contraseña.");
        }
        model.addAttribute("correo", correo);
        return "recuperar";
    }

    // 🔒 Generador de contraseña que cumple requisitos
    private String generarPasswordAleatoria(int longitud) {
        String mayus = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minus = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String simbolos = "!@#$%^&.,-_;:";
        String todos = mayus + minus + numeros + simbolos;
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Garantizar al menos un carácter de cada tipo
        password.append(mayus.charAt(random.nextInt(mayus.length())));
        password.append(numeros.charAt(random.nextInt(numeros.length())));
        password.append(simbolos.charAt(random.nextInt(simbolos.length())));
        // El resto aleatorio
        for (int i = 3; i < longitud; i++) {
            password.append(todos.charAt(random.nextInt(todos.length())));
        }
        // Mezclar para que no siempre empiece igual
        char[] arr = password.toString().toCharArray();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
        return new String(arr);
    }

    private String generarPasswordProvisional() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}