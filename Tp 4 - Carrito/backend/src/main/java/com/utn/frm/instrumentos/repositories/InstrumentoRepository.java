package com.utn.frm.instrumentos.repositories;

import com.utn.frm.instrumentos.entities.Instrumento;
import com.utn.frm.instrumentos.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * InstrumentoRepository.java
 * Repositorio para la entidad Instrumento.
 * Incluye métodos personalizados para buscar instrumentos por categoría (entidad o ID).
 * Spring Data JPA genera las consultas automáticamente basándose en los nombres de los métodos.
 */
@Repository
public interface InstrumentoRepository extends JpaRepository<Instrumento, Long> {
    List<Instrumento> findByCategoria(Categoria categoria);      // Busca por entidad Categoria
    List<Instrumento> findByCategoriaId(Long idCategoria);       // Busca por ID de categoría
}
