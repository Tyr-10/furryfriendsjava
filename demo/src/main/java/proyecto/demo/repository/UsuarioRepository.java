package proyecto.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.demo.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);
    List<Usuario> findByDisponibleTrue(); // Mostrar solo los usuarios disponibles
    int countByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);
}