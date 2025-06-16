package proyecto.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.demo.model.Perros;

public interface RegistroPerrosRepository extends JpaRepository<Perros, Long> {
    // Métodos personalizados si los necesitas
}