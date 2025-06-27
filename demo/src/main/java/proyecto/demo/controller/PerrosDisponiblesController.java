package proyecto.demo.controller;

import proyecto.demo.model.Perros;
import proyecto.demo.repository.PerrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.List;

@Controller
public class PerrosDisponiblesController {

    @Autowired
    private PerrosRepository perrosRepository;

    @GetMapping("/perrosdisponibles")
    public String mostrarPerrosDisponibles(
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tamanio,
            @RequestParam(required = false) String descripcion,
            Model model) {

        List<Perros> perros = perrosRepository.findByFiltros(edad, color, tamanio, descripcion);
        model.addAttribute("perros", perros);
        return "perrosdisponibles";
    }

    // Este es el único endpoint que debe existir para mostrar la imagen del perro
    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagenPerro(@PathVariable Long id) {
        Perros perro = perrosRepository.findById(id).orElse(null);
        if (perro != null && perro.getImagenperro() != null && perro.getImagenperro().length > 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(perro.getImagenperro());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}