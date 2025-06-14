package proyecto.demo.repository;


import proyecto.demo.model.Perros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerrosRepository extends JpaRepository<Perros, Long> {

    @Query("SELECT p FROM Perros p WHERE p.disponible = true " +
            "AND (:edad IS NULL OR p.edad = :edad) " +
            "AND (:color IS NULL OR LOWER(p.color) LIKE LOWER(CONCAT('%', :color, '%'))) " +
            "AND (:tamanio IS NULL OR LOWER(p.tamanio) LIKE LOWER(CONCAT('%', :tamanio, '%'))) " +
            "AND (:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%')))")
    List<Perros> findByFiltros(Integer edad, String color, String tamanio, String descripcion);
}