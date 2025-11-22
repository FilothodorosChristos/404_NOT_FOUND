package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import database.DataImporter;
import database.DatabaseSetup;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class για την CashFlowDao.
 * Χρησιμοποιεί test database (testdbtemp.db)
 * ώστε να μην πειράξει την κανονική βάση.
 */
public class CashFlowDaoTest {

  private static final String TEST_DB_URL = "jdbc:sqlite:testdbtemp.db";
  private static final String REAL_DB_URL = "jdbc:sqlite:budgetDB.db";

  private CashFlowDao dao;

  /**
   * Εκτελείται μία φορά πριν από όλα τα tests.
   * Δημιουργεί την test database και τους πίνακες.
   */
  @BeforeAll
    public static void setupDatabase() {
    DatabaseSetup.setURL(TEST_DB_URL);
    DatabaseSetup.setDatabase();
    DataImporter.importer();
  }

  /**
   * Εκτελείται πριν από κάθε test.
   * Δημιουργεί νέο αντικείμενο dao και καθαρίζει τη βάση.
   */
  @BeforeEach
    public void init() {
    dao = new CashFlowDao();
    DatabaseSetup.resetTables();
  }

  /**
   * Ελέγχει την εισαγωγή και επιλογή εγγραφής.
   */
  @Test
    public void testAddAndSelectCashFlow() {
    CashFlow c = new CashFlow(
                0,
                2023,
                "income",
                "Test Income",
                500.0
        );

    dao.addCashFlow(c);

    List<CashFlow> list = dao.selectCashFlow(2023, "income");

    assertEquals(1, list.size());
    assertEquals("Test Income", list.get(0).getName());
    assertEquals(2023, list.get(0).getYearId());
    assertEquals("income", list.get(0).getType());
    assertEquals(500.0, list.get(0).getAmount());
  }

  /**
   * Ελέγχει την ενημέρωση μιας εγγραφής.
   */
  @Test
    public void testUpdateCashFlow() {
    CashFlow c = new CashFlow(
                0,
                2023,
                "expense",
                "Old Name",
                200.0
        );

    dao.addCashFlow(c);

    CashFlow stored = dao.selectCashFlow(2023, "expense").get(0);
    stored.setName("New Name");
    stored.setAmount(250.0);

    dao.updateCashFlow(stored);

    CashFlow updated = dao.selectCashFlow(2023, "expense").get(0);
    assertEquals("New Name", updated.getName());
    assertEquals(250.0, updated.getAmount());
  }

  /**
   * Ελέγχει τη διαγραφή μιας εγγραφής.
   */
  @Test
    public void testDeleteCashFlow() {
    CashFlow c = new CashFlow(
                0,
                2023,
                "To Delete",
                "income",
                100.0
        );

    dao.addCashFlow(c);

    CashFlow stored = dao.selectCashFlow(2023, "income").get(0);
    dao.deleteCashFlow(stored.getId());

    List<CashFlow> remaining = dao.selectCashFlow(2023, "income");
    assertTrue(remaining.isEmpty());
  }

  /**
   * Εκτελείται μία φορά στο τέλος όλων των tests.
   * Επαναφέρει το URL στην κύρια βάση.
   */
  @AfterAll
    public static void restoreDatabaseURL() {
    DatabaseSetup.setURL(REAL_DB_URL);
  }
}

