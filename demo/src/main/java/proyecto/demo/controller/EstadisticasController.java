package proyecto.demo.controller;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class EstadisticasController {

    // Muestra la vista HTML que contiene la imagen generada
    @GetMapping("/estadisticas")
    public String mostrarVistaEstadisticas() {
        return "estadisticas";  // estadisticas.html en templates
    }

    // Genera la imagen PNG del gráfico
    @GetMapping("/grafica")
    public void generarGrafica(HttpServletResponse response) throws IOException {
        // Crear el dataset (datos simulados)
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(12, "Adopciones", "Enero");
        dataset.addValue(20, "Adopciones", "Febrero");
        dataset.addValue(18, "Adopciones", "Marzo");
        dataset.addValue(25, "Registros", "Enero");
        dataset.addValue(28, "Registros", "Febrero");
        dataset.addValue(32, "Registros", "Marzo");

        // Crear el gráfico
        JFreeChart chart = ChartFactory.createLineChart(
                "Adopciones vs Registros", // Título
                "Mes",                     // Eje X
                "Cantidad",                // Eje Y
                dataset
        );

        // Convertir el gráfico a imagen
        BufferedImage image = chart.createBufferedImage(600, 400);

        // Establecer cabeceras HTTP para imagen PNG
        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
        response.getOutputStream().close();
    }
}