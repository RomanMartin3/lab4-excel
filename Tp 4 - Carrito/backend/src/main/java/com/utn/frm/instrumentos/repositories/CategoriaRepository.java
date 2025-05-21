package com.utn.frm.instrumentos.repositories;

import com.utn.frm.instrumentos.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CategoriaRepository.java
 * Repositorio para la entidad Categoria.
 * Proporciona acceso a operaciones CRUD básicas mediante JpaRepository.
 * No requiere métodos personalizados ya que las consultas se generan automáticamente.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByDenominacionIgnoreCase(String denominacion);
}