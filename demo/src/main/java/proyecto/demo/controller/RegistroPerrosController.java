package proyecto.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Perros;
import proyecto.demo.repository.PerrosRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class RegistroPerrosController {

    @Autowired
    private PerrosRepository perrosRepository;

    @GetMapping("/registroperros")
    public String mostrarFormularioRegistro(Model model, @RequestParam(value = "success", required = false) String success) {
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

        try {
            if (!imagenArchivo.isEmpty()) {
                String nombreImagen = UUID.randomUUID().toString() + "_" + imagenArchivo.getOriginalFilename();
                String ruta = "src/main/resources/static/imagenes/";
                File directorio = new File(ruta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                imagenArchivo.transferTo(new File(ruta + nombreImagen));
                perro.setImagenperro(nombreImagen);
            }

            perro.setUserId(1L); // Reemplaza con el usuario autenticado si lo necesitas
            perro.setDisponible(true); // Siempre disponible al registrar

            perrosRepository.save(perro);

            return "redirect:/registroperros?success=🐶%20¡Perro%20registrado%20exitosamente!";

        } catch (IOException e) {
            model.addAttribute("error", "❌ Error al subir la imagen.");
            return "registroperros";
        }
    }
}