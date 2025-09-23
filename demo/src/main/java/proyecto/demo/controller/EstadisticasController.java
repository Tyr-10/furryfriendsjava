package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;

import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.repository.PerrosRepository;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Controller
public class EstadisticasController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerrosRepository perrosRepository;

    // Vista HTML de estadísticas
    @GetMapping("/estadisticas")
    public String mostrarVistaEstadisticas(Model model) {
        long usuariosTotales = usuarioRepository.count();
        long perrosTotales = perrosRepository.count();

        model.addAttribute("usuariosTotales", usuariosTotales);
        model.addAttribute("perrosTotales", perrosTotales);

        return "estadisticas";  // estadisticas.html en templates
    }

    // Nuevo: generar PDF de estadísticas
    @GetMapping("/estadisticas/reporte")
    public void generarReporteEstadisticas(HttpServletResponse response) throws Exception {
        long usuariosTotales = usuarioRepository.count();
        long perrosTotales = perrosRepository.count();

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        // Encabezado con logo y título
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
                float y = document.top() + 18;

                if (logoImg != null) {
                    logoImg.setAbsolutePosition(document.left(), y - 12);
                    try {
                        cb.addImage(logoImg);
                    } catch (Exception ignored) {}
                }

                ColumnText.showTextAligned(
                    cb,
                    Element.ALIGN_CENTER,
                    new Phrase("Furry Friends - Reporte de Estadísticas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)),
                    (document.right() + document.left()) / 2,
                    y,
                    0
                );
            }
        });

        document.setMargins(document.leftMargin(), document.rightMargin(), 70f, document.bottomMargin());
        document.open();

        // Contenido del PDF
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Reporte General de Estadísticas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("\nFecha de generación: " + java.time.LocalDate.now() + "\n\n"));

        document.add(new Paragraph("Usuarios registrados en el sistema: " + usuariosTotales));
        document.add(new Paragraph("Perros registrados en el sistema: " + perrosTotales));

        document.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_estadisticas.pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }
}
