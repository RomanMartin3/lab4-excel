package com.utn.frm.instrumentos.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.utn.frm.instrumentos.entities.Instrumento;
import com.utn.frm.instrumentos.repositories.InstrumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class InstrumentoService {

    @Autowired
    private InstrumentoRepository instrumentoRepository;

    public byte[] generarPdfInstrumento(Long id) throws IOException {
        Instrumento instrumento = instrumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instrumento no encontrado"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf); // Por defecto, usa A4 vertical

        // No agregamos el título directamente al documento aquí, ya que irá dentro de la tabla.

        // Crear una tabla con dos columnas para el diseño
        Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
        table.setWidth(UnitValue.createPercentValue(100));
        //table.setMarginTop(20);

        // Celda izquierda: Imagen y Descripción
        Cell leftCell = new Cell();
        leftCell.setBorder(Border.NO_BORDER);
        leftCell.setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.TOP);

        // Agregar Imagen a la Celda Izquierda
        try {
            ClassPathResource imageResource = new ClassPathResource("images/" + instrumento.getImagen());
            Image image = new Image(ImageDataFactory.create(imageResource.getURL()))
                    .setWidth(UnitValue.createPercentValue(90))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            leftCell.add(image);
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }

        // Agregar Descripción a la Celda Izquierda
        leftCell.add(new Paragraph("\nDescripción:").setBold());
        leftCell.add(new Paragraph(instrumento.getDescripcion()));

        table.addCell(leftCell);

        // Celda derecha: Detalles del Instrumento con la línea divisoria y nuevo orden
        Cell rightCell = new Cell();
        rightCell.setBorderLeft(new SolidBorder(ColorConstants.GRAY, 1));
        rightCell.setBorder(Border.NO_BORDER);
        rightCell.setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.TOP);
        rightCell.setPaddingLeft(10); // Espacio entre la línea y el texto

        // 1. "Vendidos" (más chico y en gris claro)
        Paragraph vendidosParagraph = new Paragraph("Vendidos: " + instrumento.getCantidadVendida())
                .setFontSize(9) // Más chico
                .setFontColor(ColorConstants.LIGHT_GRAY); // Gris claro
        rightCell.add(vendidosParagraph);

        // 2. Título del instrumento
        Paragraph instrumentTitle = new Paragraph(instrumento.getInstrumento())
                .setBold()
                .setFontSize(16); // Tamaño un poco más grande para el título dentro de la columna
        rightCell.add(instrumentTitle);

        // 3. Marca y Modelo (mantenerlo normal, o puedes ajustar el tamaño si quieres)
        rightCell.add(new Paragraph("Marca: " + instrumento.getMarca()));
        rightCell.add(new Paragraph("Modelo: " + instrumento.getModelo()));

        // 4. Precio (bastante grande)
        Paragraph precioParagraph = new Paragraph("$" + instrumento.getPrecio())
                .setBold()
                .setFontSize(20) // Bastante grande
                .setFontColor(ColorConstants.BLACK); // Color negro para el precio
        rightCell.add(precioParagraph);

        // 5. Costo de envío (con colores)
        Paragraph costoEnvioParagraph;
        if (instrumento.getCostoEnvio().equals("G")) {
            costoEnvioParagraph = new Paragraph("Costo de envío: Gratis")
                    .setFontColor(ColorConstants.GREEN)
                    .setBold();
        } else {
            costoEnvioParagraph = new Paragraph("Costo de envío: $" + instrumento.getCostoEnvio())
                    .setFontColor(ColorConstants.ORANGE)
                    .setBold();
        }
        rightCell.add(costoEnvioParagraph);


        table.addCell(rightCell);

        document.add(table);

        document.close();
        return outputStream.toByteArray();
    }
}