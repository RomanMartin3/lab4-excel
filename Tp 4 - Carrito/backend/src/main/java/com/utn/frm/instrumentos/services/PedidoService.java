package com.utn.frm.instrumentos.services;

import com.utn.frm.instrumentos.dto.*;
import com.utn.frm.instrumentos.entities.*;
import com.utn.frm.instrumentos.repositories.*;
import jakarta.transaction.Transactional;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private InstrumentoRepository instrumentoRepository;

    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequest) {
        Pedido pedido = new Pedido();
        pedido.setFecha(ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")));

        List<PedidoDetalle> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedidoRequestDTO detalleRequest : pedidoRequest.getDetalles()) {
            Instrumento instrumento = instrumentoRepository.findById(detalleRequest.getInstrumentoId())
                    .orElseThrow(() -> new RuntimeException("Instrumento no encontrado: " + detalleRequest.getInstrumentoId()));

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(instrumento.getPrecio());
            detalle.setInstrumento(instrumento);
            detalle.setPedido(pedido);

            detalles.add(detalle);
            total = total.add(instrumento.getPrecio().multiply(BigDecimal.valueOf(detalleRequest.getCantidad())));
        }

        pedido.setDetalles(detalles);
        pedido.setTotal(total);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return convertirAResponseDTO(pedidoGuardado);
    }

    public List<PedidoResponseDTO> obtenerTodosPedidos() {
        return pedidoRepository.findAllWithDetalles().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    private PedidoResponseDTO convertirAResponseDTO(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setFecha(pedido.getFecha());
        dto.setTotal(pedido.getTotal());
        dto.setDetalles(convertirDetallesAResponseDTO(pedido.getDetalles()));
        return dto;
    }

    private List<DetallePedidoResponseDTO> convertirDetallesAResponseDTO(List<PedidoDetalle> detalles) {
        return detalles.stream()
                .map(this::convertirDetalleAResponseDTO)
                .collect(Collectors.toList());
    }

    private DetallePedidoResponseDTO convertirDetalleAResponseDTO(PedidoDetalle detalle) {
        DetallePedidoResponseDTO dto = new DetallePedidoResponseDTO();
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setInstrumento(convertirInstrumentoADTO(detalle.getInstrumento()));
        return dto;
    }

    private InstrumentoDTO convertirInstrumentoADTO(Instrumento instrumento) {
        InstrumentoDTO dto = new InstrumentoDTO();
        dto.setId(instrumento.getId());
        dto.setInstrumento(instrumento.getInstrumento());
        dto.setPrecio(instrumento.getPrecio());
        return dto;
    }

    //Busca una entidad Pedido completa por su ID, incluyendo sus detalles e instrumentos.
    public Optional<Pedido> findEntidadById(Long pedidoId) {
        return pedidoRepository.findByIdWithDetallesAndInstrumentos(pedidoId);
    }

    //Obtiene un PedidoResponseDTO para un pedido específico por su ID.


    public PedidoResponseDTO obtenerPedidoDTOPorId(Long pedidoId) {
        Optional<Pedido> pedidoOptional = pedidoRepository.findByIdWithDetallesAndInstrumentos(pedidoId);
        if (!pedidoOptional.isPresent()) {
            throw new RuntimeException("Pedido no encontrado con ID: " + pedidoId);
        }
        return convertirAResponseDTO(pedidoOptional.get());
    }

    //Gnera un reporte de pedidos en Excel
    public ByteArrayInputStream generarReporteExcel(ZonedDateTime fechaDesde, ZonedDateTime fechaHasta) throws IOException {

        List<Pedido> pedidos = pedidoRepository.findByFechaBetweenWithDetalles(fechaDesde, fechaHasta);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte Pedidos");

        // Cabeceras
        String[] headers = {"Fecha Pedido", "Instrumento", "Marca", "Modelo", "Cantidad", "Precio Unitario", "Subtotal"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Datos
        int rowNum = 1;
        for (Pedido pedido : pedidos) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(pedido.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                row.createCell(1).setCellValue(detalle.getInstrumento().getInstrumento());
                row.createCell(2).setCellValue(detalle.getInstrumento().getMarca());
                row.createCell(3).setCellValue(detalle.getInstrumento().getModelo());
                row.createCell(4).setCellValue(detalle.getCantidad());
                row.createCell(5).setCellValue(detalle.getPrecioUnitario().doubleValue());
                row.createCell(6).setCellValue(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())).doubleValue());
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }




}