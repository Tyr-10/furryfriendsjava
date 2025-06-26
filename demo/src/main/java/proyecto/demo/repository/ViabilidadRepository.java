package proyecto.demo.repository;

import proyecto.demo.model.Viabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViabilidadRepository extends JpaRepository<Viabilidad, Long> {

    // Buscar viabilidades activas filtrando por nombre original (como búsqueda por texto)
    List<Viabilidad> findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(String nombreOriginal);

    // Listar todas las viabilidades activas, ordenadas por ID descendente
    List<Viabilidad> findByActivoTrueOrderByIdDesc();

    // Listar todas las viabilidades, ordenadas por ID descendente
    List<Viabilidad> findAllByOrderByIdDesc();

    // Buscar viabilidades por nombre de archivo (sin importar mayúsculas/minúsculas), ordenadas por ID descendente
    List<Viabilidad> findByNombreOriginalContainingIgnoreCaseOrderByIdDesc(String nombreOriginal);

    @Query("SELECT v FROM Viabilidad v WHERE (:nombreOriginal IS NULL OR :nombreOriginal = '' OR LOWER(v.nombreOriginal) LIKE LOWER(CONCAT('%', :nombreOriginal, '%'))) ORDER BY v.id DESC")
    List<Viabilidad> buscarPorNombreOriginal(@Param("nombreOriginal") String nombreOriginal);
}