package database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.Database.DatabaseSetup;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseSetupTest {

    private static final String TEST_URL = "jdbc:sqlite::memory:";

    @BeforeEach
    void setup() {
        DatabaseSetup.setUrl(TEST_URL);
    }

    private boolean tableExists(String tableName) {
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'")) {
            return rs.next();
        } catch (SQLException e) {
            fail("Error checking table existence: " + e.getMessage());
            return false;
        }
    }

    @Test
    void testSetDatabase_CreatesTables() {
        DatabaseSetup.setDatabase();
        assertTrue(tableExists("foreis"));
        assertTrue(tableExists("cashflows"));
    }

    @Test
    void testCleanTables_DeletesTables() {
        DatabaseSetup.setDatabase();
        DatabaseSetup.cleanTables();
        assertFalse(tableExists("foreis"));
        assertFalse(tableExists("cashflows"));
    }

    @Test
    void testResetTables_RecreatesTables() {
        DatabaseSetup.setDatabase();
        DatabaseSetup.resetTables();
        assertTrue(tableExists("foreis"));
        assertTrue(tableExists("cashflows"));
    }
}
