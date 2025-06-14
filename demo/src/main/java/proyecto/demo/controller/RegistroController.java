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
    // ============================
    // FORMULARIOS DE REGISTRO
    // ============================

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

    // ============================
    // PROCESAR REGISTROS
    // ============================

    @PostMapping("/adoptante")
    public String registrarAdoptante(@ModelAttribute("usuario") Usuario usuario) {
        Rol rol = rolRepository.findById(1L).orElse(null); // 1 = ADOPTANTE
        if (rol != null) {
            usuario.setRol(rol);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
        }
        return "redirect:/login";
    }

    @PostMapping("/natural")
    public String registrarNatural(@ModelAttribute("usuario") Usuario usuario) {
        Rol rol = rolRepository.findById(2L).orElse(null); // 2 = REFUGIO_NATURAL
        if (rol != null) {
            usuario.setRol(rol);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
        }
        return "redirect:/login";
    }

    @PostMapping("/refugio")
    public String registrarRefugio(@ModelAttribute("usuario") Usuario usuario) {
        Rol rol = rolRepository.findById(3L).orElse(null); // 3 = REFUGIO_FISICO
        if (rol != null) {
            usuario.setRol(rol);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuarioRepository.save(usuario);
        }
        return "redirect:/login";
    }
}
