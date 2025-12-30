package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.Log;
import dao.LogDao;
import database.DatabaseSetup;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.*;

public class LogServiceTest {

    private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
    private static final String TEST_URL = "jdbc:sqlite:test_logs.db";
    private static final String TEST_FILE = "test_logs.db";

    private LogService service;

    @BeforeEach
    void setup() {
        DatabaseSetup.setURL(TEST_URL);
        DatabaseSetup.setDatabase();
        DatabaseSetup.resetTables();

        service = new LogService(new LogDao());
    }

    @AfterAll
    static void tearDown() {
        DatabaseSetup.setURL(ORIGINAL_URL);
        File f = new File(TEST_FILE);
        if (f.exists()) {
            f.delete();
        }
    }

    // TEST 1: Empty table
    @Test
    void testGetLogsFromEmptyTable() {
        List<Log> logs = service.getLogsFrom();

        assertNotNull(logs);
        assertTrue(logs.isEmpty(), "Η λίστα πρέπει να είναι άδεια όταν δεν υπάρχουν logs");
    }

    // TEST 2: Returns all logs from default index
    @Test
    void testGetLogsFromWithData() {
        // Εισαγωγή logs στη βάση
        insertLog("CashFlow", "INSERT", 336, null, "{id:1, amount:100}", "2025-01-01 10:00:00");
        insertLog("Foreis", "UPDATE", 337, "{amount:200}", "{amount:250}", "2025-01-01 11:00:00");

        // Η service κρατάει το index 335
        List<Log> logs = service.getLogsFrom();

        assertEquals(2, logs.size(), "Πρέπει να επιστρέφει όλα τα logs από το 335 και μετά");
    }

    // TEST 3: Logs με id μικρότερο από index δεν επιστρέφονται
    @Test
    void testGetLogsFromIgnoresLowerIndex() {
        insertLog("Table1", "INSERT", 100, null, "{}", "2025-01-01 10:00:00");
        insertLog("Table2", "UPDATE", 336, "{}", "{x:1}", "2025-01-01 11:00:00");

        List<Log> logs = service.getLogsFrom();

        assertEquals(1, logs.size(), "Πρέπει να επιστρέφει μόνο logs >= 335");
        assertEquals("Table2", logs.get(0).getTableName());
    }

    // Helper method
    private void insertLog(String tableName,
                           String operation,
                           int rowId,
                           String oldData,
                           String newData,
                           String timestamp) {

        final String SQL =
                "INSERT INTO log (table_name, operation, row_id, old_data, new_data, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, tableName);
            stmt.setString(2, operation);
            stmt.setInt(3, rowId);
            stmt.setString(4, oldData);
            stmt.setString(5, newData);
            stmt.setString(6, timestamp);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting log into test DB", e);
        }
    }
}
