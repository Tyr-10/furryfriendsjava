package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import proyecto.demo.model.Perros;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.PerrosRepository;
import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.service.EmailService;

import java.util.List;

@Controller
public class SolicitudAdopcionController {

    @Autowired
    private PerrosRepository perrosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/solicitud-adopcion")
    public String solicitarAdopcion(@RequestParam("perroId") Long perroId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Perros perro = perrosRepository.findById(perroId).orElse(null);
        List<Perros> perros = perrosRepository.findByFiltros(null, null, null, null);
        if (perro == null) {
            model.addAttribute("mensajeError", "Perro no encontrado.");
            model.addAttribute("perros", perros);
            return "perrosdisponibles";
        }

        Usuario refugio = null;
        if (perro.getUserId() != null) {
            refugio = usuarioRepository.findById(perro.getUserId()).orElse(null);
        }
        if (refugio == null) {
            model.addAttribute("mensajeError", "Refugio no encontrado.");
            model.addAttribute("perros", perros);
            return "perrosdisponibles";
        }

        Usuario adoptante = usuarioRepository.findByCorreo(userDetails.getUsername());
        if (adoptante == null) {
            model.addAttribute("mensajeError", "Usuario no encontrado.");
            model.addAttribute("perros", perros);
            return "perrosdisponibles";
        }

        String asunto = "Solicitud de adopción para " + perro.getNombre();
        String cuerpo = "Hola,\n\n" +
                "El usuario " + adoptante.getNombre() + " (" + adoptante.getCorreo() + ") " +
                "está interesado en adoptar a tu perro llamado \"" + perro.getNombre() + "\".\n\n" +
                "Por favor, ponte en contacto con el adoptante para continuar el proceso de adopción.\n\n" +
                "¡Gracias por usar Furry Friends!";

        try {
            emailService.enviarCorreo(refugio.getCorreo(), asunto, cuerpo);
            model.addAttribute("mensajeExito",
                    "¡Se ha enviado tu solicitud al refugio encargado! Pronto se pondrán en contacto contigo.");
            model.addAttribute("adoptanteCorreo", adoptante.getCorreo());
            model.addAttribute("adoptanteNombre", adoptante.getNombre());
            model.addAttribute("perros", perrosRepository.findByFiltros(null, null, null, null));
            return "perrosdisponibles";
        } catch (Exception e) {
            model.addAttribute("mensajeError", "Error al enviar el correo. Intenta más tarde.");
        }
        model.addAttribute("perros", perros);
        return "perrosdisponibles";
    }
}