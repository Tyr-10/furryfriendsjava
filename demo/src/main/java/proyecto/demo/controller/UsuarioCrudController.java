package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioCrudController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String index(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarioscrud/index"; // ← carpeta + archivo
    }

    @GetMapping("/crear")
    public String crearFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarioscrud/create";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "usuarioscrud/edit";
        } else {
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Usuario usuario) {
        usuario.setId(id);
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }
}