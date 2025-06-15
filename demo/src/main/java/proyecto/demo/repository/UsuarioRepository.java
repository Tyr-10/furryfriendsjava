package proyecto.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);  // Correcto
}