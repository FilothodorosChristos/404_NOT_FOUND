package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import database.DataImporter;
import database.DatabaseSetup;
import dto.ForeasCompareDto;
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
    public static void restoreDatabaseUrl() {
    DatabaseSetup.setURL(REAL_DB_URL);
  }

  @Test
public void testCompareYears() throws Exception {
    Foreis f1 = new Foreis(0, 301, 2023, "TYPE_C", "Foreas C", 100.0, 50.0, 150.0);
    Foreis f2 = new Foreis(0, 301, 2024, "TYPE_C", "Foreas C", 200.0, 100.0, 300.0);
    dao.addForeis(f1);
    dao.addForeis(f2);

    List<ForeasCompareDto> compareList = dao.compareYears(2023, 2024);

    assertEquals(1, compareList.size());
    ForeasCompareDto dto = compareList.get(0);

    assertEquals(301, dto.getForeasId());
    assertEquals("Foreas C", dto.getName());

    assertEquals(100.0, dto.getRegularYear1());
    assertEquals(200.0, dto.getRegularYear2());
    assertEquals(100.0, dto.getRegularDiff());
    assertEquals(100.0, dto.getRegularPercentChange());

    assertEquals(50.0, dto.getPublicInvYear1());
    assertEquals(100.0, dto.getPublicInvYear2());
    assertEquals(50.0, dto.getPublicInvDiff());
    assertEquals(100.0, dto.getPublicInvPercentChange());

    assertEquals(150.0, dto.getTotalYear1());
    assertEquals(300.0, dto.getTotalYear2());
    assertEquals(150.0, dto.getTotalDiff());
    assertEquals(100.0, dto.getTotalPercentChange());
  }

  @Test
public void testSelectForeis_emptyResult() {
    List<Foreis> list = dao.selectForeis(2025, "NON_EXISTENT_TYPE");
    assertTrue(list.isEmpty());
  }

  @Test
public void testDeleteForeis_nonExistingId() {
    // απλώς δεν πρέπει να πετάξει exception
    dao.deleteForeis(9999);
  }

  @Test
public void testSelectForeis_multipleEntries() {
    Foreis f1 = new Foreis(0, 401, 2023, "TYPE_MULTI", "Foreas 1", 10, 20, 30);
    Foreis f2 = new Foreis(0, 402, 2023, "TYPE_MULTI", "Foreas 2", 15, 25, 40);
    dao.addForeis(f1);
    dao.addForeis(f2);

    List<Foreis> list = dao.selectForeis(2023, "TYPE_MULTI");
    assertEquals(2, list.size());
    assertEquals("Foreas 1", list.get(0).getName());
    assertEquals("Foreas 2", list.get(1).getName());
  }

  @Test
  public void testCompareYears_noEntries() throws Exception {
    List<ForeasCompareDto> compareList = dao.compareYears(2023, 2025);
    assertTrue(compareList.isEmpty());
  }

  @Test
  public void testUpdateForeis_nonExistingId() {
    Foreis f = new Foreis(9999, 601, 2023, "TYPE_X", "NonExisting", 10, 20, 30);
    // απλώς δεν πρέπει να πετάξει exception
    dao.updateForeis(f);
  }

  @Test
  public void testAddForeis_totalMismatch() {
    Foreis f = new Foreis(0, 701, 2023, "TYPE_Y", "Mismatch", 10, 20, 40);
    dao.addForeis(f);
    List<Foreis> list = dao.selectForeis(2023, "TYPE_Y");
    assertEquals(1, list.size());
    assertEquals(40, list.get(0).getTotal());
  }

}




