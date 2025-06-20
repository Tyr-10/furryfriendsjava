package proyecto.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.demo.model.Seguimiento;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {
}