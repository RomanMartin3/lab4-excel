package com.utn.frm.instrumentos.controllers;

import com.utn.frm.instrumentos.dto.CategoriaDTO;
import com.utn.frm.instrumentos.entities.Categoria;
import com.utn.frm.instrumentos.repositories.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar operaciones CRUD de categorías.
 * - Expone endpoints para crear, leer, actualizar y eliminar categorías.
 * - Utiliza DTOs para evitar exponer datos internos de las entidades.
 */
@RestController
@RequestMapping("/api/categoria") // Ruta base para todos los endpoints
public class CategoriaController {

    private final CategoriaRepository repo; // Repositorio para acceder a la base de datos

    public CategoriaController(CategoriaRepository repo) {
        this.repo = repo;
    }

    /**
     * Convierte una entidad Categoria a un DTO para ocultar campos sensibles.
     * @param categoria Entidad de la base de datos.
     * @return DTO con solo los campos necesarios (id y denominación).
     */
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setDenominacion(categoria.getDenominacion());
        return dto;
    }

    /**
     * Obtiene todas las categorías registradas.
     * @return Lista de CategoriaDTO (sin la lista de instrumentos asociados).
     */
    @GetMapping
    public List<CategoriaDTO> listarCategorias() {
        return repo.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva categoría.
     * @param categoria Datos de la categoría a crear (solo requiere "denominacion").
     * @return ResponseEntity con el DTO de la categoría creada o mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        // Validación: La denominación no puede estar vacía
        if (categoria.getDenominacion() == null || categoria.getDenominacion().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "El campo 'denominacion' es obligatorio")
            );
        }

        // Validación: Evitar duplicados
        if (repo.existsByDenominacionIgnoreCase(categoria.getDenominacion())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Collections.singletonMap("error", "Ya existe una categoría con ese nombre")
            );
        }

        Categoria nuevaCategoria = repo.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(nuevaCategoria));
    }

    /**
     * Actualiza una categoría existente.
     * @param id ID de la categoría a actualizar.
     * @param categoriaActualizada Nuevos datos de la categoría.
     * @return ResponseEntity con el DTO actualizado o mensaje de error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody Categoria categoriaActualizada) {

        // Validar que la categoría exista
        Optional<Categoria> categoriaExistente = repo.findById(id);
        if (categoriaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validar que la denominación no esté vacía
        if (categoriaActualizada.getDenominacion() == null || categoriaActualizada.getDenominacion().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "El campo 'denominacion' es obligatorio")
            );
        }

        // Actualizar y guardar
        Categoria categoria = categoriaExistente.get();
        categoria.setDenominacion(categoriaActualizada.getDenominacion());
        Categoria categoriaGuardada = repo.save(categoria);
        return ResponseEntity.ok(convertirADTO(categoriaGuardada));
    }

    /**
     * Elimina una categoría si no tiene instrumentos asociados.
     * @param id ID de la categoría a eliminar.
     * @return ResponseEntity con mensaje de éxito o error.
     */
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        // Validar que la categoría exista
        Optional<Categoria> categoria = repo.findById(id);
        if (categoria.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validar que no tenga instrumentos asociados
        if (!categoria.get().getInstrumentos().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Collections.singletonMap("error", "No se puede eliminar: existen instrumentos asociados")
            );
        }

        // Eliminar
        repo.deleteById(id);
        return ResponseEntity.ok().body(
                Collections.singletonMap("mensaje", "Categoría eliminada exitosamente")
        );
    }
}