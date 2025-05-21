package com.utn.frm.instrumentos.dto;

import java.util.Objects;

public class UsuarioResponse {
    private Long id;
    private String nombreUsuario;
    private String rol;
    // private String token; // Si usáramos JWT, iría aquí

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long id, String nombreUsuario, String rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
    }

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "UsuarioResponse{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioResponse that = (UsuarioResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(nombreUsuario, that.nombreUsuario) &&
                Objects.equals(rol, that.rol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreUsuario, rol);
    }
}