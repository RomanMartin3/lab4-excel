package com.utn.frm.instrumentos.dto;

import java.util.Objects;

public class LoginRequest {
    private String nombreUsuario;
    private String clave;

    public LoginRequest() {
    }

    public LoginRequest(String nombreUsuario, String clave) {
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
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

    @Override
    public String toString() {
        return "LoginRequest{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                // No incluir 'clave' en toString por seguridad en logs
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(nombreUsuario, that.nombreUsuario) &&
                Objects.equals(clave, that.clave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario, clave);
    }
}