package com.utn.frm.instrumentos.services;

import com.utn.frm.instrumentos.entities.Usuario;
import com.utn.frm.instrumentos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Set<String> ROLES_VALIDOS = new HashSet<>(Arrays.asList("ADMIN", "OPERADOR", "VISOR"));

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByNombreUsuario(usuario.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso: " + usuario.getNombreUsuario());
        }
        if (usuario.getRol() == null || !ROLES_VALIDOS.contains(usuario.getRol().toUpperCase())) {
            throw new IllegalArgumentException("Rol no válido: " + usuario.getRol() + ". Roles válidos: " + ROLES_VALIDOS);
        }

        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        usuario.setRol(usuario.getRol().toUpperCase()); // Guardar en mayúsculas para consistencia
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long countUsuarios() {
        return usuarioRepository.count();
    }

    // Podrías añadir más métodos (actualizar rol, eliminar usuario, etc.) según necesidad.
}