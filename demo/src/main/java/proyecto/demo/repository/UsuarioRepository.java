package proyecto.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import proyecto.demo.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);

    List<Usuario> findByDisponibleTrue(); // Mostrar solo los usuarios disponibles

    int countByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);

    // Filtro multicriterio: ambos campos deben cumplirse si ambos están presentes
    @Query("SELECT u FROM Usuario u WHERE u.disponible = true " +
            "AND (:nombre IS NULL OR :nombre = '' OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
            "AND (:correo IS NULL OR :correo = '' OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%')))")
    List<Usuario> buscarPorNombreYCorreo(@Param("nombre") String nombre, @Param("correo") String correo);
}