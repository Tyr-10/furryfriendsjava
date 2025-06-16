// src/main/java/proyecto/demo/service/UserDetailsServiceImpl.java

package proyecto.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        System.out.println("Intentando autenticar: " + correo);
        
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        System.out.println("Usuario encontrado. Email: " + usuario.getCorreo());

        // Construimos el rol con el prefijo "ROLE_"
        String nombreRol = usuario.getRol().getNombre();
        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority("ROLE_" + nombreRol);
        
        return new org.springframework.security.core.userdetails.User(
            usuario.getCorreo(),
            usuario.getPassword(),
            Collections.singletonList(autoridad)
        );
    }
}