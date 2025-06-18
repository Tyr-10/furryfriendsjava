package proyecto.demo.repository;

import proyecto.demo.model.Viabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ViabilidadRepository extends JpaRepository<Viabilidad, Long> {

    // Buscar por nombre original parcialmente y activo
    List<Viabilidad> findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(String archivo);

    // 🔧 ESTE es el que te faltaba para listar todos los activos ordenados
    List<Viabilidad> findByActivoTrueOrderByIdDesc();
}