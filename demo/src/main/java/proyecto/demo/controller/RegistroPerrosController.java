package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Perros;
import proyecto.demo.repository.PerrosRepository;

import java.io.IOException;

@Controller
public class RegistroPerrosController {

    @Autowired
    private PerrosRepository perrosRepository;

    @GetMapping("/registroperros")
    public String mostrarFormularioRegistro(Model model,
            @RequestParam(value = "success", required = false) String success) {
        model.addAttribute("perro", new Perros());

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "registroperros";
    }

    @PostMapping("/registroperros")
    public String registrarPerro(@ModelAttribute Perros perro,
            @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
            Model model) {

        if (!imagenArchivo.isEmpty()) {
            try {
                perro.setImagenperro(imagenArchivo.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "❌ Error al subir la imagen.");
                model.addAttribute("perro", perro);
                return "registroperros";
            }
        }

        perro.setUserId(1L); // Cambia esto si usas autenticación
        perro.setDisponible(true); // Valor por defecto

        perrosRepository.save(perro);

        model.addAttribute("success", "🐶 ¡Perro registrado exitosamente!");
        model.addAttribute("perro", new Perros()); // Limpia el formulario
        return "registroperros";
    }

    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable Long id) {
        Perros perro = perrosRepository.findById(id).orElse(null);
        if (perro == null || perro.getImagenperro() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(perro.getImagenperro());
    }
}