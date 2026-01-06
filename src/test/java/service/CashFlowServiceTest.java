package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
import dao.CashFlowDao;
import database.DatabaseSetup;
import dto.CashFlowCompareDto;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.*;

public class CashFlowServiceTest {

  private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
  private static final String TEST_URL = "jdbc:sqlite:test_cashflow.db";
  private static final String TEST_FILE = "test_cashflow.db";

  private CashFlowService service;

  @BeforeEach
  void setup() {

    DatabaseSetup.setURL(TEST_URL);

    // Δημιουργεί πίνακες
    DatabaseSetup.setDatabase();

    // Καθαρίζει πίνακες
    DatabaseSetup.resetTables();

    service = new CashFlowService();
  }

  @AfterAll
  static void tearDown() {
    DatabaseSetup.setURL(ORIGINAL_URL);
    File f = new File(TEST_FILE); 
    if (f.exists()) {
      f.delete();
    }
  }

  // -------------------------------------------------------------
  // ADD TESTS
  // -------------------------------------------------------------
 
  @Test
  void testAddCashflowInvalidAmount() {
    CashFlow c = new CashFlow(
            0, 2023, "income",
            "Invalid",
            -10.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.addCashflow(c));
  }

  @Test
  void testAddCashflowInvalidYear() {
    CashFlow c = new CashFlow(
            0, 2027, "income",
            "Invalid Year",
            100.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.addCashflow(c));
  }

  @Test
  void testAddCashflowNull() {
    assertThrows(IllegalArgumentException.class, () -> service.addCashflow(null));
  }

  @Test
  void testUpdateCashflowInvalidId() {
    CashFlow updated = new CashFlow(
            0,
            2023,
            "income",
            "Invalid ID",
            100.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.updateCashflow(updated));
  }

  @Test
  void testUpdateCashflowNotFound() {
    CashFlow updated = new CashFlow(
            9999,
            2023,
            "income",
            "Not Found",
            100.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.updateCashflow(updated));
  }

  @Test
  void testUpdateCashflowTooLargeChangeWithExistingRow() {

    CashFlowDao dao = new CashFlowDao();
    dao.addCashFlow(new CashFlow(
            1, 2023, "income", "Base", 100.0
    ));

    CashFlow updated = new CashFlow(
            1, 2023, "income", "Too Large", 300.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.updateCashflow(updated));
  }

  @Test
  void testUpdateCashflowValidIdButValidationFails() {

    CashFlowDao dao = new CashFlowDao();
    dao.addCashFlow(new CashFlow(
            1, 2023, "income", "Base", 100.0
    ));

    CashFlow updated = new CashFlow(
            1, 2023, "income", "Updated", 150.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.updateCashflow(updated));
  }

  // -------------------------------------------------------------
  // DELETE TESTS
  // -------------------------------------------------------------

  @Test
  void testDeleteCashflowInvalidId() {
    assertThrows(IllegalArgumentException.class, () -> service.deleteCashflow(0));
  }

  @Test
  void testDeleteCashflowExistingId() {

    CashFlowDao dao = new CashFlowDao();
    dao.addCashFlow(new CashFlow(
            1, 2023, "income", "ToDelete", 100.0
    ));

    assertDoesNotThrow(() -> service.deleteCashflow(1));

    List<CashFlow> list = service.getCashflows(2023, "income");
    assertEquals(0, list.size());
  }

  @Test
  void testDeleteCashflowNotFound() {
    assertDoesNotThrow(() -> service.deleteCashflow(9999));
  }

  
  // GET TESTS

  @Test
  void testGetCashflowsSuccessEmptyResult() {
    List<CashFlow> list = service.getCashflows(2023, "income");
    assertNotNull(list);
    assertEquals(0, list.size());
  }

  @Test
  void testGetCashflowsInvalidYear() {
    assertThrows(IllegalArgumentException.class,
            () -> service.getCashflows(2027, "income"));
  }

  @Test
  void testGetCashflowsInvalidType() {
    assertThrows(IllegalArgumentException.class,
            () -> service.getCashflows(2023, ""));
  }

  @Test
void testCompareCashFlows() {
    CashFlowDao dao = new CashFlowDao();

    // -------------------------
    // Έτος 2023
    // -------------------------
    dao.addCashFlow(new CashFlow(0, 2023, "income", "Salary", 2000.0));
    dao.addCashFlow(new CashFlow(0, 2023, "income", "Bonus", 500.0));
    dao.addCashFlow(new CashFlow(0, 2023, "income", "Other Income", 300.0));

    // -------------------------
    // Έτος 2024
    // -------------------------
    dao.addCashFlow(new CashFlow(0, 2024, "income", "Salary", 2200.0));        // κοινό όνομα
    dao.addCashFlow(new CashFlow(0, 2024, "income", "Gift", 400.0));          // νέο όνομα
    dao.addCashFlow(new CashFlow(0, 2024, "income", "Bonus", 600.0));         // κοινό όνομα με αλλαγή

    // Κλήση μεθόδου compareCashFlows
    List<CashFlowCompareDto> comparison = service.compareCashFlows(2023, 2024, "income");

    assertEquals(4, comparison.size()); // Salary, Bonus, Other Income, Gift → 5 διαφορετικά ονόματα

    for (CashFlowCompareDto dto : comparison) {
      switch (dto.getName()) {
        case "Salary" -> {
          assertEquals(2000.0, dto.getAmountYear1());
          assertEquals(2200.0, dto.getAmountYear2());
          assertFalse(dto.isMissingInYear1());
          assertFalse(dto.isMissingInYear2());
        }
        case "Bonus" -> {
          assertEquals(500.0, dto.getAmountYear1());
          assertEquals(600.0, dto.getAmountYear2());
          assertFalse(dto.isMissingInYear1());
          assertFalse(dto.isMissingInYear2());
        }
        case "Other Income" -> {
          assertEquals(300.0, dto.getAmountYear1());
          assertEquals(0.0, dto.getAmountYear2());
          assertFalse(dto.isMissingInYear1());
          assertTrue(dto.isMissingInYear2());
        }
        case "Gift" -> {
          assertEquals(0.0, dto.getAmountYear1());
          assertEquals(400.0, dto.getAmountYear2());
          assertTrue(dto.isMissingInYear1());
          assertFalse(dto.isMissingInYear2());
        }
        default -> fail("Unexpected cashflow name: " + dto.getName());
      }
    }
  }

}
