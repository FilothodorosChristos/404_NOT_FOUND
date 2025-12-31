package util;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.Test;


/**
 * Test class για την ValidationUtils.
 * Ελέγχει όλους τους κανόνες validation (ποσοστιαία αλλαγή, έτος, μη αρνητικά ποσά,
 * θετικά IDs, και τελικά totals).
 */
public class ValidationUtilsTest {

  /**
   * Ελέγχει ότι η validateAmountChange δεν πετάει exception
   * όταν η αλλαγή είναι εντός του ορίου.
   */
  @Test
    public void testValidateAmountChangeWithinLimit() {
    assertDoesNotThrow(() -> ValidationUtils.validateAmountChange(100.0, 120.0));
  }

  /**
   * Ελέγχει ότι η validateAmountChange πετάει exception
   * όταν η αλλαγή ξεπερνάει το όριο.
   */
  @Test
    public void testValidateAmountChangeExceedsLimit() {
    assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validateAmountChange(100.0, 200.0));
  }

  /**
   * Ελέγχει ότι η validateYear δέχεται έτη 2021–2026.
   */
  @Test
    public void testValidateYearValid() {
    assertDoesNotThrow(() -> ValidationUtils.validateYear(2021));
    assertDoesNotThrow(() -> ValidationUtils.validateYear(2026));
  }

  /**
   * Ελέγχει ότι η validateYear πετάει exception για μη επιτρεπτά έτη.
   */
  @Test
    public void testValidateYearInvalid() {
    assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validateYear(2020));
    assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validateYear(2027));
  }

  /**
   * Ελέγχει ότι η validateNonNegative δέχεται μη αρνητικά ποσά.
   */
  @Test
    public void testValidateNonNegativeValid() {
    assertDoesNotThrow(() -> ValidationUtils.validateNonNegative(0.0)); 
    assertDoesNotThrow(() -> ValidationUtils.validateNonNegative(100.0));
  }

  /**
   * Ελέγχει ότι η validateNonNegative πετάει exception για αρνητικά ποσά.
   */
  @Test
    public void testValidateNonNegativeInvalid() {
    assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validateNonNegative(-5.0));
  }

  /**
   * Ελέγχει ότι η validatePositiveId δέχεται θετικά IDs.
   */
  @Test
    public void testValidatePositiveIdValid() {
    assertDoesNotThrow(() -> ValidationUtils.validatePositiveId(10, "Foreas ID"));
  }

  /**
   * Ελέγχει ότι η validatePositiveId πετάει exception για μη θετικά IDs.
   */
  @Test
    public void testValidatePositiveIdInvalid() {
    assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validatePositiveId(0, "Foreas ID"));
    assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.validatePositiveId(-1, "Foreas ID"));
  }

 @Test
public void testPrintBudgetStatusBalanced() {

    // Arrange
    List<CashFlow> cashflows = List.of(
            new CashFlow(1, 2023, "Έσοδο", "Income", 100.0),
            new CashFlow(2, 2023, "Έξοδο", "Expense", 100.0)
    );

    // Act
    String result = ValidationUtils.printBudgetStatus(cashflows);

    // Assert
    assertEquals("Ο προϋπολογισμός είναι ΙΣΟΣΚΕΛΙΣΜΕΝΟΣ", result);
}

@Test
public void testPrintBudgetStatusSurplus() {

    List<CashFlow> cashflows = List.of(
            new CashFlow(1, 2023, "Έσοδο", "Income", 200.0),
            new CashFlow(2, 2023, "Έξοδο", "Expense", 100.0)
    );

    String result = ValidationUtils.printBudgetStatus(cashflows);

    assertEquals("Ο προϋπολογισμός είναι ΠΛΕΟΝΑΣΜΑΤΙΚΟΣ", result);
}

@Test
public void testPrintBudgetStatusDeficit() {

    List<CashFlow> cashflows = List.of(
            new CashFlow(1, 2023, "Έσοδο", "Income", 100.0),
            new CashFlow(2, 2023, "Έξοδο", "Expense", 200.0)
    );

    String result = ValidationUtils.printBudgetStatus(cashflows);

    assertEquals("Ο προϋπολογισμός είναι ΕΛΛΕΙΜΜΑΤΙΚΟΣ", result);
}

}
