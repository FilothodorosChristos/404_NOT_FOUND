package database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;

import database.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseSetupTest {


    private static Connection conn;
    private static final String TEST_DB_FILE = "test_db_temp.db";

    private static final String TEST_URL = "jdbc:sqlite:" + TEST_DB_FILE;

    private static final String REAL_URL = "jdbc:sqlite:budgetDB.db";


    @BeforeAll
    static void setup() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection(TEST_URL);
        DatabaseSetup.setURL(TEST_URL);
        DatabaseSetup.setDatabase(); 
    }// Δημιουργεί πίνακες

    @BeforeEach
    void cleanTables() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM foreis");
            stmt.execute("DELETE FROM cashflows");
        }
    }

    @AfterAll
    static void teardownClass() throws Exception {
        if (conn != null && !conn.isClosed()) {
        conn.close();
        }
        
        //deletes test db file
        java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(TEST_DB_FILE));

        //restores url to real db
        DatabaseSetup.setURL(REAL_URL);
    }

    // Helper method για έλεγχο αν υπάρχει πίνακας
    private boolean tableExists(String tableName) {
        try (Statement stmt = conn.createStatement();
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
