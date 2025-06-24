package proyecto.demo.config;

import proyecto.demo.model.Usuario;
import proyecto.demo.model.Rol;
import proyecto.demo.repository.UsuarioRepository;
import proyecto.demo.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminDataLoader {

    @Bean
    CommandLineRunner initAdminUsers(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Busca el rol de admin (ajusta el nombre si tu rol se llama diferente)
            Rol rolAdmin = rolRepository.findById(4L).orElse(null);

            // --- Admin Mariana ---
            if (usuarioRepository.findByCorreo("mariana@gmail.com") == null) {
                Usuario mariana = new Usuario();
                mariana.setNombre("Admin Mariana");
                mariana.setCorreo("mariana@gmail.com");
                mariana.setPassword(encoder.encode("Admin123."));
                mariana.setTelefono("1234567890");
                mariana.setDireccion("Calle 21");
                mariana.setCiudad("Ciudad Ejemplo");
                mariana.setCapacidad(50);
                mariana.setHorarios("9am - 5pm");
                mariana.setResponsable("Mariana Leal");
                mariana.setServicios("Consultoría");
                mariana.setRol(rolAdmin);
                usuarioRepository.save(mariana);
                System.out.println("Admin Mariana creado.");
            }

            // --- Admin Emilly ---
            if (usuarioRepository.findByCorreo("emilly@gmail.com") == null) {
                Usuario emilly = new Usuario();
                emilly.setNombre("Admin Emilly");
                emilly.setCorreo("emilly@gmail.com");
                emilly.setPassword(encoder.encode("Admin1234."));
                emilly.setTelefono("1234567890");
                emilly.setDireccion("Calle Falsa 31");
                emilly.setCiudad("Ciudad Ejemplo");
                emilly.setCapacidad(50);
                emilly.setHorarios("9am - 5pm");
                emilly.setResponsable("Emilly Cinaricicua");
                emilly.setServicios("Consultoría");
                emilly.setRol(rolAdmin);
                usuarioRepository.save(emilly);
                System.out.println("Admin Emilly creado.");
            }

            // --- Admin Juan ---
            if (usuarioRepository.findByCorreo("juan@gmail.com") == null) {
                Usuario juan = new Usuario();
                juan.setNombre("Admin Juan");
                juan.setCorreo("juan@gmail.com");
                juan.setPassword(encoder.encode("Admin12345."));
                juan.setTelefono("1234567890");
                juan.setDireccion("Calle 41");
                juan.setCiudad("Ciudad Ejemplo");
                juan.setCapacidad(50);
                juan.setHorarios("9am - 5pm");
                juan.setResponsable("Juan Valencia");
                juan.setServicios("Consultoría");
                juan.setRol(rolAdmin);
                usuarioRepository.save(juan);
                System.out.println("Admin Juan creado.");
            }
        };
    }
}