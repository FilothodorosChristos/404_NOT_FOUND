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
 * Test class για την ForeisDao.
 * Χρησιμοποιεί test database (test_db_temp.db)
 * ώστε να μην πειράξει την κανονική βάση.
 */
public class ForeisDaoTest {

  private static final String TEST_DB_URL = "jdbc:sqlite:test_db_temp.db";
  private static final String REAL_DB_URL = "jdbc:sqlite:budgetDB.db";

  private ForeisDao dao;

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
    dao = new ForeisDao();
    DatabaseSetup.resetTables();
  }

  /**
   * Ελέγχει την εισαγωγή και επιλογή εγγραφής.
   */
  @Test
    public void testAddAndSelectForeis() {
    Foreis f = new Foreis(
                0,
                101,
                2023,
                "TYPE_X",
                "Test Foreas",
                100.0,
                50.0,
                150.0
        );

    dao.addForeis(f);
    List<Foreis> list = dao.selectForeis(2023, "TYPE_X");

    assertEquals(1, list.size());
    assertEquals("Test Foreas", list.get(0).getName());
  }

  /**
   * Ελέγχει την ενημέρωση μιας εγγραφής.
   */
  @Test
    public void testUpdateForeis() {
    Foreis f = new Foreis(
                0,
                102,
                2023,
                "TYPE_Y",
                "Old Name",
                10.0,
                20.0,
                30.0
        );
    dao.addForeis(f);

    Foreis stored = dao.selectForeis(2023, "TYPE_Y").get(0);
    stored.setName("New Name");

    dao.updateForeis(stored);

    Foreis updated = dao.selectForeis(2023, "TYPE_Y").get(0);
    assertEquals("New Name", updated.getName());
  }

  /**
   * Ελέγχει τη διαγραφή μιας εγγραφής.
   */
  @Test
    public void testDeleteForeis() {
    Foreis f = new Foreis(
                0,
                103,
                2023,
                "TYPE_Z",
                "To Delete",
                5.0,
                10.0,
                15.0
        );
    dao.addForeis(f);

    Foreis stored = dao.selectForeis(2023, "TYPE_Z").get(0);
    dao.deleteForeis(stored.getId());

    List<Foreis> remaining = dao.selectForeis(2023, "TYPE_Z");
    assertTrue(remaining.isEmpty());
  }
  /**
   * Ελέγχει την αναζήτηση foreis με βάση το ID.
   */

  @Test
public void testSelectForeisById() {
    Foreis f = new Foreis(0, 200, 2023, "TYPE_TEST", "ById Foreas", 100.0, 50.0, 150.0);
    dao.addForeis(f);

    Foreis stored = dao.selectForeis(2023, "TYPE_TEST").get(0);
    Foreis found = dao.selectForeisById(stored.getId());

    assertEquals(stored.getId(), found.getId());
    assertEquals("ById Foreas", found.getName());
    assertEquals(150.0, found.getTotal());

    // Έλεγχος για μη υπαρκτό ID
    Foreis notFound = dao.selectForeisById(9999);
    assertEquals(null, notFound);
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




