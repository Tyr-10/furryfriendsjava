package proyecto.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
}
