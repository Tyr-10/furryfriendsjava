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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Controller
public class RegistroPerrosController {

    @Autowired
    private PerrosRepository perrosRepository;

    @GetMapping("/registroperros")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("perro", new Perros());
        return "registroperros";
    }

    @PostMapping("/registroperros")
    public String registrarPerro(@ModelAttribute Perros perro,
                                 @RequestParam("imagenArchivo") MultipartFile imagenArchivo,
                                 Model model) {

        // Verificar si la imagen no está vacía
        if (!imagenArchivo.isEmpty()) {
            try {
                // Crear nombre original y ruta
                String originalFilename = imagenArchivo.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String nombreEncriptado = sha256(UUID.randomUUID().toString() + originalFilename) + extension;

                String ruta = "src/main/resources/static/imagenes/";
                File directorio = new File(ruta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }

                imagenArchivo.transferTo(new File(ruta + nombreEncriptado));
                perro.setImagenperro(nombreEncriptado);

            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                model.addAttribute("error", "❌ Error al subir la imagen.");
                return "registroperros";
            }
        }

        perro.setDisponible(true);
        perro.setUserId(1L); // cambia esto si usas sesión

        perrosRepository.save(perro);

        return "redirect:/vistarefugio?success=🐶%20¡Perro%20registrado%20exitosamente!";
    }

    // Método para encriptar usando SHA-256
    private String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());

        // Convertir bytes a hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}