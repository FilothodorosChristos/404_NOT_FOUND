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

/**
 * Unit tests για την κλάση {@link LogService}.
 * 
 * Ελέγχει ότι η μέθοδος {@link LogService#getLogsFrom()} επιστρέφει σωστά logs
 * από το καθορισμένο index και μετά.
 * Χρησιμοποιεί ξεχωριστή βάση δεδομένων για δοκιμές.
 */
public class LogServiceTest {

  private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
  private static final String TEST_URL = "jdbc:sqlite:test_logs.db";
  private static final String TEST_FILE = "test_logs.db";

  /** Η service που θα ελέγχεται 
   * 
   */
  private LogService service;

  /**
   * Προετοιμάζει τη test βάση και δημιουργεί το αντικείμενο LogService πριν από κάθε test.
   */
  @BeforeEach
    void setup() {
    DatabaseSetup.setURL(TEST_URL);
    DatabaseSetup.setDatabase();
    DatabaseSetup.resetTables();

    service = new LogService(new LogDao());
    service.setIndex(1); // Για τα tests ορίζουμε index = 1
  }

  /**
   * Καθαρίζει τη test βάση και επαναφέρει τη σύνδεση στην production βάση.
   */
  @AfterAll
    static void tearDown() {
    DatabaseSetup.setURL(ORIGINAL_URL);
    File f = new File(TEST_FILE);
    if (f.exists()) {
      f.delete();
    }
  }

  /**
   * Test 1: Έλεγχος ότι όταν η βάση είναι άδεια, επιστρέφεται κενή λίστα.
   */
  @Test
    void testGetLogsFromEmptyTable() {
    List<Log> logs = service.getLogsFrom();

    assertNotNull(logs);
    assertTrue(logs.isEmpty(), "Η λίστα πρέπει να είναι άδεια όταν δεν υπάρχουν logs");
  }

  /**
   * Test 2: Έλεγχος ότι επιστρέφονται όλα τα logs από το καθορισμένο index και μετά.
   */
  @Test
    void testGetLogsFromWithData() {
    // Εισαγωγή logs στη βάση
    insertLog("CashFlow", "INSERT", 1, null, "{id:1, amount:100}", "2025-01-01 10:00:00");
    insertLog("Foreis", "UPDATE", 2, "{amount:200}", "{amount:250}", "2025-01-01 11:00:00");

    List<Log> logs = service.getLogsFrom();

    assertEquals(2, logs.size(), "Πρέπει να επιστρέφει όλα τα logs από index και μετά");
  }

  /**
   * Test 3: Έλεγχος ότι logs με id κάτω από το index δεν επιστρέφονται.
   */
  @Test
    void testGetLogsFromIgnoresLowerIndex() {
    service.setIndex(2); // Ορίζουμε index = 2 για αυτό το test

    insertLog("Table1", "INSERT", 1, null, "{}", "2025-01-01 10:00:00"); // κάτω από το index
    insertLog("Table2", "UPDATE", 2, "{}", "{x:1}", "2025-01-01 11:00:00"); // ίσο με το index

    List<Log> logs = service.getLogsFrom();

    assertEquals(1, logs.size(), "Πρέπει να επιστρέφει μόνο logs >= index");
    assertEquals("Table2", logs.get(0).getTableName());
  }

  /**
   * Βοηθητική μέθοδος για εισαγωγή test logs στη βάση δεδομένων.
   *
   * @param tableName το όνομα του πίνακα
   * @param operation η λειτουργία (INSERT, UPDATE, DELETE)
   * @param rowId το ID της γραμμής
   * @param oldData τα παλιά δεδομένα
   * @param newData τα νέα δεδομένα
   * @param timestamp ο χρόνος καταγραφής
   */
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
