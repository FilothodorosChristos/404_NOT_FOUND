package service;

import database.DataImporter;
import database.DatabaseSetup;
import org.junit.jupiter.api.*;
import util.DbExistsChecker;

import java.io.File;

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
        DataImporter.setURL("jdbc:sqlite:" + TEST_DB);
        DatabaseSetup.setURL(TEST_DB);

        // Διαγραφή της test DB αν υπάρχει
        File file = new File(TEST_DB);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Διαγράφει τη test DB μετά από κάθε τεστ για καθαρό state.
     */
    @AfterEach
    void cleanup() {
        File file = new File(TEST_DB);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Επαναφέρει τη βάση δεδομένων στην κανονική μετά από όλα τα τεστ.
     */
    @AfterAll
    static void restoreDbFile() {
        DbExistsChecker.setDbFile(REAL_DB);
        DataImporter.setURL("jdbc:sqlite:" + REAL_DB);
    }

    /**
     * Ελέγχει ότι η μέθοδος δεν καλεί importer αν η βάση υπάρχει ήδη.
     */
    @Test
    @DisplayName("Δεν καλεί importer αν η βάση υπάρχει")
    void testStartIfDatabaseMissing_WhenDbExists() throws Exception {
        File file = new File(TEST_DB);
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
