package util;

import org.junit.jupiter.api.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests για την κλάση {@link DbExistsChecker}.
 * Ελέγχουμε τη μέθοδο {@link DbExistsChecker#databaseExists()}
 * σε περιπτώσεις όπου το αρχείο υπάρχει ή δεν υπάρχει.
 */
class DbExistsCheckerTest {

    private static final String TEST_DB = "test_db_temp.db";
    private static final String NON_EXISTENT_DB = "nonexistent.db";
    private static final String ORIGINAL_DB = "budgetDB.db";

    /**
     * Πριν από κάθε τεστ, ρυθμίζουμε το DbExistsChecker να δείχνει στο test DB.
     */
    @BeforeEach
    void setup() {
        DbExistsChecker.setDbFile(TEST_DB);
    }

    /**
     * Μετά από όλα τα τεστ, επαναφέρουμε το dbFile στην πραγματική βάση.
     */
    @AfterAll
    static void restoreDbFile() {
        DbExistsChecker.setDbFile(ORIGINAL_DB);
        File f = new File(TEST_DB);
        if (f.exists()) f.delete();
    }

    /**
     * Ελέγχουμε ότι η μέθοδος {@link DbExistsChecker#databaseExists()}
     * επιστρέφει true όταν το αρχείο υπάρχει.
     */
    @Test
    @DisplayName("Η βάση υπάρχει")
    void testDatabaseExistsTrue() {
        File file = new File(TEST_DB);
        try {
            if (!file.exists()) {
                assertTrue(file.createNewFile(), "Δε μπόρεσε να δημιουργηθεί το test db αρχείο");
            }

            assertTrue(DbExistsChecker.databaseExists(), "Η μέθοδος πρέπει να επιστρέφει true αν υπάρχει το αρχείο");
        } catch (Exception e) {
            fail("Σφάλμα κατά τη δημιουργία του test αρχείου: " + e.getMessage());
        }
    }

    /**
     * Ελέγχουμε ότι η μέθοδος {@link DbExistsChecker#databaseExists()}
     * επιστρέφει false όταν το αρχείο δεν υπάρχει.
     */
    @Test
    @DisplayName("Η βάση δεν υπάρχει")
    void testDatabaseExistsFalse() {
        DbExistsChecker.setDbFile(NON_EXISTENT_DB);
        assertFalse(DbExistsChecker.databaseExists(), "Η μέθοδος πρέπει να επιστρέφει false αν το αρχείο δεν υπάρχει");
    }
}
