package database;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import database.DatabaseSetup;

public class DatabaseSetupTest {

    private static final String URL = "jdbc:sqlite:budgetDB.db";

    @Test
    public void setTablesTest() {
        DatabaseSetup.setDatabase();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
                ResultSet rsForeis = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='foreis'");
            assertTrue(rsForeis.next(), "Ο πίνακας 'foreis' πρέπει να έχει δημιουργηθεί.");
            rsForeis.close();

            // Έλεγχος πίνακα 'cashflows'
            ResultSet rsCashflows = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='cashflows'");
            assertTrue(rsCashflows.next(), "Ο πίνακας 'cashflows' πρέπει να έχει δημιουργηθεί.");
            rsCashflows.close();

        } catch (SQLException e) {
            fail("Προέκυψε SQLException κατά τον έλεγχο της δημιουργίας πινάκων: " + e.getMessage());
        }
    }

    /**
     * Ελέγχει τη σωστή διαγραφή των πινάκων 'foreis' και 'cashflows'.
     */
    @Test
    public void testDropTables_DeletesTables() {
        System.out.println("Εκτέλεση Test: testDropTables_DeletesTables");

        // 1. Βεβαιωνόμαστε ότι οι πίνακες υπάρχουν πριν τη διαγραφή
        DatabaseSetup.setDatabase();

        // 2. Εκτέλεση της μεθόδου προς δοκιμή
        DatabaseSetup.dropTables();

        // 3. Έλεγχος: Επαληθεύουμε ότι οι πίνακες έχουν διαγραφεί
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // Έλεγχος πίνακα 'foreis'
            ResultSet rsForeis = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='foreis'");
            assertFalse(rsForeis.next(), "Ο πίνακας 'foreis' πρέπει να έχει διαγραφεί.");
            rsForeis.close();

            // Έλεγχος πίνακα 'cashflows'
            ResultSet rsCashflows = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='cashflows'");
            assertFalse(rsCashflows.next(), "Ο πίνακας 'cashflows' πρέπει να έχει διαγραφεί.");
            rsCashflows.close();

        } catch (SQLException e) {
            fail("Προέκυψε SQLException κατά τον έλεγχο της διαγραφής πινάκων: " + e.getMessage());
        }
    }
    
}