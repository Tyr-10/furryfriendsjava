package proyecto.demo.repository;

import proyecto.demo.model.Viabilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViabilidadRepository extends JpaRepository<Viabilidad, Long> {
    Optional<Viabilidad> findByNombre(String nombre);
}
