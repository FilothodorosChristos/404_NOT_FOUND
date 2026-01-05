package dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
 * Χρησιμοποιεί test database (test_db_temp.db)
 * ώστε να μην πειράξει την κανονική βάση.
 */
public class CashFlowDaoTest {

  private static final String TEST_DB_URL = "jdbc:sqlite:test_db_temp.db";
  private static final String REAL_DB_URL = "jdbc:sqlite:budgetDB.db";

  private CashFlowDao dao;

  /**
   * Εκτελείται μία φορά πριν από όλα τα tests.
   * Δημιουργεί την test database και τους πίνακες.
   */
  @BeforeAll
    public static void setupDatabase() {
    DatabaseSetup.setURL(TEST_DB_URL);
    DataImporter.setURL(TEST_DB_URL);
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
   * Εκτελείται μία φορά στο τέλος όλων των tests.
   * Επαναφέρει το URL στην κύρια βάση.
   */
  @AfterAll
    public static void restoreDatabaseURL() {
    DatabaseSetup.setURL(REAL_DB_URL);
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
                "income",
                "To Delete",
                100.0
        );

    dao.addCashFlow(c);

    CashFlow stored = dao.selectCashFlow(2023, "income").get(0);
    dao.deleteCashFlow(stored.getId());

    List<CashFlow> remaining = dao.selectCashFlow(2023, "income");
    assertTrue(remaining.isEmpty());
  }
  /**
   * Ελέγχει την αναζήτηση cashflow με βάση το ID.
   */

  @Test
public void testSelectCashFlowById() {
    CashFlow c = new CashFlow(0, 2023, "income", "ById Test", 300.0);
    dao.addCashFlow(c);

    CashFlow stored = dao.selectCashFlow(2023, "income").get(0);
    CashFlow found = dao.selectCashFlowById(stored.getId());

    assertEquals(stored.getId(), found.getId());
    assertEquals("ById Test", found.getName());
    assertEquals(300.0, found.getAmount());

    // Έλεγχος για μη υπαρκτό ID
    CashFlow notFound = dao.selectCashFlowById(9999);
    assertEquals(null, notFound);
  }
 
  @Test
public void testSelectCashFlow_emptyResult() {
    List<CashFlow> list = dao.selectCashFlow(2025, "nonexistent");
    assertTrue(list.isEmpty());
}

@Test
public void testDeleteCashFlow_nonExistingId() {
    // Δεν πρέπει να πετάξει exception αν το ID δεν υπάρχει
    dao.deleteCashFlow(9999);
}

  @Test
  public void testUpdateCashFlow_nonExisting() {
    CashFlow c = new CashFlow(9999, 2023, "income", "NonExisting", 100.0);
    // Αν το update δεν βρει την εγγραφή, 
    // δεν κάνουμε τίποτα ή πετάει exception ανάλογα με την υλοποίηση
    assertDoesNotThrow(() -> dao.updateCashFlow(c));

  }
  
  @Test
public void testSelectCashFlowById_returnNullExplicit() {
    // Βάση άδεια
    DatabaseSetup.resetTables();

    CashFlow result = dao.selectCashFlowById(1);

    assertEquals(null, result);
}


}

