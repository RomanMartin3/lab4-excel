package com.utn.frm.instrumentos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.util.Objects;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", unique = true, nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String clave; // Se almacenará encriptada

    @Column(nullable = false)
    private String rol; // "ADMIN", "OPERADOR", "VISOR"

    // Constructor sin argumentos (requerido por JPA)
    public Usuario() {
    }

    // Constructor con todos los argumentos (útil para creación)
    public Usuario(String nombreUsuario, String clave, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.rol = rol;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // equals, hashCode y toString (buenas prácticas para entidades)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) &&
                Objects.equals(nombreUsuario, usuario.nombreUsuario); // Puedes basar equals en id o en campos únicos como nombreUsuario
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", rol='" + rol + '\'' +
                // No incluir 'clave' en toString por seguridad
                '}';
    }
}