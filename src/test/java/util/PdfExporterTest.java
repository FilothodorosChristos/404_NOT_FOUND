package util;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
import dao.Foreis;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PdfExporterTest {

  @TempDir
  Path tempDir;

  private static final String ARIAL_PATH = "c:/windows/fonts/arial.ttf";

  private static void assumeWindowsWithArial() {
    String os = System.getProperty("os.name", "").toLowerCase();
    Assumptions.assumeTrue(os.contains("win"),
        "Skipping PDF export test: not running on Windows");
    Assumptions.assumeTrue(new File(ARIAL_PATH).exists(),
        "Skipping PDF export test: Arial font not found at " + ARIAL_PATH);
  }

  @Test
  void exportCashFlowToPdf_ShouldCreateNonEmptyPdf() {
    assumeWindowsWithArial();

    List<CashFlow> cashFlows = List.of(
        new CashFlow(1, 2023, "Έσοδο", "Μισθός", 1000.00),
        new CashFlow(2, 2023, "Έσοδο", "Bonus", 250.50)
    );

    String outputPath = tempDir.resolve("cashflows_2023.pdf").toString();

    assertDoesNotThrow(() ->
        PdfExporter.exportCashFlowToPdf(cashFlows, 2023, "Έσοδα", outputPath)
    );

    File pdf = new File(outputPath);
    assertTrue(pdf.exists(), "Το PDF αρχείο δεν δημιουργήθηκε.");
    assertTrue(pdf.length() > 0, "Το PDF αρχείο είναι άδειο.");
  }

  @Test
  void exportForeisToPdf_ShouldCreateNonEmptyPdf() {
    assumeWindowsWithArial();

    List<Foreis> foreisList = List.of(
        new Foreis(1, 101, 2023, "Τακτικός", "Υπουργείο Οικονομικών", 100.0, 50.0, 150.0),
        new Foreis(2, 102, 2023, "Τακτικός", "Υπουργείο Παιδείας", 200.0, 25.0, 225.0)
    );

    String outputPath = tempDir.resolve("foreis_2023.pdf").toString();

    assertDoesNotThrow(() ->
        PdfExporter.exportForeisToPdf(foreisList, 2023, "Τακτικός", outputPath)
    );

    File pdf = new File(outputPath);
    assertTrue(pdf.exists(), "Το PDF αρχείο δεν δημιουργήθηκε.");
    assertTrue(pdf.length() > 0, "Το PDF αρχείο είναι άδειο.");
  }
}

