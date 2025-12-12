package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
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

    DatabaseSetup.resetTables();

    service = new CashFlowService();
  }

  @AfterEach
    void tearDown() {

    DatabaseSetup.setURL(ORIGINAL_URL);
  }

  @Test
    void testAddCashflowSuccess() {
    CashFlow c = new CashFlow(
                0, 2023, "income",
                "Salary",
                1200.0
        );

    assertDoesNotThrow(() -> service.addCashflow(c));

    List<CashFlow> list = service.getCashflows(2023, "income");
    assertEquals(1, list.size());
    assertEquals("Salary", list.get(0).getName());
    assertEquals(1200.0, list.get(0).getAmount());
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

  @Test
    void testUpdateCashflowSuccess() {

    CashFlow c = new CashFlow(
                0, 2023, "expense",
                "Old Name",
                200.0
        );
    service.addCashflow(c);

    CashFlow existing = service.getCashflows(2023, "expense").get(0);

    CashFlow updated = new CashFlow(
                existing.getId(),
                2023,
                "expense",
                "New Name",
                250.0
        );

    assertDoesNotThrow(() -> service.updateCashflow(updated));

    CashFlow after = service.getCashflows(2023, "expense").get(0);
    assertEquals("New Name", after.getName());
    assertEquals(250.0, after.getAmount());
  }

  @Test
    void testUpdateCashflowTooLargeChange() {

    CashFlow c = new CashFlow(
                0, 2023, "income",
                "Base",
                100.0
        );
    service.addCashflow(c);

    CashFlow existing = service.getCashflows(2023, "income").get(0);

    CashFlow updated = new CashFlow(
                existing.getId(),
                2023,
                "income",
                "Too Large",
                300.0
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
    void testDeleteCashflowSuccess() {
    CashFlow c = new CashFlow(
                0, 2023, "income",
                "To Delete",
                100.0
        );
    service.addCashflow(c);

    CashFlow existing = service.getCashflows(2023, "income").get(0);

    assertDoesNotThrow(() -> service.deleteCashflow(existing.getId()));

    List<CashFlow> list = service.getCashflows(2023, "income");
    assertEquals(0, list.size());
  }

  @Test
    void testDeleteCashflowInvalidId() {
    assertThrows(IllegalArgumentException.class, () -> service.deleteCashflow(0));
  }

  @Test
    void testGetCashflowsSuccess() {
    CashFlow c = new CashFlow(
                0, 2023, "income",
                "Bonus",
                500.0
        );
    service.addCashflow(c);

    List<CashFlow> list = service.getCashflows(2023, "income");

    assertEquals(1, list.size());
    assertEquals("Bonus", list.get(0).getName());
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


