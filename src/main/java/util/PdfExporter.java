package util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
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
 * Utility class για εξαγωγή δεδομένων σε PDF format.
 * Δημιουργεί PDF αρχεία με λευκό background και μαύρα γράμματα.
 */
public class PdfExporter {

  private static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);
  private static final DeviceRgb BLACK = new DeviceRgb(0, 0, 0);
  private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(240, 240, 240);

  /**
   * Εξάγει CashFlow δεδομένα σε PDF αρχείο.
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
    addHeaderCell(table, "ID");
    addHeaderCell(table, "Έτος");
    addHeaderCell(table, "Όνομα");
    addHeaderCell(table, "Ποσό (€)");

    // Δεδομένα
    for (CashFlow cf : cashFlows) {
      addDataCell(table, String.valueOf(cf.getId()), false);
      addDataCell(table, String.valueOf(cf.getYearId()), false);
      addDataCell(table, cf.getName(), false);
      addDataCell(table, String.format("%.2f", cf.getAmount()), true);
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
   * Εξάγει Foreis δεδομένα σε PDF αρχείο.
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
    addHeaderCell(table, "ID");
    addHeaderCell(table, "ID Φορέα");
    addHeaderCell(table, "Έτος");
    addHeaderCell(table, "Όνομα");
    addHeaderCell(table, "Τακτικός Π/Υ (€)");
    addHeaderCell(table, "Σύνολο (€)");

    // Δεδομένα
    for (Foreis f : foreisList) {
      addDataCell(table, String.valueOf(f.getId()), false);
      addDataCell(table, String.valueOf(f.getForeasId()), false);
      addDataCell(table, String.valueOf(f.getYearId()), false);
      addDataCell(table, f.getName(), false);
      addDataCell(table, String.format("%.2f", f.getRegularBudget()), true);
      addDataCell(table, String.format("%.2f", f.getTotal()), true);
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
   * Προσθέτει header cell στον πίνακα.
   */
  private static void addHeaderCell(Table table, String text) {
    Cell cell = new Cell()
        .add(new Paragraph(text).setBold().setFontSize(10))
        .setBackgroundColor(LIGHT_GRAY)
        .setFontColor(BLACK)
        .setTextAlignment(TextAlignment.CENTER)
        .setPadding(5);
    table.addHeaderCell(cell);
  }

  /**
   * Προσθέτει data cell στον πίνακα.
   */
  private static void addDataCell(Table table, String text, boolean alignRight) {
    Cell cell = new Cell()
        .add(new Paragraph(text).setFontSize(9))
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