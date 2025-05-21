package com.utn.frm.instrumentos.services;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferencePayerRequest; // Añadir este import
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest; // Import si decides usar Payer
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.utn.frm.instrumentos.dto.PreferenceResponseDTO;
import com.utn.frm.instrumentos.entities.Pedido;
import com.utn.frm.instrumentos.entities.PedidoDetalle;
import com.utn.frm.instrumentos.entities.Instrumento;
import org.slf4j.Logger; // SLF4J Logger
import org.slf4j.LoggerFactory; // SLF4J Logger
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class); // Logger SLF4J

    @Value("${mercadopago.access_token}")
    private String accessToken;

    @Value("${app.frontend.url.success}")
    private String successUrl;

    @Value("${app.frontend.url.failure}")
    private String failureUrl;

    @Value("${app.frontend.url.pending}")
    private String pendingUrl;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.error("CRITICAL: El Access Token de Mercado Pago (mercadopago.access_token) no está configurado en application.properties.");
        } else {
            MercadoPagoConfig.setAccessToken(accessToken);
            log.info("Mercado Pago SDK configurado con Access Token.");
        }
        if (successUrl == null || successUrl.trim().isEmpty() ||
                failureUrl == null || failureUrl.trim().isEmpty() ||
                pendingUrl == null || pendingUrl.trim().isEmpty()) {
            log.warn("Advertencia: Una o más URLs de retorno (success, failure, pending) no están configuradas en application.properties. Se usarán placeholders.");
            // Asignar placeholders si es necesario
            if (successUrl == null || successUrl.trim().isEmpty()) successUrl = "http://localhost:5173/payment-feedback?status=success"; // Ejemplo
            if (failureUrl == null || failureUrl.trim().isEmpty()) failureUrl = "http://localhost:5173/payment-feedback?status=failure"; // Ejemplo
            if (pendingUrl == null || pendingUrl.trim().isEmpty()) pendingUrl = "http://localhost:5173/payment-feedback?status=pending"; // Ejemplo
        } else {
            log.info("Success URL: {}", successUrl);
            log.info("Failure URL: {}", failureUrl);
            log.info("Pending URL: {}", pendingUrl);
        }
    }

    public PreferenceResponseDTO crearPreferenciaPago(Pedido pedido) throws MPException, MPApiException {
        log.info("Iniciando creación de preferencia para Pedido ID: {}", pedido.getId());

        PreferenceClient client = new PreferenceClient();
        List<PreferenceItemRequest> items = new ArrayList<>();

        log.debug("Iterando sobre detalles del pedido ID: {}", pedido.getId());
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            Instrumento instrumento = detalle.getInstrumento();
            if (instrumento == null) {
                log.error("Error CRÍTICO: Instrumento es null para el detalle ID: {} del Pedido ID: {}", detalle.getId(), pedido.getId());
                throw new RuntimeException("Inconsistencia de datos: Instrumento no encontrado para un detalle del pedido ID: " + detalle.getId());
            }
            // Validación de precio
            if (instrumento.getPrecio() == null || instrumento.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Error CRÍTICO: Precio inválido (null, cero o negativo) para el Instrumento ID: {}. Precio: {}", instrumento.getId(), instrumento.getPrecio());
                throw new RuntimeException("Precio inválido para el instrumento ID: " + instrumento.getId() + ". El precio debe ser mayor que cero.");
            }

            log.debug("Añadiendo item al request de MP: [ID Instrumento: {}, Título: '{}', Cantidad: {}, Precio Unitario: {}]",
                    instrumento.getId(), instrumento.getInstrumento(), detalle.getCantidad(), instrumento.getPrecio());

            items.add(
                    PreferenceItemRequest.builder()
                            .id(instrumento.getId().toString())
                            .title(instrumento.getInstrumento())
                            .quantity(detalle.getCantidad())
                            .currencyId("ARS")
                            .unitPrice(instrumento.getPrecio())
                            .build());
        }

        if (items.isEmpty()) {
            log.error("Error CRÍTICO: No se pudieron generar ítems para la preferencia del Pedido ID: {}. La lista de ítems está vacía.", pedido.getId());
            throw new RuntimeException("No se pueden crear preferencias sin ítems para el pedido ID: " + pedido.getId());
        }

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl) // Usar la URL específica de éxito
                .failure(failureUrl) // Usar la URL específica de fallo
                .pending(pendingUrl) // Usar la URL específica de pendiente
                .build();
        log.debug("Back URLs para Pedido ID {}: Success='{}', Failure='{}', Pending='{}'",
                pedido.getId(), backUrls.getSuccess(), backUrls.getFailure(), backUrls.getPending());

        // Dentro de crearPreferenciaPago, antes de construir el PreferenceRequest:
        PreferencePayerRequest payer = PreferencePayerRequest.builder()
                .email("test_user_1998127515@testuser.com") //
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .payer(payer)
                .autoReturn("approved")
                .externalReference(pedido.getId().toString())
                .build();

        // --- LOG DETALLADO DEL REQUEST ANTES DE ENVIAR ---
        log.debug("--- INICIO DATOS PREFERENCE REQUEST (Pedido ID: {}) ---", pedido.getId());
        log.debug("External Reference: {}", request.getExternalReference());
        log.debug("Auto Return: {}", request.getAutoReturn());
        if (request.getBackUrls() != null) {
            log.debug("Back URL Success: {}", request.getBackUrls().getSuccess());
            log.debug("Back URL Failure: {}", request.getBackUrls().getFailure());
            log.debug("Back URL Pending: {}", request.getBackUrls().getPending());
        } else {
            log.warn("request.getBackUrls() es NULL para Pedido ID: {}", pedido.getId());
        }
        log.debug("Items ({}):", items.size());
        if (request.getItems() != null) {
            for (int i = 0; i < request.getItems().size(); i++) {
                PreferenceItemRequest item = request.getItems().get(i);
                log.debug("  Item[{}]: ID='{}', Title='{}', Quantity={}, Currency='{}', UnitPrice={}",
                        i, item.getId(), item.getTitle(), item.getQuantity(), item.getCurrencyId(), item.getUnitPrice());
            }
        } else {
            log.warn("request.getItems() es NULL para Pedido ID: {}", pedido.getId());
        }

        log.debug("--- FIN DATOS PREFERENCE REQUEST (Pedido ID: {}) ---", pedido.getId());

        log.info("Enviando PreferenceRequest a la API de Mercado Pago para Pedido ID: {}", pedido.getId());
        Preference preference = client.create(request);
        log.info("Preferencia creada exitosamente en Mercado Pago para Pedido ID: {}. Preference ID: {}", pedido.getId(), preference.getId());

        return new PreferenceResponseDTO(preference.getId(), preference.getInitPoint());
    }
}