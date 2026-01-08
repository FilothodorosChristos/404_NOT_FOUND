package util;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import dao.CashFlow;
import dao.Foreis;
import java.io.File;
import java.util.List;

/**
 * Utility class για εξαγωγή δεδομένων σε PDF format με UTF-8 encoding.
 * Δημιουργεί PDF αρχεία με λευκό background και μαύρα γράμματα.
 */
public class PdfExporter {

  private static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
  private static final DeviceRgb BLACK = new DeviceRgb(0, 0, 0);
  private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(240, 240, 240);

  /**
   * Εξάγει CashFlow δεδομένα σε PDF αρχείο με UTF-8 encoding.
   *
   * @param cashFlows λίστα με CashFlow εγγραφές
   * @param year το έτος των δεδομένων
   * @param type ο τύπος (Έσοδα ή Έξοδα)
   * @param outputPath το path όπου θα αποθηκευτεί το PDF
   * @throws Exception αν υπάρξει σφάλμα κατά τη δημιουργία του PDF
   */
  public static void exportCashFlowToPdf(List<CashFlow> cashFlows, int year, 
                                          String type, String outputPath) throws Exception {
    File file = new File(outputPath);
    PdfWriter writer = new PdfWriter(file);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    // Δημιουργία font με πλήρη UTF-8 support για ελληνικά
    // Χρησιμοποιούμε το Arial που υπάρχει στο σύστημα
    PdfFont font = PdfFontFactory.createFont("c:/windows/fonts/arial.ttf", 
        PdfEncodings.IDENTITY_H, 
        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
    document.setFont(font);

    // Λευκό background για όλο το document
    document.setBackgroundColor(WHITE);

    // Τίτλος
    Paragraph title = new Paragraph("GoverLens - Αναφορά " + type + " " + year)
        .setFontSize(18)
        .setBold()
        .setFontColor(BLACK)
        .setTextAlignment(TextAlignment.CENTER)
        .setMarginBottom(20);
    document.add(title);

    // Δημιουργία πίνακα με 4 στήλες
    Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 4, 3}));
    table.setWidth(UnitValue.createPercentValue(100));

    // Headers
    addHeaderCell(table, "ID", font);
    addHeaderCell(table, "Έτος", font);
    addHeaderCell(table, "Όνομα", font);
    addHeaderCell(table, "Ποσό (€)", font);

    // Δεδομένα
    for (CashFlow cf : cashFlows) {
      addDataCell(table, String.valueOf(cf.getId()), false, font);
      addDataCell(table, String.valueOf(cf.getYearId()), false, font);
      addDataCell(table, cf.getName(), false, font);
      addDataCell(table, String.format("%.2f", cf.getAmount()), true, font);
    }

    document.add(table);

    // Υπολογισμός συνόλου
    double total = cashFlows.stream().mapToDouble(CashFlow::getAmount).sum();
    Paragraph totalParagraph = new Paragraph(
        String.format("Σύνολο: %.2f €", total))
        .setFontSize(12)
        .setBold()
        .setFontColor(BLACK)
        .setTextAlignment(TextAlignment.RIGHT)
        .setMarginTop(10);
    document.add(totalParagraph);

    document.close();
  }

  /**
   * Εξάγει Foreis δεδομένα σε PDF αρχείο με UTF-8 encoding.
   *
   * @param foreisList λίστα με Foreis εγγραφές
   * @param year το έτος των δεδομένων
   * @param type ο τύπος φορέα
   * @param outputPath το path όπου θα αποθηκευτεί το PDF
   * @throws Exception αν υπάρξει σφάλμα κατά τη δημιουργία του PDF
   */
  public static void exportForeisToPdf(List<Foreis> foreisList, int year, 
                                        String type, String outputPath) throws Exception {
    File file = new File(outputPath);
    PdfWriter writer = new PdfWriter(file);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    // Δημιουργία font με πλήρη UTF-8 support για ελληνικά
    // Χρησιμοποιούμε το Arial που υπάρχει στο σύστημα
    PdfFont font = PdfFontFactory.createFont("c:/windows/fonts/arial.ttf", 
        PdfEncodings.IDENTITY_H, 
        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
    document.setFont(font);

    // Λευκό background για όλο το document
    document.setBackgroundColor(WHITE);

    // Τίτλος
    Paragraph title = new Paragraph("GoverLens - Αναφορά Φορέων " + type + " " + year)
        .setFontSize(18)
        .setBold()
        .setFontColor(BLACK)
        .setTextAlignment(TextAlignment.CENTER)
        .setMarginBottom(20);
    document.add(title);

    // Δημιουργία πίνακα με 6 στήλες
    Table table = new Table(UnitValue.createPercentArray(
        new float[]{1, 1.5f, 1, 4, 2.5f, 2.5f}));
    table.setWidth(UnitValue.createPercentValue(100));

    // Headers
    addHeaderCell(table, "ID", font);
    addHeaderCell(table, "ID Φορέα", font);
    addHeaderCell(table, "Έτος", font);
    addHeaderCell(table, "Όνομα", font);
    addHeaderCell(table, "Τακτικός Π/Υ (€)", font);
    addHeaderCell(table, "Σύνολο (€)", font);

    // Δεδομένα
    for (Foreis f : foreisList) {
      addDataCell(table, String.valueOf(f.getId()), false, font);
      addDataCell(table, String.valueOf(f.getForeasId()), false, font);
      addDataCell(table, String.valueOf(f.getYearId()), false, font);
      addDataCell(table, f.getName(), false, font);
      addDataCell(table, String.format("%.2f", f.getRegularBudget()), true, font);
      addDataCell(table, String.format("%.2f", f.getTotal()), true, font);
    }

    document.add(table);

    // Υπολογισμός συνόλου
    double total = foreisList.stream().mapToDouble(Foreis::getTotal).sum();
    Paragraph totalParagraph = new Paragraph(
        String.format("Συνολικό Ποσό: %.2f €", total))
        .setFontSize(12)
        .setBold()
        .setFontColor(BLACK)
        .setTextAlignment(TextAlignment.RIGHT)
        .setMarginTop(10);
    document.add(totalParagraph);

    document.close();
  }

  /**
   * Προσθέτει header cell στον πίνακα με UTF-8 font.
   */
  private static void addHeaderCell(Table table, String text, PdfFont font) {
    Cell cell = new Cell()
        .add(new Paragraph(text).setBold().setFontSize(10).setFont(font))
        .setBackgroundColor(LIGHT_GRAY)
        .setFontColor(BLACK)
        .setTextAlignment(TextAlignment.CENTER)
        .setPadding(5);
    table.addHeaderCell(cell);
  }

  /**
   * Προσθέτει data cell στον πίνακα με UTF-8 font.
   */
  private static void addDataCell(Table table, String text, boolean alignRight, PdfFont font) {
    Cell cell = new Cell()
        .add(new Paragraph(text).setFontSize(9).setFont(font))
        .setBackgroundColor(WHITE)
        .setFontColor(BLACK)
        .setPadding(5);
    
    if (alignRight) {
      cell.setTextAlignment(TextAlignment.RIGHT);
    } else {
      cell.setTextAlignment(TextAlignment.LEFT);
    }
    
    table.addCell(cell);
  }
}