package com.utn.frm.instrumentos.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.utn.frm.instrumentos.entities.Instrumento;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Categoria.java
 * Entidad que representa una categoría de instrumentos.
 * Relacionada con la tabla 'categoria' en la base de datos.
 */
@Entity
@Table(name = "categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "denominacion", nullable = false, length = 100)
    private String denominacion;

    @JsonManagedReference
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Instrumento> instrumentos = new ArrayList<>();

    public Categoria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public List<Instrumento> getInstrumentos() {
        return instrumentos;
    }

    public void setInstrumentos(List<Instrumento> instrumentos) {
        this.instrumentos = instrumentos;
    }
}