package database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseSetupTest {

    // In-memory DB για πλήρη απομόνωση
    private static final String TEST_URL = "jdbc:sqlite::memory:";
    private static final String REAL_URL = "jdbc:sqlite:budgetDB.db";

    @BeforeEach
    void setTestUrl() {
        DatabaseSetup.setURL(TEST_URL);
    }

    @AfterAll
    static void restoreRealUrl() {
        DatabaseSetup.setURL(REAL_URL);
    }

    // Helper method για έλεγχο αν υπάρχει πίνακας
    private boolean tableExists(String tableName) {
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'")) {
            return rs.next();
        } catch (SQLException e) {
            fail("Error checking table existence: " + e.getMessage());
        }
        return false;
    }

    @Test
    void testSetDatabase_CreatesTables() {
        DatabaseSetup.setDatabase();

        assertTrue(tableExists("foreis"), "Ο πίνακας 'foreis' πρέπει να έχει δημιουργηθεί.");
        assertTrue(tableExists("cashflows"), "Ο πίνακας 'cashflows' πρέπει να έχει δημιουργηθεί.");
    }

    @Test
    void testCleanTables_DeletesTables() {
        DatabaseSetup.setDatabase();
        DatabaseSetup.cleanTables();

        assertFalse(tableExists("foreis"), "Ο πίνακας 'foreis' πρέπει να έχει διαγραφεί.");
        assertFalse(tableExists("cashflows"), "Ο πίνακας 'cashflows' πρέπει να έχει διαγραφεί.");
    }

    @Test
    void testResetTables_RecreatesTables() {
        DatabaseSetup.setDatabase();
        DatabaseSetup.resetTables();

        assertTrue(tableExists("foreis"), "Ο πίνακας 'foreis' πρέπει να υπάρχει μετά το reset.");
        assertTrue(tableExists("cashflows"), "Ο πίνακας 'cashflows' πρέπει να υπάρχει μετά το reset.");
    }
}
