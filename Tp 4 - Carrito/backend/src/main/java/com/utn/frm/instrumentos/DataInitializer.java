package com.utn.frm.instrumentos;

import com.utn.frm.instrumentos.entities.Categoria;
import com.utn.frm.instrumentos.entities.Instrumento;
import com.utn.frm.instrumentos.entities.Usuario;
import com.utn.frm.instrumentos.repositories.CategoriaRepository;
import com.utn.frm.instrumentos.repositories.InstrumentoRepository;
import com.utn.frm.instrumentos.services.UsuarioService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioService usuarioService;
    private final CategoriaRepository categoriaRepository; // Añadido
    private final InstrumentoRepository instrumentoRepository; // Añadido

    @Autowired
    public DataInitializer(UsuarioService usuarioService,
                           CategoriaRepository categoriaRepository, // Añadido
                           InstrumentoRepository instrumentoRepository) { // Añadido
        this.usuarioService = usuarioService;
        this.categoriaRepository = categoriaRepository; // Añadido
        this.instrumentoRepository = instrumentoRepository; // Añadido
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Ejecutando DataInitializer...");

        // --- Carga de Categorías ---
        if (categoriaRepository.count() == 0) {
            log.info("No hay categorías en la base de datos. Creando categorías iniciales...");
            Categoria guitarras = new Categoria("Guitarras"); // Asume constructor Categoria(String)
            guitarras = categoriaRepository.save(guitarras);

            Categoria pianos = new Categoria("Pianos");
            pianos = categoriaRepository.save(pianos);

            Categoria baterias = new Categoria("Baterías");
            baterias = categoriaRepository.save(baterias);

            Categoria teclados = new Categoria("Teclados");
            teclados = categoriaRepository.save(teclados);
            log.info("Categorías creadas.");

            // --- Carga de Instrumentos ---
            if (instrumentoRepository.count() == 0) {
                log.info("No hay instrumentos en la base de datos. Creando instrumentos iniciales...");

                // Asume un constructor en Instrumento o usa setters
                Instrumento inst1 = new Instrumento();
                inst1.setInstrumento("Guitarra Acústica Fender");
                inst1.setMarca("Fender");
                inst1.setModelo("FA-135CE");
                inst1.setImagen("nro1.jpg");
                inst1.setPrecio(new BigDecimal("1500.00"));
                inst1.setCostoEnvio("G");
                inst1.setCantidadVendida(50);
                inst1.setDescripcion("Guitarra acústica de concierto con tapa de abeto y cuerpo de caoba. Ideal para músicos profesionales.");
                inst1.setCategoria(guitarras);
                instrumentoRepository.save(inst1);

                Instrumento inst2 = new Instrumento();
                inst2.setInstrumento("Piano Digital Yamaha P-125");
                inst2.setMarca("Fender"); // Según tu SQL. Considera cambiar a "Yamaha" si es un error.
                inst2.setModelo("FA 135CE"); // Según tu SQL. Considera cambiar a "P-125".
                inst2.setImagen("nro6.jpg");
                inst2.setPrecio(new BigDecimal("350.00"));
                inst2.setCostoEnvio("300");
                inst2.setCantidadVendida(10);
                inst2.setDescripcion("Teclado portátil ideal para principiantes.");
                inst2.setCategoria(pianos);
                instrumentoRepository.save(inst2);

                Instrumento inst3 = new Instrumento();
                inst3.setInstrumento("Guitarra Acústica Fender (Premium)");
                inst3.setMarca("Fender");
                inst3.setModelo("FA-135CE Special");
                inst3.setImagen("nro1.jpg");
                inst3.setPrecio(new BigDecimal("1500000.00"));
                inst3.setCostoEnvio("G");
                inst3.setCantidadVendida(50);
                inst3.setDescripcion("Guitarra acústica de concierto con tapa de abeto y cuerpo de caoba. Modelo premium.");
                inst3.setCategoria(pianos); // En tu SQL está en Pianos
                instrumentoRepository.save(inst3);

                Instrumento inst4 = new Instrumento();
                inst4.setInstrumento("qweqwe");
                inst4.setMarca("qweqwe");
                inst4.setModelo("qweqwe");
                inst4.setImagen("nro8.jpg");
                inst4.setPrecio(new BigDecimal("23333.00"));
                inst4.setCostoEnvio("G");
                inst4.setCantidadVendida(12);
                inst4.setDescripcion("sadasdasdasd");
                inst4.setCategoria(baterias);
                instrumentoRepository.save(inst4);

                Instrumento inst5 = new Instrumento();
                inst5.setInstrumento("QWEQWE");
                inst5.setMarca("QWEQWE");
                inst5.setModelo("EQWEQWE");
                inst5.setImagen("nro3.jpg");
                inst5.setPrecio(new BigDecimal("6.00"));
                inst5.setCostoEnvio("G");
                inst5.setCantidadVendida(2);
                inst5.setDescripcion("QWEQWEQW");
                inst5.setCategoria(guitarras);
                instrumentoRepository.save(inst5);

                log.info("Instrumentos creados.");
            } else {
                log.info("La base de datos ya contiene instrumentos. No se crearán instrumentos iniciales.");
            }
        } else {
            log.info("La base de datos ya contiene categorías. No se crearán categorías ni instrumentos iniciales.");
        }

        // --- Carga de Usuarios ---
        if (usuarioService.countUsuarios() == 0) {
            log.info("No hay usuarios en la base de datos. Creando usuarios iniciales...");
            try {
                Usuario admin = new Usuario();
                admin.setNombreUsuario("admin");
                admin.setClave("admin123"); // Clave en texto plano, el servicio la encriptará
                admin.setRol("ADMIN");     // Rol como String, el servicio lo validará y guardará en mayúsculas
                usuarioService.crearUsuario(admin);
                log.info("Usuario Admin creado: nombreUsuario='admin', rol='ADMIN'");

                Usuario operator = new Usuario();
                operator.setNombreUsuario("operador");
                operator.setClave("operador123");
                operator.setRol("OPERADOR");
                usuarioService.crearUsuario(operator);
                log.info("Usuario Operador creado: nombreUsuario='operador', rol='OPERADOR'");

                Usuario viewer = new Usuario();
                viewer.setNombreUsuario("visor"); // Cambiado de "visorInicial" para ser más genérico
                viewer.setClave("visor123");
                viewer.setRol("VISOR");
                usuarioService.crearUsuario(viewer);
                log.info("Usuario Visor creado: nombreUsuario='visor', rol='VISOR'");

            } catch (IllegalArgumentException e) {
                log.error("Error al crear usuario inicial (IllegalArgumentException): " + e.getMessage());
            } catch (Exception e) {
                log.error("Error inesperado al crear usuarios iniciales: " + e.getMessage(), e);
            }
        } else {
            log.info("La base de datos ya contiene usuarios. No se crearán usuarios iniciales.");
        }
        log.info("DataInitializer finalizado.");
    }
}