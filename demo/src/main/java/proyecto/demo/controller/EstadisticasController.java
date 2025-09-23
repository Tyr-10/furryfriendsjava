package proyecto.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;

import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.repository.PerrosRepository;
import proyecto.demo.model.Usuario;
import proyecto.demo.model.Perros;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

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

        return "estadisticas";
    }

    // Generar PDF
    @GetMapping("/estadisticas/reporte")
    public void generarReporteEstadisticas(HttpServletResponse response) throws Exception {
        long usuariosTotales = usuarioRepository.count();
        long perrosTotales = perrosRepository.count();

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Perros> perros = perrosRepository.findAll();

        Document document = new Document(PageSize.A4.rotate()); // Horizontal para que quepa mejor
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

        // Título
        document.add(new Paragraph("Reporte General de Estadísticas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        document.add(new Paragraph("\nFecha de generación: " + java.time.LocalDate.now() + "\n\n"));

        document.add(new Paragraph("Usuarios registrados en el sistema: " + usuariosTotales));
        document.add(new Paragraph("Perros registrados en el sistema: " + perrosTotales));
        document.add(new Paragraph("\n\n"));

        // ===== Tabla de Usuarios =====
        Paragraph tituloUsuarios = new Paragraph("Usuarios", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        tituloUsuarios.setSpacingBefore(10);
        tituloUsuarios.setSpacingAfter(10);
        document.add(tituloUsuarios);

        PdfPTable tablaUsuarios = new PdfPTable(6);
        tablaUsuarios.setWidthPercentage(100);
        tablaUsuarios.setWidths(new int[]{3, 5, 4, 4, 2, 2});

        tablaUsuarios.addCell("Nombre");
        tablaUsuarios.addCell("Correo");
        tablaUsuarios.addCell("Responsable");
        tablaUsuarios.addCell("Teléfono");
        tablaUsuarios.addCell("Disponible");
        tablaUsuarios.addCell("Rol ID");

        for (Usuario u : usuarios) {
            tablaUsuarios.addCell(u.getNombre());
            tablaUsuarios.addCell(u.getCorreo());
            tablaUsuarios.addCell(u.getResponsable() != null ? u.getResponsable() : "null");
            tablaUsuarios.addCell(u.getTelefono() != null ? u.getTelefono() : "null");
            tablaUsuarios.addCell(String.valueOf(u.isDisponible()));
            tablaUsuarios.addCell(u.getRol() != null ? u.getRol().getNombre() : "null");


        }

        document.add(tablaUsuarios);
        document.add(new Paragraph("\n"));

        // ===== Tabla de Perros =====
        Paragraph tituloPerros = new Paragraph("Perros", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        tituloPerros.setSpacingBefore(10);
        tituloPerros.setSpacingAfter(10);
        document.add(tituloPerros);

        PdfPTable tablaPerros = new PdfPTable(4);
        tablaPerros.setWidthPercentage(100);
        tablaPerros.setWidths(new int[]{3, 3, 2, 2});

        tablaPerros.addCell("Nombre");
        tablaPerros.addCell("Dueño Actual");
        tablaPerros.addCell("Disponible");
        tablaPerros.addCell("Sexo");

      for (Perros p : perros) {
    tablaPerros.addCell(p.getNombre());

    Usuario usuario = usuarioRepository.findById(p.getUserId()).orElse(null);
    tablaPerros.addCell(usuario != null ? usuario.getNombre() : "null");

    tablaPerros.addCell(String.valueOf(p.isDisponible()));
    tablaPerros.addCell(p.getSexo());




}


        document.add(tablaPerros);

        document.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_estadisticas.pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }
}
