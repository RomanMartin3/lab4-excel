/**
 * InstrumentoDTO.java
 * Objeto de Transferencia de Datos (DTO) para representar un instrumento.
 * Contiene información básica como ID, nombre y precio.
 * Se utiliza para transferir datos entre el frontend y el backend.
 */
package com.utn.frm.instrumentos.dto;

import java.math.BigDecimal;

public class InstrumentoDTO {
    private Long id;
    private String instrumento; // Nombre del instrumento
    private BigDecimal precio;  // Precio unitario

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstrumento() { return instrumento; }
    public void setInstrumento(String instrumento) { this.instrumento = instrumento; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}