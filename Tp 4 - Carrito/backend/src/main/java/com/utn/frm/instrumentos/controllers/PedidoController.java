package com.utn.frm.instrumentos.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPResponse;
import com.utn.frm.instrumentos.dto.PedidoRequestDTO;
import com.utn.frm.instrumentos.dto.PedidoResponseDTO;
import com.utn.frm.instrumentos.dto.PreferenceResponseDTO;
import com.utn.frm.instrumentos.entities.Pedido;
import com.utn.frm.instrumentos.services.PedidoService;
import com.utn.frm.instrumentos.services.MercadoPagoService;
import com.utn.frm.instrumentos.services.PedidoService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/pedidos") //Cambio realizado ahora es pedidoS
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        try {
            PedidoResponseDTO response = pedidoService.crearPedido(pedidoRequest);
            System.out.println("Pedido creado con ID (desde PedidoResponseDTO): " + response.getId()); // Asumiendo que PedidoResponseDTO tiene un getId()
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error al crear el pedido: {}", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodosPedidos() {
        return ResponseEntity.ok(pedidoService.obtenerTodosPedidos());
    }

    @PostMapping("/{pedidoId}/preferencia")
    public ResponseEntity<?> crearPreferenciaMercadoPago(@PathVariable Long pedidoId) {
        try {
            log.info("Solicitud para crear preferencia de Mercado Pago para pedido ID: {}", pedidoId);
            Optional<Pedido> pedidoOptional = pedidoService.findEntidadById(pedidoId);
            if (pedidoOptional.isEmpty()) { // Usar .isEmpty() es más idiomático desde Java 11+ que !.isPresent()
                log.warn("Pedido no encontrado con ID: {}", pedidoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Pedido no encontrado con ID: " + pedidoId));
            }

            Pedido pedidoCompleto = pedidoOptional.get();
            if (pedidoCompleto.getDetalles() == null || pedidoCompleto.getDetalles().isEmpty()) {
                log.warn("Intento de crear preferencia para pedido ID: {} sin ítems.", pedidoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "El pedido no contiene ítems para procesar el pago."));
            }

            log.debug("Llamando a mercadoPagoService.crearPreferenciaPago para pedido ID: {}", pedidoId);
            PreferenceResponseDTO preferenceResponse = mercadoPagoService.crearPreferenciaPago(pedidoCompleto);
            log.info("Preferencia de Mercado Pago creada exitosamente para pedido ID: {}. Preference ID: {}", pedidoId, preferenceResponse.getPreferenceId());
            return ResponseEntity.ok(preferenceResponse);

        } catch (MPApiException apiEx) {
            // Usamos getMessage() como sugiere el IDE para el mensaje principal,
            // y getStatusCode() para el código de estado.
            String errorMessage = apiEx.getMessage() != null ? apiEx.getMessage() : "Error desconocido de API Mercado Pago.";
            Integer statusCode = apiEx.getStatusCode(); // Puede ser null, lo manejaremos al construir el mensaje
            String causeMessage = (apiEx.getCause() != null && apiEx.getCause().getMessage() != null) ? apiEx.getCause().getMessage() : null;

            log.error("MPApiException al crear preferencia para pedido ID {}: Status Code: {}, Message: {}, Cause: {}",
                    pedidoId, statusCode, errorMessage, causeMessage, apiEx);
            MPResponse mpResponse = apiEx.getApiResponse();
            if (mpResponse != null && mpResponse.getContent() != null) {
                log.error("DETALLE DEL ERROR 400 DE MERCADO PAGO (Pedido ID {}): {}", pedidoId, mpResponse.getContent());
            } else {
                log.error("No se pudo obtener el contenido detallado de la respuesta de la API de Mercado Pago para el Pedido ID {}", pedidoId);
            }
            StringBuilder mensajeErrorDetallado = new StringBuilder("Error de API Mercado Pago: " + errorMessage);
            if (statusCode != null) { // La comprobación de nulidad aquí sigue siendo buena práctica
                mensajeErrorDetallado.append(" (Status: ").append(statusCode).append(")");
            }
            if (causeMessage != null) {
                mensajeErrorDetallado.append(". Detalles: ").append(causeMessage);
            }

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Collections.singletonMap("error", mensajeErrorDetallado.toString()));

        } catch (MPException mpEx) {
            log.error("MPException al crear preferencia para pedido ID {}: Message: {}", pedidoId, mpEx.getMessage(), mpEx);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error con el SDK de Mercado Pago: " + mpEx.getMessage()));

        } catch (RuntimeException e) {
            log.error("RuntimeException no esperada al crear preferencia para pedido ID {}: {}", pedidoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error interno al crear la preferencia de pago: " + e.getMessage()));
        }
    }
    //Obtener un PedidoResponseDTO por ID para consultar que funcione
    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long pedidoId) {
        try {
            PedidoResponseDTO response = pedidoService.obtenerPedidoDTOPorId(pedidoId); // Asumiendo que tienes un método que devuelve PedidoResponseDTO
            if (response == null) { // O si el servicio lanza una excepción que atrapas aquí
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Pedido no encontrado con ID: " + pedidoId));
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) //
                    .body(Collections.singletonMap("error al obtener el ID del pedido", e.getMessage()));
        }
    }

    @GetMapping("/excel-pedidos")
    public ResponseEntity<Resource> generarReporteExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) ZonedDateTime fechaHasta) throws IOException {

        ByteArrayInputStream excelStream = pedidoService.generarReporteExcel(fechaDesde, fechaHasta);
        InputStreamResource resource = new InputStreamResource(excelStream);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_pedidos.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    @GetMapping("/chart/pedidos-by-month")
    public ResponseEntity<List<Object[]>> getContadorPedidosPorMesYAñoChartData() {
        return ResponseEntity.ok(pedidoService.getContadorPedidosPorMesYAño());
    }

    // Nuevo endpoint para los datos del gráfico de torta
    @GetMapping("/chart/quantities-by-instrument")
    public ResponseEntity<List<Object[]>> getCantidadesPorInstrumentoChartData() {
        return ResponseEntity.ok(pedidoService.getCantidadesPorInstrumento());
    }
}


