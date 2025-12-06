package proyecto.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import org.springframework.core.io.ClassPathResource;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.text.NumberFormat;

import jakarta.servlet.http.HttpServletResponse;
import proyecto.demo.model.Perros;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.PerrosRepository;
import proyecto.demo.repository.UsuarioRepository;

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

    // Generar PDF con gráficos + tablas
    @GetMapping("/estadisticas/reporte")
    public void generarReporteEstadisticas(HttpServletResponse response) throws Exception {
        long usuariosTotales = usuarioRepository.count();
        long perrosTotales = perrosRepository.count();

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Perros> perros = perrosRepository.findAll();

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // ====== ENCABEZADO: TÍTULO + LOGO (en una fila) ======
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[]{8, 2});

        Paragraph titulo = new Paragraph("Reporte General de Estadísticas",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.add(new Paragraph("\nFecha de generación: " + java.time.LocalDate.now() + "\n\n"));

        PdfPCell titleCell = new PdfPCell();
        titleCell.addElement(titulo);
        titleCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(titleCell);

        // Intentar cargar el logo desde classpath: /static/images/logo.jpg (mismo usado por recursos estáticos)
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        try {
            ClassPathResource logoResource = new ClassPathResource("static/images/logo.jpg");
            if (logoResource.exists()) {
                byte[] logoBytes = logoResource.getInputStream().readAllBytes();
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(90, 90);
                logo.setAlignment(Image.RIGHT);
                logoCell.addElement(logo);
            }
        } catch (IOException ex) {
            // Si no se encuentra el logo no interrumpe la generación; dejar celda vacía
        }
        logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        headerTable.addCell(logoCell);

        document.add(headerTable);

        // ================== GRÁFICO PIE (con etiquetas numéricas) ==================
        DefaultPieDataset datasetPie = new DefaultPieDataset();
        datasetPie.setValue("Usuarios", usuariosTotales);
        datasetPie.setValue("Perros", perrosTotales);

        JFreeChart chartPie = ChartFactory.createPieChart(
                "Distribución Usuarios vs Perros",
                datasetPie,
                true, true, false
        );

        // Configurar que las secciones muestren el nombre y el valor numérico
        PiePlot piePlot = (PiePlot) chartPie.getPlot();
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}",
                NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
        piePlot.setSimpleLabels(false);

        Image chartImagePie = convertirGraficoAImagen(chartPie, 500, 300);
        document.add(chartImagePie);
        document.add(new Paragraph("\n\n"));

        // ================== GRÁFICO BARRAS ==================
        DefaultCategoryDataset datasetBar = new DefaultCategoryDataset();
        datasetBar.addValue(usuariosTotales, "Usuarios", "2025");
        datasetBar.addValue(perrosTotales, "Perros", "2025");

        JFreeChart chartBar = ChartFactory.createBarChart(
                "Comparación Total",
                "Categoría",
                "Cantidad",
                datasetBar
        );

        Image chartImageBar = convertirGraficoAImagen(chartBar, 500, 300);
        document.add(chartImageBar);
        document.add(new Paragraph("\n\n"));

        // ================== TABLA USUARIOS ==================
        Paragraph tituloUsuarios = new Paragraph("Usuarios",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
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

        // ================== TABLA PERROS ==================
        Paragraph tituloPerros = new Paragraph("Perros",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
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

        // ====== CIERRE ======
        document.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_estadisticas.pdf");
        response.getOutputStream().write(baos.toByteArray());
        response.getOutputStream().flush();
    }

    // Método auxiliar para convertir gráficos a imágenes de iText
    private Image convertirGraficoAImagen(JFreeChart chart, int width, int height)
            throws IOException, BadElementException {
        ByteArrayOutputStream chartBaos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartBaos, chart, width, height);
        return Image.getInstance(chartBaos.toByteArray());
    }
}
