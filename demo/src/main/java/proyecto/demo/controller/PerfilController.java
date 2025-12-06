package proyecto.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import java.util.Collections;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm,
                                   @RequestParam(required = false) String nuevaPassword,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   HttpSession session,
                                   Model model) {

        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername());

        usuario.setNombre(usuarioForm.getNombre());
        usuario.setCorreo(usuarioForm.getCorreo());
        usuario.setTelefono(usuarioForm.getTelefono());
        usuario.setDireccion(usuarioForm.getDireccion());

        // Validar nueva contraseña si se proporciona
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            String passwordValidationError = validarContraseña(nuevaPassword);
            if (passwordValidationError != null) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("error", passwordValidationError);
                return "perfil";
            }
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        usuarioRepository.save(usuario);

        // Si se cambió el correo, actualizar la Authentication en el SecurityContext
        String oldCorreo = userDetails.getUsername();
        if (oldCorreo != null && !oldCorreo.equals(usuario.getCorreo())) {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = currentAuth != null ? currentAuth.getAuthorities() : Collections.emptyList();

            // Crear nuevo principal con el nuevo correo y la contraseña (ya codificada)
            User newPrincipal = new User(usuario.getCorreo(), usuario.getPassword(), authorities != null ? authorities : Collections.emptyList());

            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(newPrincipal, usuario.getPassword(), newPrincipal.getAuthorities());

            if (currentAuth != null) {
                newAuth.setDetails(currentAuth.getDetails());
            }
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        session.setAttribute("success", "Perfil actualizado exitosamente.");
        return "redirect:/perfil";
    }

    private String validarContraseña(String password) {
        if (password.length() < 8) {
            return "La contraseña debe tener mínimo 8 caracteres";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una mayúscula";
        }
        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una minúscula";
        }
        if (!password.matches(".*[0-9].*")) {
            return "La contraseña debe contener al menos un número";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "La contraseña debe contener al menos un símbolo especial (!@#$%^&*)";
        }
        return null; // Sin errores
    }

    @PostMapping("/eliminar")
    public String eliminarPerfil(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername());
        usuario.setDisponible(false); // Cambiar estado a no disponible
        usuarioRepository.save(usuario);

        session.setAttribute("mensajeDesactivado", "Cuenta desactivada correctamente.");
        return "redirect:/"; // Redirige a index.html
    }
}