package com.utn.frm.instrumentos.dto;

import java.util.Objects;

public class CambiarRolRequest {
    private String nuevoRol;

    public CambiarRolRequest() {
    }

    public CambiarRolRequest(String nuevoRol) {
        this.nuevoRol = nuevoRol;
    }

    public String getNuevoRol() {
        return nuevoRol;
    }

    public void setNuevoRol(String nuevoRol) {
        this.nuevoRol = nuevoRol;
    }

    @Override
    public String toString() {
        return "CambiarRolRequest{" +
                "nuevoRol='" + nuevoRol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CambiarRolRequest that = (CambiarRolRequest) o;
        return Objects.equals(nuevoRol, that.nuevoRol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nuevoRol);
    }
}