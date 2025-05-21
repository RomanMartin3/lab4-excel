package com.utn.frm.instrumentos.services;

import com.utn.frm.instrumentos.entities.Usuario;
import com.utn.frm.instrumentos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User; // Asegúrate que es org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true) // Buena práctica para métodos de solo lectura
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con nombre: " + nombreUsuario));

        Set<GrantedAuthority> authorities = new HashSet<>();
        // Spring Security espera los roles con el prefijo "ROLE_" para las comprobaciones hasRole()
        // Si usas hasAuthority() no necesitas el prefijo.
        // Para consistencia con hasRole() que es común, lo añadimos.
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()));

        return new org.springframework.security.core.userdetails.User(
                usuario.getNombreUsuario(),
                usuario.getClave(), // La clave ya está encriptada en la BD
                authorities);
    }
}