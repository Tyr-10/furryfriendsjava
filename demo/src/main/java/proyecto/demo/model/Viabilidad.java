package proyecto.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "viabilidadEstudios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Viabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String archivo;

    @Column(name = "nombre_original")
    private String nombreOriginal;

    @Column(name = "rol_id")
    private Long rolId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
}