package proyecto.demo.model;


import jakarta.persistence.*;

@Entity
@Table(name = "perros")
public class Perros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer edad;
    private String raza;
    private String tamanio;
    private String descripcion;
    private Long userId;
    private boolean disponible;
    private String imagenperro;
    private String sexo;
    private String historialClinico;
    private String color;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // ... (repite para cada campo)

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // etc.
}