package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.Log;
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

    // Δημιουργεί πίνακες
    DatabaseSetup.setDatabase();

    // Καθαρίζει πίνακες
    DatabaseSetup.resetTables();

    service = new LogService();
  }

  @AfterAll
  static void tearDown() {
    DatabaseSetup.setURL(ORIGINAL_URL);
    File f = new File(TEST_FILE);
    if (f.exists()) {
      f.delete();
    }
  }

  // -------------------------------------------------------------
  // TEST 1: Empty logs
  // -------------------------------------------------------------
  @Test
  void testGetAllLogsEmpty() {
    List<Log> logs = service.getAllLogs();

    assertNotNull(logs);
    assertEquals(0, logs.size());
  }

  // -------------------------------------------------------------
  // TEST 2: Logs exist in DB
  // -------------------------------------------------------------
  @Test
  void testGetAllLogsWithData() {

    insertLogIntoDB(
            "CashFlow",
            "INSERT",
            1,
            null,
            "{id:1, amount:100.0}",
            "2025-01-01 10:00:00"
    );

    insertLogIntoDB(
            "Foreis",
            "UPDATE",
            2,
            "{amount:200.0}",
            "{amount:250.0}",
            "2025-01-01 11:00:00"
    );

    List<Log> logs = service.getAllLogs();

    assertNotNull(logs);
    assertEquals(2, logs.size());

    Log log1 = logs.get(0);
    assertEquals("CashFlow", log1.getTableName());
    assertEquals("INSERT", log1.getOperation());
    assertEquals(1, log1.getRowId());
    assertNull(log1.getOldData());
    assertEquals("{id:1, amount:100.0}", log1.getNewData());

    Log log2 = logs.get(1);
    assertEquals("Foreis", log2.getTableName());
    assertEquals("UPDATE", log2.getOperation());
    assertEquals(2, log2.getRowId());
    assertEquals("{amount:200.0}", log2.getOldData());
    assertEquals("{amount:250.0}", log2.getNewData());
  }

  // -------------------------------------------------------------
  // Helper method to insert logs directly into DB
  // -------------------------------------------------------------
  private void insertLogIntoDB(String tableName, String operation, Integer rowId,
                               String oldData, String newData, String timestamp) {

    final String SQL = "INSERT INTO log (table_name, operation, row_id, old_data, new_data, timestamp) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

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
      throw new RuntimeException("Error inserting log into test DB: " + e.getMessage(), e);
    }
  }

}


