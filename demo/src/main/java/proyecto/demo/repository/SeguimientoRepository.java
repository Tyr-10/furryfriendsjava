package proyecto.demo.repository;

import proyecto.demo.model.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    // Buscar seguimientos activos filtrando por nombre original (como búsqueda por
    // texto)
    List<Seguimiento> findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(String nombreOriginal);

    // Listar todos los seguimientos activos, ordenados por ID descendente
    List<Seguimiento> findByActivoTrueOrderByIdDesc();

    @Query("SELECT s FROM Seguimiento s WHERE s.activo = true " +
            "AND (:nombreOriginal IS NULL OR :nombreOriginal = '' OR LOWER(s.nombreOriginal) LIKE LOWER(CONCAT('%', :nombreOriginal, '%'))) "
            +
            "ORDER BY s.id DESC")
    List<Seguimiento> buscarPorNombreOriginal(@Param("nombreOriginal") String nombreOriginal);
}