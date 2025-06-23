package proyecto.demo.repository;

import proyecto.demo.model.Viabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ViabilidadRepository extends JpaRepository<Viabilidad, Long> {

    // Buscar viabilidades activas filtrando por nombre original (como búsqueda por texto)
    List<Viabilidad> findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(String nombreOriginal);

    // Listar todas las viabilidades activas, ordenadas por ID descendente
    List<Viabilidad> findByActivoTrueOrderByIdDesc();
}