package EventoController;

import Taller7.Taller7.Model.Evento;
import Taller7.Taller7.Service.EventoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

// iText imports
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

// Apache POI imports
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String listarEventos(Model model) {
        List<Evento> eventos = eventoService.listarEventos();
        model.addAttribute("eventos", eventos);
        return "lista_eventos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioEvento(Model model) {
        model.addAttribute("evento", new Evento());
        return "formularioEventos";
    }

    @PostMapping("/guardar")
    public String guardarEvento(@ModelAttribute Evento evento) {
        eventoService.guardarEvento(evento);
        return "redirect:/eventos";
    }

    @GetMapping("/editar/{id}")
    public String editarEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("evento", evento);
        return "formularioEventos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return "redirect:/eventos";
    }

    @GetMapping("/exportar/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=eventos.pdf");

        List<Evento> eventos = eventoService.listarEventos();

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Listado de Eventos"));

        float[] columnWidths = {100F, 100F, 100F, 200F};
        Table table = new Table(columnWidths);
        table.addCell("Título");
        table.addCell("Fecha");
        table.addCell("Lugar");
        table.addCell("Descripción");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Evento e : eventos) {
            table.addCell(e.getTitulo());
            table.addCell(e.getFecha() != null ? e.getFecha().format(formatter) : "");
            table.addCell(e.getLugar());
            table.addCell(e.getDescripcion());
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/exportar/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=eventos.xlsx");

        List<Evento> eventos = eventoService.listarEventos();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Eventos");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Título");
        header.createCell(1).setCellValue("Fecha");
        header.createCell(2).setCellValue("Lugar");
        header.createCell(3).setCellValue("Descripción");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int rowIdx = 1;
        for (Evento e : eventos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getTitulo());
            row.createCell(1).setCellValue(e.getFecha() != null ? e.getFecha().format(formatter) : "");
            row.createCell(2).setCellValue(e.getLugar());
            row.createCell(3).setCellValue(e.getDescripcion());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
