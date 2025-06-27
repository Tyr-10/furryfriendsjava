package proyecto.demo.repository;

import proyecto.demo.model.Perros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerrosRepository extends JpaRepository<Perros, Long> {

    // Perros disponibles para la vista pública
    List<Perros> findByDisponibleTrue();

    // Filtro por nombre
    List<Perros> findByDisponibleTrueAndNombreContainingIgnoreCase(String nombre);

    // Filtro para búsqueda avanzada (solo disponibles)
    @Query("SELECT p FROM Perros p WHERE p.disponible = true " +
            "AND (:edad IS NULL OR p.edad = :edad) " +
            "AND (:color IS NULL OR LOWER(p.color) LIKE LOWER(CONCAT('%', :color, '%'))) " +
            "AND (:tamanio IS NULL OR LOWER(p.tamanio) LIKE LOWER(CONCAT('%', :tamanio, '%'))) " +
            "AND (:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) ")
    List<Perros> findByFiltros(Integer edad, String color, String tamanio, String descripcion);

    // ✅ CORRECTO según tu modelo
    List<Perros> findByUserId(Long userId);

    List<Perros> findByUserIdAndDisponibleTrue(Long userId);

    // Filtro por nombre (si está presente)
    @Query("SELECT p FROM Perros p WHERE p.disponible = true " +
            "AND (:nombre IS NULL OR :nombre = '' OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))" )
    List<Perros> buscarPorNombre(@Param("nombre") String nombre);
}