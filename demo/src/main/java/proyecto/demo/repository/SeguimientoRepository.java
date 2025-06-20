package proyecto.demo.repository;

import proyecto.demo.model.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    // Buscar por nombre original parcialmente y activo
    List<Seguimiento> findByActivoTrueAndNombreOriginalContainingIgnoreCaseOrderByIdDesc(String archivo);

    // Listar todos los activos ordenados
    List<Seguimiento> findByActivoTrueOrderByIdDesc();
}