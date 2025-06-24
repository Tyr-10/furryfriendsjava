package proyecto.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Perros;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.PerrosRepository;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/refugiocrud")
public class RefugioCrudController {

    @Autowired
    private PerrosRepository perrosRepository;

    @GetMapping
    public String index(Model model, HttpSession session) {
        Usuario refugio = (Usuario) session.getAttribute("usuarioLogueado");
        if (refugio == null) {
            return "redirect:/login";
        }

        List<Perros> perros = perrosRepository.findByUserId(refugio.getId());

        // Convertir imágenes a base64
        Map<Long, String> imagenesBase64 = new HashMap<>();
        for (Perros p : perros) {
            if (p.getImagenperro() != null) {
                String base64 = Base64.getEncoder().encodeToString(p.getImagenperro());
                imagenesBase64.put(p.getId(), base64);
            }
        }

        model.addAttribute("perros", perros);
        model.addAttribute("imagenes", imagenesBase64);

        return "refugiocrud/index";
    }

    @PostMapping("/guardar")
    public String guardarPerro(@ModelAttribute Perros perro,
                               @RequestParam("imagenFile") MultipartFile imagenFile,
                               HttpSession session) throws IOException {
        Usuario refugio = (Usuario) session.getAttribute("usuarioLogueado");
        if (refugio == null) return "redirect:/login";

        if (!imagenFile.isEmpty()) {
            perro.setImagenperro(imagenFile.getBytes());
        }

        perro.setUserId(refugio.getId());
        perrosRepository.save(perro);
        return "redirect:/refugiocrud";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, HttpSession session) {
        Usuario refugio = (Usuario) session.getAttribute("usuarioLogueado");
        if (refugio == null) return "redirect:/login";

        Optional<Perros> perroOpt = perrosRepository.findById(id);
        if (perroOpt.isPresent() && perroOpt.get().getUserId().equals(refugio.getId())) {
            model.addAttribute("perro", perroOpt.get());
            model.addAttribute("tamanos", List.of("Grande", "Mediano", "Pequeño"));
            model.addAttribute("sexos", List.of("Hembra", "Macho"));
            return "refugiocrud/edit";
        }

        return "redirect:/refugiocrud";
    }

    @PostMapping("/actualizar")
    public String actualizarPerro(@ModelAttribute Perros perro,
                                  @RequestParam("imagenFile") MultipartFile imagenFile,
                                  HttpSession session) throws IOException {
        Usuario refugio = (Usuario) session.getAttribute("usuarioLogueado");
        if (refugio == null) return "redirect:/login";

        Optional<Perros> perroExistente = perrosRepository.findById(perro.getId());
        if (perroExistente.isPresent() && perroExistente.get().getUserId().equals(refugio.getId())) {
            Perros actualizado = perroExistente.get();
            actualizado.setNombre(perro.getNombre());
            actualizado.setEdad(perro.getEdad());
            actualizado.setTamanio(perro.getTamanio());
            actualizado.setColor(perro.getColor());
            actualizado.setSexo(perro.getSexo());
            actualizado.setDescripcion(perro.getDescripcion());
            actualizado.setDisponible(perro.isDisponible());

            if (!imagenFile.isEmpty()) {
                actualizado.setImagenperro(imagenFile.getBytes());
            }

            perrosRepository.save(actualizado);
        }

        return "redirect:/refugiocrud";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPerro(@PathVariable Long id, HttpSession session) {
        Usuario refugio = (Usuario) session.getAttribute("usuarioLogueado");
        if (refugio == null) return "redirect:/login";

        Optional<Perros> perroOpt = perrosRepository.findById(id);
        if (perroOpt.isPresent() && perroOpt.get().getUserId().equals(refugio.getId())) {
            Perros perro = perroOpt.get();
            perro.setDisponible(false); // eliminación lógica
            perrosRepository.save(perro);
        }

        return "redirect:/refugiocrud";
    }
}