package proyecto.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername());
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm, @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername());
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setCorreo(usuarioForm.getCorreo());
        usuario.setTelefono(usuarioForm.getTelefono());
        usuario.setDireccion(usuarioForm.getDireccion());
        usuarioRepository.save(usuario);
        session.setAttribute("success", "Perfil actualizado exitosamente.");
        return "redirect:/perfil";
    }

    @PostMapping("/eliminar")
    public String eliminarPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername());
        usuarioRepository.delete(usuario);
        return "redirect:/logout";
    }
}