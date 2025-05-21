package com.utn.frm.instrumentos.dto;

/**
 * Objeto de Transferencia de Datos (DTO) para la entidad Categoria.
 * - Se utiliza para enviar/recibir datos sin exponer la lista de instrumentos asociados.
 * - Evita recursión en las respuestas JSON y mejora el rendimiento.
 */
public class CategoriaDTO {
    private Long id;          // ID de la categoría
    private String denominacion; // Nombre de la categoría

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDenominacion() { return denominacion; }
    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }
}