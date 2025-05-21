package com.utn.frm.instrumentos.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Instrumento.java
 * Entidad que representa un instrumento musical.
 * Relacionada con la tabla 'instrumentos' en la base de datos.
 */
@Entity
@Table(name = "instrumentos")
public class Instrumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String instrumento;

    @Column(length = 50)
    private String marca;

    @Column(length = 50)
    private String modelo;

    @Column(length = 255)
    private String imagen;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(name = "costo_envio", length = 50)
    private String costoEnvio;

    @Column(name = "cantidad_vendida")
    private int cantidadVendida;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @JsonIgnoreProperties({"instrumentos", "hibernateLazyInitializer", "handler"})
    private Categoria categoria;

    public Instrumento(String instrumento, String marca, String modelo, String imagen,
                       BigDecimal precio, String costoEnvio, int cantidadVendida,
                       String descripcion, Categoria categoria) {
        this.instrumento = instrumento;
        this.marca = marca;
        this.modelo = modelo;
        this.imagen = imagen;
        this.precio = precio;
        this.costoEnvio = costoEnvio;
        this.cantidadVendida = cantidadVendida;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public Instrumento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(String costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}