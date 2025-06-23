package proyecto.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/refugiocrud")
public class RefugioCrudController {

    @Autowired
    private PerrosRepository perrosRepository;

    // INDEX: Mostrar todos los perros del refugio autenticado
    @GetMapping
    public String listarPerros(Model model, HttpSession session) {
        Long refugioId = (Long) session.getAttribute("usuarioId");

        if (refugioId == null) {
            return "redirect:/login"; // o muestra error
        }

        List<Perros> lista = perrosRepository.findByUserId(refugioId);

        // Convertir imagenes byte[] a Base64 para el HTML
        List<Perros> perrosConImagenBase64 = lista.stream().map(perro -> {
            if (perro.getImagenperro() != null && perro.getImagenperro().length > 0) {
                String base64 = Base64.encodeBase64String(perro.getImagenperro());
                perro.setDescripcion(base64); // solo para mostrar imagen
            }
            return perro;
        }).collect(Collectors.toList());

        model.addAttribute("perros", perrosConImagenBase64);
        return "refugiocrud/index";
    }

    // FORMULARIO CREAR
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("perro", new Perros());
        return "refugiocrud/create";
    }

    // GUARDAR NUEVO PERRO
    @PostMapping("/guardar")
    public String guardarPerro(@ModelAttribute Perros perro,
                               @RequestParam("file") MultipartFile imagen,
                               HttpSession session) throws IOException {

        Long refugioId = (Long) session.getAttribute("usuarioId");
        if (refugioId == null) {
            return "redirect:/login"; // o error
        }

        perro.setUserId(refugioId); // ✔ asignar refugio
        perro.setDisponible(true);
        if (!imagen.isEmpty()) {
            perro.setImagenperro(imagen.getBytes());
        }
        perrosRepository.save(perro);
        return "redirect:/refugiocrud";
    }

    // FORMULARIO EDITAR
    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Perros> perro = perrosRepository.findById(id);
        if (perro.isPresent()) {
            model.addAttribute("perro", perro.get());
            return "refugiocrud/edit";
        }
        return "redirect:/refugiocrud";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Perros formPerro,
                             @RequestParam("file") MultipartFile imagen,
                             HttpSession session) throws IOException {

        Optional<Perros> perroOpt = perrosRepository.findById(id);
        if (perroOpt.isPresent()) {
            Perros perro = perroOpt.get();
            perro.setNombre(formPerro.getNombre());
            perro.setEdad(formPerro.getEdad());
            perro.setRaza(formPerro.getRaza());
            perro.setTamanio(formPerro.getTamanio());
            perro.setDescripcion(formPerro.getDescripcion());
            perro.setSexo(formPerro.getSexo());
            perro.setColor(formPerro.getColor());
            perro.setHistorialClinico(formPerro.getHistorialClinico());
            perro.setDisponible(formPerro.isDisponible());

            if (!imagen.isEmpty()) {
                perro.setImagenperro(imagen.getBytes());
            }

            Long refugioId = (Long) session.getAttribute("usuarioId");
            perro.setUserId(refugioId); // ✔ importante mantener esto

            perrosRepository.save(perro);
        }
        return "redirect:/refugiocrud";
    }

    // ELIMINACIÓN LÓGICA
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        Optional<Perros> perro = perrosRepository.findById(id);
        if (perro.isPresent()) {
            perro.get().setDisponible(false);
            perrosRepository.save(perro.get());
        }
        return "redirect:/refugiocrud";
    }
}