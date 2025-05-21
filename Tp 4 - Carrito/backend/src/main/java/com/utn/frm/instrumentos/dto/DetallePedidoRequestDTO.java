package com.utn.frm.instrumentos.dto;

public class DetallePedidoRequestDTO {
    private Long instrumentoId;
    private Integer cantidad;

    // Getters y Setters
    public Long getInstrumentoId() { return instrumentoId; }
    public void setInstrumentoId(Long instrumentoId) { this.instrumentoId = instrumentoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
