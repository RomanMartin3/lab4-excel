package com.utn.frm.instrumentos.dto;

import java.util.List;

public class PedidoRequestDTO {
    private List<DetallePedidoRequestDTO> detalles;

    // Getters y Setters
    public List<DetallePedidoRequestDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoRequestDTO> detalles) { this.detalles = detalles; }
}