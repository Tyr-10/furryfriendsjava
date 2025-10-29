package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import proyecto.demo.model.Usuario;
import proyecto.demo.model.Rol;
import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.repository.RolRepository;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String mostrarSeleccionRol() {
        return "registro";
    }

    @GetMapping("/adoptante")
    public String mostrarFormularioAdoptante(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registeradoptante";
    }

    @GetMapping("/natural")
    public String mostrarFormularioNatural(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registernatural";
    }

    @GetMapping("/refugio")
    public String mostrarFormularioRefugio(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registerrefugio";
    }

    private boolean validarPassword(String password) {
        // Al menos 8 caracteres, una mayúscula, un número y un símbolo
        return password != null && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&.,\\-_;:]).{8,}$");
    }

    @PostMapping("/adoptante")
    public String registrarAdoptante(@ModelAttribute("usuario") Usuario usuario, @RequestParam String passwordConfirm, Model model) {
        if (!usuario.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registeradoptante";
        }
        if (!validarPassword(usuario.getPassword())) {
            model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un símbolo.");
            return "registeradoptante";
        }
        Rol rol = rolRepository.findById(1L).orElse(null);
        if (rol != null) {
            usuario.setRol(rol);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
            return "redirect:/login";
        }
        model.addAttribute("error", "Error al asignar el rol.");
        return "registeradoptante";
    }

    @PostMapping("/natural")
    public String registrarNatural(@ModelAttribute("usuario") Usuario usuario, @RequestParam String passwordConfirm, Model model) {
        if (!usuario.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registernatural";
        }
        if (!validarPassword(usuario.getPassword())) {
            model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un símbolo.");
            return "registernatural";
        }
        Rol rol = rolRepository.findById(2L).orElse(null);
        if (rol != null) {
            usuario.setRol(rol);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
            return "redirect:/login";
        }
        model.addAttribute("error", "Error al asignar el rol.");
        return "registernatural";
    }

    @PostMapping("/refugio")
    public String registrarRefugio(@ModelAttribute("usuario") Usuario usuario, @RequestParam String passwordConfirm, Model model) {
        if (!usuario.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registerrefugio";
        }
        if (!validarPassword(usuario.getPassword())) {
            model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un símbolo.");
            return "registerrefugio";
        }
        Rol rol = rolRepository.findById(3L).orElse(null);
        if (rol != null) {
            usuario.setRol(rol);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
            return "redirect:/login";
        }
        model.addAttribute("error", "Error al asignar el rol.");
        return "registerrefugio";
    }
    
}