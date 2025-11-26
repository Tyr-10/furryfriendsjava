package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/usuarioscrud")
public class UsuarioCrudController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ✅ Mostrar solo usuarios disponibles con filtro de búsqueda
    @GetMapping
    public String index(@RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String correo,
                        Model model) {
        List<Usuario> usuarios = usuarioRepository.buscarPorNombreYCorreo(nombre, correo);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        return "usuarioscrud/index";
    }

    // Formulario para crear usuario
    @GetMapping("/create")
    public String crearFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarioscrud/create";
    }

    // Guardar usuario nuevo
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario, Model model) {
        // Validaciones server-side
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|co)$", Pattern.CASE_INSENSITIVE);
        Pattern pwdPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

        boolean hasError = false;
        if (usuario.getCorreo() == null || !emailPattern.matcher(usuario.getCorreo()).matches()) {
            model.addAttribute("errorEmail", "El correo debe ser válido y terminar en .com o .co");
            hasError = true;
        }

        if (usuario.getPassword() == null || !pwdPattern.matcher(usuario.getPassword()).matches()) {
            model.addAttribute("errorPassword", "La contraseña debe tener mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 símbolo.");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("usuario", usuario);
            return "usuarioscrud/create";
        }

        // Encriptar la contraseña antes de guardar
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encryptedPassword);

        usuarioRepository.save(usuario);
        return "redirect:/usuarioscrud";
    }

    // Formulario de edición
    @GetMapping("/edit/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "usuarioscrud/edit";
        } else {
            return "redirect:/usuarioscrud";
        }
    }

    // Actualizar usuario
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id, @ModelAttribute Usuario usuarioForm) {
        Optional<Usuario> usuarioBD = usuarioRepository.findById(id);

        if (usuarioBD.isPresent()) {
            Usuario usuario = usuarioBD.get();

            usuario.setNombre(usuarioForm.getNombre());
            usuario.setCorreo(usuarioForm.getCorreo());
            usuario.setTelefono(usuarioForm.getTelefono());
            usuario.setDireccion(usuarioForm.getDireccion());
            usuario.setCiudad(usuarioForm.getCiudad());
            usuario.setCapacidad(usuarioForm.getCapacidad());
            usuario.setHorarios(usuarioForm.getHorarios());
            usuario.setResponsable(usuarioForm.getResponsable());
            usuario.setServicios(usuarioForm.getServicios());
            usuario.setDisponible(usuarioForm.isDisponible());
            usuario.setRol(usuarioForm.getRol());

            // Solo encriptar si hay nueva contraseña
            if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isBlank()) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String encryptedPassword = passwordEncoder.encode(usuarioForm.getPassword());
                usuario.setPassword(encryptedPassword);
            }

            usuarioRepository.save(usuario);
        }

        return "redirect:/usuarioscrud";
    }

    // ✅ Eliminar lógicamente (no borrar de base de datos)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setDisponible(false); // 🔴 marcar como no disponible
            usuarioRepository.save(usuario);
        }
        return "redirect:/usuarioscrud";
    }
}