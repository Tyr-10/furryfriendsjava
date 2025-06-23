package proyecto.demo.repository;

import proyecto.demo.model.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    // Buscar seguimientos activos filtrando por nombre original (como búsqueda por texto)
    List<Seguimiento> findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(String nombreOriginal);

    // Listar todos los seguimientos activos, ordenados por ID descendente
    List<Seguimiento> findByActivoTrueOrderByIdDesc();
}