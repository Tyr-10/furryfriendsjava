package proyecto.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import proyecto.demo.model.Usuario;
import proyecto.demo.repository.UsuarioRepository;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Obtener el correo (username) del usuario autenticado
        String correo = authentication.getName();

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        // Guardar el usuario en sesión
        if (usuario != null) {
            request.getSession().setAttribute("usuarioLogueado", usuario);
        }

        // Redireccionar según el rol
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_adoptante"))) {
            response.sendRedirect("/vistaadoptante");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_refugio_natural")) ||
                   authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_refugio_fisico"))) {
            response.sendRedirect("/vistarefugio");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
            response.sendRedirect("/vistaadmin");
        } else {
            response.sendRedirect("/"); // Por defecto
        }
    }
}