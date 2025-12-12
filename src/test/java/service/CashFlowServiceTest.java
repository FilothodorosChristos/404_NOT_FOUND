package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
import dao.CashFlowDao;
import database.DatabaseSetup;
import java.util.List;
import org.junit.jupiter.api.*;

public class CashFlowServiceTest {

  private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
  private static final String TEST_URL = "jdbc:sqlite:test_cashflow.db";

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

  @AfterEach
  void tearDown() {
    DatabaseSetup.setURL(ORIGINAL_URL);
  }

  // -------------------------------------------------------------
  // ADD TESTS
  // -------------------------------------------------------------

  @Test
  void testAddCashflowThrowsDueToFinalTotals() {
    CashFlow c = new CashFlow(
            0, 2023, "income",
            "Salary",
            1200.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.addCashflow(c));
  }

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
            0, 2026, "income",
            "Invalid Year",
            100.0
    );

    assertThrows(IllegalArgumentException.class, () -> service.addCashflow(c));
  }

  @Test
  void testAddCashflowNull() {
    assertThrows(IllegalArgumentException.class, () -> service.addCashflow(null));
  }

  // -------------------------------------------------------------
  // UPDATE TESTS
  // -------------------------------------------------------------

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

  // -------------------------------------------------------------
  // GET TESTS
  // -------------------------------------------------------------

  @Test
  void testGetCashflowsSuccessEmptyResult() {
    List<CashFlow> list = service.getCashflows(2023, "income");
    assertNotNull(list);
    assertEquals(0, list.size());
  }

  @Test
  void testGetCashflowsInvalidYear() {
    assertThrows(IllegalArgumentException.class,
            () -> service.getCashflows(2026, "income"));
  }

  @Test
  void testGetCashflowsInvalidType() {
    assertThrows(IllegalArgumentException.class,
            () -> service.getCashflows(2023, ""));
  }

}






