package proyecto.demo.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.ColumnText;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyecto.demo.model.Perros;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.PerrosRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if (refugio == null)
            return "redirect:/login";

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
        if (refugio == null)
            return "redirect:/login";

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
        if (refugio == null)
            return "redirect:/login";

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
        if (refugio == null)
            return "redirect:/login";

        Optional<Perros> perroOpt = perrosRepository.findById(id);
        if (perroOpt.isPresent() && perroOpt.get().getUserId().equals(refugio.getId())) {
            Perros perro = perroOpt.get();
            perro.setDisponible(false); // eliminación lógica
            perrosRepository.save(perro);
        }

        return "redirect:/refugiocrud";
    }

    @GetMapping("/reporte")
    public void generarReporte(HttpSession session, HttpServletResponse response) throws Exception {
        Usuario refugio = (Usuario) session.getAttribute("usuarioLogueado");
        if (refugio == null) {
            response.sendRedirect("/login");
            return;
        }

        List<Perros> perros = perrosRepository.findByUserIdAndDisponibleTrue(refugio.getId());

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Pie de página personalizado con nombre de empresa y logo alineado al título
        writer.setPageEvent(new PdfPageEventHelper() {
            Image logoImg = null;
            {
                try {
                    InputStream logoStream = new ClassPathResource("static/images/logo.jpg").getInputStream();
                    logoImg = Image.getInstance(logoStream.readAllBytes());
                    logoImg.scaleToFit(40, 40);
                } catch (Exception e) {
                    logoImg = null;
                }
            }

            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                float y = document.top() + 18; // más arriba para el título
                // Logo alineado a la altura del título
                if (logoImg != null) {
                    logoImg.setAbsolutePosition(document.left(), y - 12);
                    try {
                        cb.addImage(logoImg);
                    } catch (Exception ignored) {
                    }
                }
                // Nombre de la empresa centrado más arriba
                com.lowagie.text.pdf.ColumnText.showTextAligned(
                        cb,
                        Element.ALIGN_CENTER,
                        new Phrase("Furry Friends", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)),
                        (document.right() + document.left()) / 2,
                        y,
                        0);
            }
        });

        // Margen superior ajustado para dejar espacio solo necesario
        document.setMargins(document.leftMargin(), document.rightMargin(), 70f, document.bottomMargin());

        document.open();

        // Solo un salto de línea para separar del encabezado
        document.add(new Paragraph(" "));

        document.add(
                new Paragraph("Reporte de Perros Registrados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("Refugio: " + refugio.getNombre() + " (" + refugio.getCorreo() + ")\n\n"));

        // Espacio antes de la información de los perros
        document.add(new Paragraph(" "));

        for (Perros p : perros) {
            // Imagen del perro (solo si es una imagen válida)
            if (p.getImagenperro() != null && p.getImagenperro().length > 0) {
                try {
                    Image img = Image.getInstance(p.getImagenperro());
                    img.scaleToFit(100, 100);
                    img.setSpacingBefore(10f); // espacio suficiente para no tapar el logo/título
                    document.add(img);
                } catch (Exception ex) {
                    // Si la imagen no es válida, ignora y sigue con el resto del reporte
                }
            }
            document.add(new Paragraph(
                    "Nombre: " + p.getNombre() +
                            "\nEdad: " + p.getEdad() +
                            "\nRaza: " + p.getRaza() +
                            "\nTamaño: " + p.getTamanio() +
                            "\nColor: " + p.getColor() +
                            "\nSexo: " + p.getSexo() +
                            "\nDescripción: " + p.getDescripcion() +
                            "\nHistorial Clínico: " + p.getHistorialClinico()));
            document.add(new Paragraph(" "));
        }

        document.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_perros.pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }
}