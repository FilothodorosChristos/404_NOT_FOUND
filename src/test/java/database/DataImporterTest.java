package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link DataImporter} class.
 *
 * <p>These tests use a temporary SQLite database in order to verify that
 * CSV data are correctly imported and invalid rows are skipped.</p>
 */
public class DataImporterTest {

  /**
   * JDBC URL of the temporary test database.
   */
  private static final String TEST_URL = "jdbc:sqlite:test_db_temp.db";

  /**
   * JDBC URL of the real application database.
   */
  private static final String REAL_URL = "jdbc:sqlite:budgetDB.db";

  /**
   * Configures the {@link DataImporter} and {@link DatabaseSetup}
   * classes to use the temporary test database before all tests.
   *
   * @throws Exception if the JDBC driver cannot be loaded
   */
  @BeforeAll
  static void setupTestUrl() throws Exception {
    Class.forName("org.sqlite.JDBC");
    DataImporter.setURL(TEST_URL);
    DatabaseSetup.setURL(TEST_URL);
  }

  /**
   * Restores the original database URL after all tests have completed.
   *
   * @throws Exception if restoring the database URL fails
   */
  @AfterAll
  static void restoreRealUrl() throws Exception {
    DataImporter.setURL(REAL_URL);
    DatabaseSetup.setURL(REAL_URL);
  }

  /**
   * Recreates the database schema before each test execution.
   */
  @BeforeEach
  void setupDatabaseSchema() {
    try (Connection conn = DriverManager.getConnection(TEST_URL);
         Statement stmt = conn.createStatement()) {

      stmt.execute("DROP TABLE IF EXISTS foreis");
      stmt.execute("DROP TABLE IF EXISTS cashflows");
      stmt.execute("DROP TABLE IF EXISTS log");

      String createForeisTable =
          "CREATE TABLE IF NOT EXISTS foreis ("
              + "foreas_id INTEGER, year_id INTEGER, type TEXT, name TEXT, "
              + "regular_budget REAL, public_inv_budget REAL, total REAL)";

      String createCashflowsTable =
          "CREATE TABLE IF NOT EXISTS cashflows ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT, year_id INTEGER, "
              + "type TEXT, name TEXT, amount REAL)";

      String createLogTable =
          "CREATE TABLE IF NOT EXISTS log ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT, table_name TEXT, "
              + "operation TEXT, row_id INTEGER, details TEXT, "
              + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

      stmt.execute(createForeisTable);
      stmt.execute(createCashflowsTable);
      stmt.execute(createLogTable);

    } catch (SQLException e) {
      e.printStackTrace();
      fail("Αποτυχία δημιουργίας σχήματος βάσης δεδομένων: " + e.getMessage());
    }
  }

  /**
   * Counts the number of rows in the specified database table.
   *
   * @param tableName the name of the table
   * @return the number of rows contained in the table
   * @throws SQLException if a database access error occurs
   */
  private int countRows(String tableName) throws SQLException {
    try (Connection conn = DriverManager.getConnection(TEST_URL);
         Statement stmt = conn.createStatement();
         ResultSet rs =
             stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {

      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException e) {
      fail("Σφάλμα κατά την μέτρηση γραμμών στον πίνακα "
          + tableName + ": " + e.getMessage());
    }
    return 0;
  }

  /**
   * Verifies that valid rows are inserted into the {@code foreis} table
   * and invalid rows are skipped.
   *
   * @throws Exception if CSV import fails
   */
  @Test
  void insertForeisFromCsvShouldInsertValidRowsAndSkipInvalid()
      throws Exception {

    DataImporter.insertForeisFromCsv("B23ForeisTEST.csv");

    int count = countRows("foreis");
    assertEquals(2, count,
        "Πρέπει να έχουν εισαχθεί 2 έγκυρες γραμμές στον πίνακα foreis.");

    try (Connection conn = DriverManager.getConnection(TEST_URL);
         PreparedStatement pstmt =
             conn.prepareStatement(
                 "SELECT name, regular_budget FROM foreis "
                     + "WHERE foreas_id = 1001");
         ResultSet rs = pstmt.executeQuery()) {

      assertTrue(rs.next(), "Δεν βρέθηκε ο φορέας 1001.");
      assertEquals("Υπ. Οικονομικών", rs.getString("name"));
      assertEquals(100000.50, rs.getDouble("regular_budget"), 0.001);
    }
  }

  /**
   * Verifies that valid cashflow rows are inserted and invalid rows are skipped.
   *
   * @throws Exception if CSV import fails
   */
  @Test
  void insertCashflowsFromCsvShouldInsertValidRowsAndSkipInvalid()
      throws Exception {

    DataImporter.insertCashflowsFromCsv("B23EsodaTEST.csv", "Έσοδο");

    int count = countRows("cashflows");
    assertEquals(2, count,
        "Πρέπει να έχουν εισαχθεί 2 έγκυρες γραμμές στον πίνακα cashflows.");

    try (Connection conn = DriverManager.getConnection(TEST_URL);
         PreparedStatement pstmt =
             conn.prepareStatement(
                 "SELECT amount, type FROM cashflows WHERE name = 'ΦΠΑ'");
         ResultSet rs = pstmt.executeQuery()) {

      assertTrue(rs.next(), "Δεν βρέθηκε η ροή 'ΦΠΑ'.");
      assertEquals(250000000.75, rs.getDouble("amount"), 0.001);
      assertEquals("Έσοδο", rs.getString("type"));
    }
  }

  /**
   * Verifies that an exception is thrown when the CSV file does not exist.
   */
  @Test
  void insertForeisFromCsvShouldThrowFileNotFoundException() {
    Exception exception =
        assertThrows(Exception.class, () ->
            DataImporter.insertForeisFromCsv("B27ForeisTEST.csv"));

    assertTrue(exception.getMessage().contains("Δεν βρέθηκε το αρχείο"));
  }
}
