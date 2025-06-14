package proyecto.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proyecto.demo.model.Rol;
import proyecto.demo.repository.RolRepository;

import java.util.Arrays;
import java.util.List;

@Component
public class InitRolesConfig {

    @Autowired
    private RolRepository rolRepository;

    @PostConstruct
    public void init() {
        List<String> roles = Arrays.asList("adoptante", "refugio_natural", "refugio_fisico", "admin");
        for (String nombre : roles) {
            if (!rolRepository.findByNombre(nombre).isPresent()) {
                Rol rol = new Rol();
                rol.setNombre(nombre);
                rolRepository.save(rol);
            }
        }
    }
}




