package util;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
import dao.Foreis;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PdfExporterTest {

  @TempDir
  Path tempDir;

  @Test
  void exportCashFlowToPdf_ShouldCreateNonEmptyPdf() {
    // Arrange
    List<CashFlow> cashFlows = List.of(
        new CashFlow(1, 2023, "Έσοδο", "Μισθός", 1000.00),
        new CashFlow(2, 2023, "Έσοδο", "Bonus", 250.50)
    );

    String outputPath = tempDir.resolve("cashflows_2023.pdf").toString();

    // Act + Assert
    assertDoesNotThrow(() ->
        PdfExporter.exportCashFlowToPdf(cashFlows, 2023, "Έσοδα", outputPath)
    );

    File pdf = new File(outputPath);
    assertTrue(pdf.exists(), "Το PDF αρχείο δεν δημιουργήθηκε.");
    assertTrue(pdf.length() > 0, "Το PDF αρχείο είναι άδειο.");
  }

  @Test
  void exportForeisToPdf_ShouldCreateNonEmptyPdf() {
    // Arrange
    List<Foreis> foreisList = List.of(
        new Foreis(1, 101, 2023, "Τακτικός", "Υπουργείο Οικονομικών", 100.0, 50.0, 150.0),
        new Foreis(2, 102, 2023, "Τακτικός", "Υπουργείο Παιδείας", 200.0, 25.0, 225.0)
    );

    String outputPath = tempDir.resolve("foreis_2023.pdf").toString();

    // Act + Assert
    assertDoesNotThrow(() ->
        PdfExporter.exportForeisToPdf(foreisList, 2023, "Τακτικός", outputPath)
    );

    File pdf = new File(outputPath);
    assertTrue(pdf.exists(), "Το PDF αρχείο δεν δημιουργήθηκε.");
    assertTrue(pdf.length() > 0, "Το PDF αρχείο είναι άδειο.");
  }
}
