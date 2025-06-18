package proyecto.demo.controller;

import proyecto.demo.model.Viabilidad;
import proyecto.demo.repository.ViabilidadRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class ViabilidadController {

    @Autowired
    private ViabilidadRepository repo;

    @GetMapping("/viabilidad")
    public String mostrarVista() {
        return "viabilidad";
    }

    @PostMapping("/viabilidad/subir")
    public String subirDocumento(@RequestParam("archivo") MultipartFile archivo, Model model) {
        try {
            String nombre = StringUtils.cleanPath(archivo.getOriginalFilename());
            Viabilidad doc = new Viabilidad();
            doc.setNombre(nombre);
            doc.setTipo(archivo.getContentType());
            doc.setContenido(archivo.getBytes()); // Asegúrate que el nombre del método sea correcto
            repo.save(doc);
            model.addAttribute("mensaje", "Documento subido correctamente.");
        } catch (IOException e) {
            model.addAttribute("mensaje", "Error al subir documento.");
        }
        return "viabilidad";
    }

    @GetMapping("/viabilidad/buscar")
    public String buscarDocumento(@RequestParam("nombre") String nombre, Model model) {
        Optional<Viabilidad> resultado = repo.findByNombre(nombre);
        if (resultado.isPresent()) {
            model.addAttribute("viabilidadEncontrada", resultado.get());
        } else {
            model.addAttribute("mensaje", "No se encontraron resultados.");
        }
        return "viabilidad";
    }

    @GetMapping("/viabilidad/descargar/{id}")
    public void descargarDocumento(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Viabilidad> resultado = repo.findById(id);
        if (resultado.isPresent()) {
            Viabilidad documento = resultado.get();
            response.setContentType(documento.getTipo());
            String headerValue = "attachment; filename=\"" +
                    URLEncoder.encode(documento.getNombre(), StandardCharsets.UTF_8) + "\"";
            response.setHeader("Content-Disposition", headerValue);
            response.getOutputStream().write(documento.getContenido());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado");
        }
    }
}