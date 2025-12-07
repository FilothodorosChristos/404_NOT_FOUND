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
        void resetDB() throws SQLException {
            // Drop tables + triggers (όπως στην cleanTables())
            DatabaseSetup.cleanTables();
            // recreate από την αρχή (πίνακες + triggers)
            DatabaseSetup.setDatabase();
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
        assertTrue(tableExists("log"), "Ο πίνακας 'log' πρέπει να έχει δημιουργηθεί.");// Andrew's addition for log table
    }

    @Test
    void testCleanTables_DeletesTables() {
        DatabaseSetup.setDatabase();
        DatabaseSetup.cleanTables();

        assertFalse(tableExists("foreis"), "Ο πίνακας 'foreis' πρέπει να έχει διαγραφεί.");
        assertFalse(tableExists("cashflows"), "Ο πίνακας 'cashflows' πρέπει να έχει διαγραφεί.");
        assertFalse(tableExists("log"), "Ο πίνακας 'log' πρέπει να έχει διαγραφεί.");// Andrew's addition for log table
    }

    @Test
    void testResetTables_RecreatesTables() {
        DatabaseSetup.setDatabase();
        DatabaseSetup.resetTables();

        assertTrue(tableExists("foreis"), "Ο πίνακας 'foreis' πρέπει να υπάρχει μετά το reset.");
        assertTrue(tableExists("cashflows"), "Ο πίνακας 'cashflows' πρέπει να υπάρχει μετά το reset.");
        assertTrue(tableExists("log"), "Ο πίνακας 'log' πρέπει να υπάρχει μετά το reset.");// Andrew's addition for log table
    }

    @Test
    void testForeisTriggers() throws SQLException {
        try (Connection conn = DatabaseSetup.getConnection();
            Statement stmt = conn.createStatement()) {

            // INSERT trigger
            stmt.executeUpdate("INSERT INTO foreis(foreas_id, year_id, type, name, regular_budget, public_inv_budget, total) " +
                    "VALUES(1, 2025, 'typeA', 'NameA', 100.0, 50.0, 150.0)");

            // Έλεγχος log μετά το INSERT
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM log WHERE table_name='foreis' AND operation='INSERT'")) {
                assertTrue(rs.next(), "Το trigger INSERT στον foreis δεν εκτελέστηκε.");
            }

            // UPDATE trigger
            stmt.executeUpdate("UPDATE foreis SET name='NameB' WHERE foreas_id=1 AND year_id=2025");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM log WHERE table_name='foreis' AND operation='UPDATE'")) {
                assertTrue(rs.next(), "Το trigger UPDATE στον foreis δεν εκτελέστηκε.");
            }

            // DELETE trigger
            stmt.executeUpdate("DELETE FROM foreis WHERE foreas_id=1 AND year_id=2025");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM log WHERE table_name='foreis' AND operation='DELETE'")) {
                assertTrue(rs.next(), "Το trigger DELETE στον foreis δεν εκτελέστηκε.");
            }
        }
    }

    @Test
    void testCashflowsTriggers() throws SQLException {
        try (Connection conn = DatabaseSetup.getConnection();
            Statement stmt = conn.createStatement()) {

            // INSERT trigger
            stmt.executeUpdate("INSERT INTO cashflows(year_id, type, name, amount) " +
                    "VALUES(2025, 'Income', 'CashA', 200.0)");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM log WHERE table_name='cashflows' AND operation='INSERT'")) {
                assertTrue(rs.next(), "Το trigger INSERT στον cashflows δεν εκτελέστηκε.");
            }

            // UPDATE trigger
            stmt.executeUpdate("UPDATE cashflows SET amount=300.0 WHERE name='CashA'");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM log WHERE table_name='cashflows' AND operation='UPDATE'")) {
                assertTrue(rs.next(), "Το trigger UPDATE στον cashflows δεν εκτελέστηκε.");
            }

            // DELETE trigger
            stmt.executeUpdate("DELETE FROM cashflows WHERE name='CashA'");

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM log WHERE table_name='cashflows' AND operation='DELETE'")) {
                assertTrue(rs.next(), "Το trigger DELETE στον cashflows δεν εκτελέστηκε.");
            }
        }
    }

}
