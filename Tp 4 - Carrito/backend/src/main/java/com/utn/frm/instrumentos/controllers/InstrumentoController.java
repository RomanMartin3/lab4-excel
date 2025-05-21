package com.utn.frm.instrumentos.controllers;

import com.utn.frm.instrumentos.entities.Categoria;
import com.utn.frm.instrumentos.entities.Instrumento;
import com.utn.frm.instrumentos.repositories.CategoriaRepository;
import com.utn.frm.instrumentos.repositories.InstrumentoRepository;
import org.springframework.http.HttpStatus; // Necesario para ResponseEntity.status
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instrumentos")
public class InstrumentoController {

    private final InstrumentoRepository instrumentoRepo;
    private final CategoriaRepository categoriaRepo;

    // Inyección por constructor es correcta
    public InstrumentoController(InstrumentoRepository instrumento, CategoriaRepository categoria) {
        this.instrumentoRepo = instrumento;
        this.categoriaRepo = categoria;
    }

    /**
     * Obtiene todos los instrumentos registrados.
     * Acceso público o para cualquier usuario autenticado según SecurityConfig.
     * @return Lista de Instrumento.
     */
    @GetMapping("/all")
    public List<Instrumento> getAll() {
        return instrumentoRepo.findAll();
    }

    /**
     * Obtiene instrumentos filtrados por categoría.
     * @param id ID de la categoría.
     * @return Lista de Instrumento asociados a la categoría.
     */
    @GetMapping("/categoria/{id}")
    public List<Instrumento> getByCategoria(@PathVariable Long id) {
        // Considera manejar el caso en que la categoría no se encuentre con una respuesta más explícita
        Categoria categoria = categoriaRepo.findById(id).orElse(null);
        if (categoria == null) {
            // Podrías devolver una lista vacía o un ResponseEntity con 404
            return List.of(); // o ResponseEntity.notFound().build(); pero el tipo de retorno es List
        }
        return instrumentoRepo.findByCategoria(categoria);
    }

    /**
     * Busca un instrumento por su ID.
     * @param id ID del instrumento.
     * @return ResponseEntity con el Instrumento encontrado o error 404.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Instrumento> getInstrumento(@PathVariable Long id) {
        Optional<Instrumento> instrumento = instrumentoRepo.findById(id);
        return instrumento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo instrumento. Solo Admin.
     * @param instrumento Datos del instrumento a crear (incluye categoría).
     * @return ResponseEntity con el Instrumento creado o error.
     */
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crear(@RequestBody Instrumento instrumento) {
        try {
            if (instrumento.getCategoria() == null || instrumento.getCategoria().getId() == null) {
                return ResponseEntity.badRequest().body("La categoría y su ID son obligatorios para crear un instrumento.");
            }
            Optional<Categoria> categoriaOpt = categoriaRepo.findById(instrumento.getCategoria().getId());
            if (categoriaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Categoría no encontrada con ID: " + instrumento.getCategoria().getId());
            }
            instrumento.setCategoria(categoriaOpt.get());
            Instrumento nuevoInstrumento = instrumentoRepo.save(instrumento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInstrumento);
        } catch (Exception e) {
            // Loguear el error es buena práctica
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el instrumento: " + e.getMessage());
        }
    }

    /**
     * Actualiza un instrumento existente. Solo Admin.
     * @param id ID del instrumento a actualizar.
     * @param instrumentoActualizado Nuevos datos del instrumento.
     * @return ResponseEntity con el Instrumento actualizado o error.
     */
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Instrumento instrumentoActualizado) {

        try {
            Instrumento instrumentoExistente = instrumentoRepo.findById(id)
                    .orElse(null);

            if (instrumentoExistente == null) {
                return ResponseEntity.notFound().build();
            }

            if (instrumentoActualizado.getCategoria() == null || instrumentoActualizado.getCategoria().getId() == null) {
                return ResponseEntity.badRequest().body("La categoría y su ID son obligatorios para actualizar un instrumento.");
            }

            Categoria categoria = categoriaRepo.findById(instrumentoActualizado.getCategoria().getId())
                    .orElse(null);
            if (categoria == null) {
                return ResponseEntity.badRequest().body("Categoría no encontrada con ID: " + instrumentoActualizado.getCategoria().getId());
            }

            instrumentoExistente.setInstrumento(instrumentoActualizado.getInstrumento()); // Asumo que tienes un campo "instrumento" (nombre)
            instrumentoExistente.setMarca(instrumentoActualizado.getMarca());
            instrumentoExistente.setModelo(instrumentoActualizado.getModelo());
            instrumentoExistente.setImagen(instrumentoActualizado.getImagen());
            instrumentoExistente.setPrecio(instrumentoActualizado.getPrecio());
            instrumentoExistente.setCostoEnvio(instrumentoActualizado.getCostoEnvio());
            instrumentoExistente.setCantidadVendida(instrumentoActualizado.getCantidadVendida());
            instrumentoExistente.setDescripcion(instrumentoActualizado.getDescripcion());
            instrumentoExistente.setCategoria(categoria);

            Instrumento guardado = instrumentoRepo.save(instrumentoExistente);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el instrumento: " + e.getMessage());
        }
    }

    /**
     * Elimina un instrumento por su ID. Solo Admin.
     * @param id ID del instrumento a eliminar.
     * @return ResponseEntity con mensaje de confirmación o error.
     */
    @DeleteMapping("/borrar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (!instrumentoRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instrumento no encontrado con ID: " + id);
        }
        try {
            instrumentoRepo.deleteById(id);
            return ResponseEntity.ok("Instrumento eliminado con éxito. ID: " + id);
        } catch (Exception e) {
            // Por si hay alguna restricción de FK u otro problema
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el instrumento: " + e.getMessage());
        }
    }
}