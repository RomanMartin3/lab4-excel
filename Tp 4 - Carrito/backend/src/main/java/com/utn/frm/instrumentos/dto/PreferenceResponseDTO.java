package com.utn.frm.instrumentos.dto;

public class PreferenceResponseDTO {
    private String preferenceId;
    private String initPoint; // URL a la que redirigir

    // Constructor, Getters y Setters
    public PreferenceResponseDTO(String preferenceId, String initPoint) {
        this.preferenceId = preferenceId;
        this.initPoint = initPoint;
    }

    public String getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getInitPoint() {
        return initPoint;
    }

    public void setInitPoint(String initPoint) {
        this.initPoint = initPoint;
    }

}