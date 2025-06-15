package proyecto.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perros")
public class Perros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;
    private String descripcion;

    private boolean disponible = true; // Siempre inicia como disponible

    private Integer edad;

    @Column(name = "historial_clinico")
    private String historialClinico;

    private String imagenperro;
    private String nombre;
    private String raza;
    private String sexo;
    private String tamanio;

    @Column(name = "usuario_id")
    private Long userId;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getHistorialClinico() { return historialClinico; }
    public void setHistorialClinico(String historialClinico) { this.historialClinico = historialClinico; }

    public String getImagenperro() { return imagenperro; }
    public void setImagenperro(String imagenperro) { this.imagenperro = imagenperro; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}