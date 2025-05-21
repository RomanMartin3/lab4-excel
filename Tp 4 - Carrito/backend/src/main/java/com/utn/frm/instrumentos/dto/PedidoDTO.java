package com.utn.frm.instrumentos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class PedidoDTO {
    private Long id;
    private ZonedDateTime fecha;
    private BigDecimal total;
    private List<PedidoDetalleDTO> detalles;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    public ZonedDateTime getFecha() { return fecha; }
    public void setFecha(ZonedDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<PedidoDetalleDTO> getDetalles() { return detalles; }
    public void setDetalles(List<PedidoDetalleDTO> detalles) { this.detalles = detalles; }
}