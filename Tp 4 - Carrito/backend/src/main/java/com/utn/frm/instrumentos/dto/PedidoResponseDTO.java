package com.utn.frm.instrumentos.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class PedidoResponseDTO {
    private Long id;
    private ZonedDateTime fecha;
    private BigDecimal total;
    private List<DetallePedidoResponseDTO> detalles;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ZonedDateTime getFecha() { return fecha; }
    public void setFecha(ZonedDateTime fecha) { this.fecha = fecha; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<DetallePedidoResponseDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoResponseDTO> detalles) { this.detalles = detalles; }
}
