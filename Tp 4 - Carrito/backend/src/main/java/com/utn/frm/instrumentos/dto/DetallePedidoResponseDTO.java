package com.utn.frm.instrumentos.dto;

import java.math.BigDecimal;

public class DetallePedidoResponseDTO {
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private InstrumentoDTO instrumento;

    // Getters y Setters
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public InstrumentoDTO getInstrumento() { return instrumento; }
    public void setInstrumento(InstrumentoDTO instrumento) { this.instrumento = instrumento; }
}
