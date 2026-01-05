package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.Foreis;
import dao.ForeisDao;
import database.DatabaseSetup;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.*;

public class ScenarioForeisServiceTest {

  private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
  private static final String TEST_URL = "jdbc:sqlite:test_foreis.db";
  private static final String TEST_FILE = "test_foreis.db";

  private ScenarioForeisService service;

  @BeforeEach
  void setup() {
    DatabaseSetup.setURL(TEST_URL);

    // Δημιουργεί πίνακες
    DatabaseSetup.setDatabase();

    // Καθαρίζει πίνακες
    DatabaseSetup.resetTables();

    service = new ScenarioForeisService();
  }

  @AfterAll
  static void tearDown() {
    DatabaseSetup.setURL(ORIGINAL_URL);
    File f = new File(TEST_FILE);
    if (f.exists()) {
      f.delete();
    }
  }

  @Test
  void testInvalidYear() {
    assertThrows(IllegalArgumentException.class,
        () -> service.updateForeisWithModifiedBudget(
            2030, "Υπουργείο", "RegularBudget", 10));
  }

  @Test
  void testInvalidType() {
    assertThrows(IllegalArgumentException.class,
        () -> service.updateForeisWithModifiedBudget(
            2023, "Λάθος", "RegularBudget", 10));
  }

  @Test
  void testInvalidCategory() {
    assertThrows(IllegalArgumentException.class,
        () -> service.updateForeisWithModifiedBudget(
            2023, "Υπουργείο", "WrongBudget", 10));
  }

  @Test
  void testInvalidPercentageNaN() {
    assertThrows(IllegalArgumentException.class,
        () -> service.updateForeisWithModifiedBudget(
            2023, "Υπουργείο", "RegularBudget", Double.NaN));
  }

  @Test
  void testUpdateRegularBudgetSuccess() {

    ForeisDao dao = new ForeisDao();
    dao.addForeis(new Foreis(
        1, 100, 2023, "Υπουργείο", "Υπ. Παιδείας",
        1000.0, 500.0, 1500.0
    ));

    int updated = service.updateForeisWithModifiedBudget(
        2023, "Υπουργείο", "RegularBudget", 10);

    assertEquals(1, updated);

    List<Foreis> list = dao.selectForeis(2023, "Υπουργείο");
    Foreis f = list.get(0);

    assertEquals(1100.0, f.getRegularBudget());
    assertEquals(500.0, f.getPublicInvBudget());
    assertEquals(1600.0, f.getTotal());
  }

  @Test
  void testUpdatePublicInvBudgetSuccess() {

    ForeisDao dao = new ForeisDao();
    dao.addForeis(new Foreis(
        1, 200, 2024, "Υπουργείο", "Υπ. Υγείας",
        800.0, 200.0, 1000.0
    ));

    int updated = service.updateForeisWithModifiedBudget(
        2024, "Υπουργείο", "PublicInvBudget", -25);

    assertEquals(1, updated);

    Foreis f = dao.selectForeis(2024, "Υπουργείο").get(0);

    assertEquals(800.0, f.getRegularBudget());
    assertEquals(150.0, f.getPublicInvBudget());
    assertEquals(950.0, f.getTotal());
  }

  @Test
  void testNoForeisFound() {

    int updated = service.updateForeisWithModifiedBudget(
        2023, "Υπουργείο", "RegularBudget", 10);

    assertEquals(0, updated);
  }

  @Test
  void testTooLargeBudgetChangeFails() {

    ForeisDao dao = new ForeisDao();
    dao.addForeis(new Foreis(
        1, 300, 2023, "Υπουργείο", "Υπ. Οικονομικών",
        1000.0, 500.0, 1500.0
    ));

    assertThrows(IllegalArgumentException.class,
        () -> service.updateForeisWithModifiedBudget(
            2023, "Υπουργείο", "RegularBudget", 80));
  }

  @Test
  void testNegativeResultingBudgetFails() {

    ForeisDao dao = new ForeisDao();
    dao.addForeis(new Foreis(
        1, 400, 2023, "Υπουργείο", "Υπ. Μεταφορών",
        200.0, 100.0, 300.0
    ));

    assertThrows(IllegalArgumentException.class,
        () -> service.updateForeisWithModifiedBudget(
            2023, "Υπουργείο", "PublicInvBudget", -200));
  }
}
