package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Perros;
import proyecto.demo.repository.PerrosRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/perroscrud")
public class PerrosCrudController {

    @Autowired
    private PerrosRepository perrosRepository;

    // Mostrar perros disponibles con filtro por nombre
    @GetMapping
    public String index(@RequestParam(required = false) String nombre, Model model) {
        List<Perros> perros = perrosRepository.buscarPorNombre(nombre);
        model.addAttribute("perros", perros);
        model.addAttribute("nombre", nombre);
        return "perroscrud/index";
    }

    // Formulario para crear nuevo perro
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("perro", new Perros());
        return "perroscrud/create";
    }

    // Guardar nuevo perro
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Perros perro,
            @RequestParam("imagen") MultipartFile imagenFile) throws IOException {
        if (!imagenFile.isEmpty()) {
            perro.setImagenperro(imagenFile.getBytes());
        }

        perro.setDisponible(true);
        perro.setUserId(1L); // Asigna automáticamente el id del usuario creador
        perrosRepository.save(perro);
        return "redirect:/perroscrud";
    }

    // Formulario para editar
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Perros> perroOpt = perrosRepository.findById(id);
        if (perroOpt.isPresent()) {
            model.addAttribute("perro", perroOpt.get());
            return "perroscrud/edit";
        } else {
            return "redirect:/perroscrud";
        }
    }

    // Actualizar perro
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
            @ModelAttribute Perros perroForm,
            @RequestParam("imagen") MultipartFile imagenFile) throws IOException {
        Optional<Perros> perroOpt = perrosRepository.findById(id);

        if (perroOpt.isPresent()) {
            Perros perro = perroOpt.get();
            perro.setNombre(perroForm.getNombre());
            perro.setEdad(perroForm.getEdad());
            perro.setRaza(perroForm.getRaza());
            perro.setTamanio(perroForm.getTamanio());
            perro.setDescripcion(perroForm.getDescripcion());
            perro.setColor(perroForm.getColor());
            perro.setSexo(perroForm.getSexo());
            perro.setHistorialClinico(perroForm.getHistorialClinico());
            perro.setDisponible(perroForm.isDisponible());

            // Si se sube una nueva imagen, actualizarla
            if (!imagenFile.isEmpty()) {
                perro.setImagenperro(imagenFile.getBytes());
            }

            perrosRepository.save(perro);
        }

        return "redirect:/perroscrud";
    }

    // Eliminación lógica
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Optional<Perros> perroOpt = perrosRepository.findById(id);
        if (perroOpt.isPresent()) {
            Perros perro = perroOpt.get();
            perro.setDisponible(false);
            perrosRepository.save(perro);
        }
        return "redirect:/perroscrud";
    }
}