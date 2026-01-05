package service;

import database.DataImporter;
import database.DatabaseSetup;
import java.io.File;
import org.junit.jupiter.api.*;
import util.DbExistsChecker;



import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests για την κλάση {@link SimulationService}.
 * Ελέγχουν τη συμπεριφορά της με βάση την ύπαρξη ή όχι της βάσης δεδομένων.
 */
class SimulationServiceTest {

  private static final String TEST_DB = "test_db_temp.db";
  private static final String REAL_DB = "budgetDB.db"; // Κανονική βάση

  /**
   * Ρυθμίζει τη test DB πριν από κάθε τεστ.
   */
  @BeforeEach
    void setup() {
    DbExistsChecker.setDbFile(TEST_DB);

    File f = new File(TEST_DB);
    if (f.exists()) {
      f.delete();
    }
    DataImporter.setURL("jdbc:sqlite:" + TEST_DB);
    DatabaseSetup.setURL("jdbc:sqlite:" + TEST_DB);
  }


  /**
   * Επαναφέρει τη βάση δεδομένων στην κανονική μετά από όλα τα τεστ.
   */
  @AfterAll
    static void restoreDbFile() {
    DbExistsChecker.setDbFile(REAL_DB);
    DataImporter.setURL("jdbc:sqlite:" + REAL_DB);
    DatabaseSetup.setURL("jdbc:sqlite:" + REAL_DB);
    File f = new File(TEST_DB);
    if (f.exists()) {
      f.delete();
    }
  }
  /**
   * Ελέγχει ότι η μέθοδος δεν καλεί importer αν η βάση υπάρχει ήδη.
   */

  @Test
  @DisplayName("Δεν καλεί importer αν η βάση υπάρχει")
    void testStartIfDatabaseMissing_WhenDbExists() throws Exception {
    File file = new File(TEST_DB);  
    // Προσοχή μπορει να προκαλεσει θεματα στο μελλον αν το αρχειο δεν υπαρχει ηδη
    assertTrue(file.createNewFile(), "Δε μπόρεσε να δημιουργηθεί η test DB");

    SimulationService.startIfDatabaseMissing();

    assertTrue(file.exists(), "Η test DB πρέπει να υπάρχει μετά την κλήση");
  }

  /**
   * Ελέγχει ότι η μέθοδος καλεί importer αν η βάση ΔΕΝ υπάρχει.
   */
  @Test
  @DisplayName("Καλεί importer αν η βάση ΔΕΝ υπάρχει")
    void testStartIfDatabaseMissing_WhenDbMissing() {
    SimulationService.startIfDatabaseMissing();

    File file = new File(TEST_DB);
    assertTrue(file.exists(), "Η test DB πρέπει να δημιουργηθεί από τον importer");
  }

  /**
   * Ελέγχει ότι η startNewSimulation καλείται όταν η βάση λείπει.
   */

  @Test
  @DisplayName("Καλεί πάντα startNewSimulation από startIfDatabaseMissing όταν η DB λείπει")
    void testStartNewSimulationCalled() {
    SimulationService.startIfDatabaseMissing();

    File file = new File(TEST_DB);
    assertTrue(file.exists(), "startNewSimulation πρέπει να καλέσει importer αν η DB λείπει");
  }
}
