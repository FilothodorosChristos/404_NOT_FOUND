package database;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DatabaseSetup}.
 * Uses a temporary SQLite database in order to verify:
 * <ul>
 *   <li>Table creation</li>
 *   <li>Table deletion</li>
 *   <li>Database reset</li>
 *   <li>Trigger execution</li>
 * </ul>
 */
public class DatabaseSetupTest {

  private static Connection conn;

  private static final String TEST_DB_FILE = "test_db_temp.db";
  private static final String TEST_URL = "jdbc:sqlite:" + TEST_DB_FILE;
  private static final String REAL_URL = "jdbc:sqlite:budgetDB.db";

  /**
   * Executes once before all tests.
   * Creates the test database and initializes all tables and triggers.
   *
   * @throws Exception if database setup fails
   */
  @BeforeAll
  static void setup() throws Exception {
    Class.forName("org.sqlite.JDBC");
    conn = DriverManager.getConnection(TEST_URL);
    DatabaseSetup.setURL(TEST_URL);
    DatabaseSetup.setDatabase();
  }

  /**
   * Executes before each test.
   * Drops all tables and triggers and recreates them from scratch.
   *
   * @throws SQLException if database reset fails
   */
  @BeforeEach
  void resetDb() throws SQLException {
    DatabaseSetup.cleanTables();
    DatabaseSetup.setDatabase();
  }

  /**
   * Executes once after all tests.
   * Closes the database connection, deletes the temporary database file
   * and restores the real database URL.
   *
   * @throws Exception if cleanup fails
   */
  @AfterAll
  static void teardownClass() throws Exception {
    if (conn != null && !conn.isClosed()) {
      conn.close();
    }

    java.nio.file.Files.deleteIfExists(
        java.nio.file.Paths.get(TEST_DB_FILE));

    DatabaseSetup.setURL(REAL_URL);
  }

  /**
   * Checks whether a table exists in the database.
   *
   * @param tableName the name of the table
   * @return {@code true} if the table exists, {@code false} otherwise
   */
  private boolean tableExists(String tableName) {
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT name FROM sqlite_master "
                 + "WHERE type='table' AND name='" + tableName + "'")) {

      return rs.next();

    } catch (SQLException e) {
      fail("Error checking table existence: " + e.getMessage());
      return false;
    }
  }

  /**
   * Verifies that {@link DatabaseSetup#setDatabase()} creates all tables.
   */
  @Test
  void testSetDatabaseCreatesTables() {
    DatabaseSetup.setDatabase();

    assertTrue(tableExists("foreis"),
        "Ο πίνακας 'foreis' πρέπει να έχει δημιουργηθεί.");
    assertTrue(tableExists("cashflows"),
        "Ο πίνακας 'cashflows' πρέπει να έχει δημιουργηθεί.");
    assertTrue(tableExists("log"),
        "Ο πίνακας 'log' πρέπει να έχει δημιουργηθεί.");
  }

  /**
   * Verifies that {@link DatabaseSetup#cleanTables()} deletes all tables.
   */
  @Test
  void testCleanTablesDeletesTables() {
    DatabaseSetup.setDatabase();
    DatabaseSetup.cleanTables();

    assertFalse(tableExists("foreis"),
        "Ο πίνακας 'foreis' πρέπει να έχει διαγραφεί.");
    assertFalse(tableExists("cashflows"),
        "Ο πίνακας 'cashflows' πρέπει να έχει διαγραφεί.");
    assertFalse(tableExists("log"),
        "Ο πίνακας 'log' πρέπει να έχει διαγραφεί.");
  }

  /**
   * Verifies that {@link DatabaseSetup#resetTables()} recreates all tables.
   */
  @Test
  void testResetTablesRecreatesTables() {
    DatabaseSetup.setDatabase();
    DatabaseSetup.resetTables();

    assertTrue(tableExists("foreis"),
        "Ο πίνακας 'foreis' πρέπει να υπάρχει μετά το reset.");
    assertTrue(tableExists("cashflows"),
        "Ο πίνακας 'cashflows' πρέπει να υπάρχει μετά το reset.");
    assertTrue(tableExists("log"),
        "Ο πίνακας 'log' πρέπει να υπάρχει μετά το reset.");
  }

  /**
   * Verifies that INSERT, UPDATE and DELETE triggers
   * on table {@code foreis} execute correctly.
   *
   * @throws SQLException if database access fails
   */
  @Test
  void testForeisTriggers() throws SQLException {
    try (Connection conn = DatabaseSetup.getConnection();
         Statement stmt = conn.createStatement()) {

      stmt.executeUpdate(
          "INSERT INTO foreis(foreas_id, year_id, type, name, "
              + "regular_budget, public_inv_budget, total) "
              + "VALUES(1, 2025, 'typeA', 'NameA', 100.0, 50.0, 150.0)");

      try (ResultSet rs = stmt.executeQuery(
          "SELECT * FROM log "
              + "WHERE table_name='foreis' AND operation='INSERT'")) {
        assertTrue(rs.next(),
            "Το trigger INSERT στον foreis δεν εκτελέστηκε.");
      }

      stmt.executeUpdate(
          "UPDATE foreis SET name='NameB' "
              + "WHERE foreas_id=1 AND year_id=2025");

      try (ResultSet rs = stmt.executeQuery(
          "SELECT * FROM log "
              + "WHERE table_name='foreis' AND operation='UPDATE'")) {
        assertTrue(rs.next(),
            "Το trigger UPDATE στον foreis δεν εκτελέστηκε.");
      }

      stmt.executeUpdate(
          "DELETE FROM foreis "
              + "WHERE foreas_id=1 AND year_id=2025");

      try (ResultSet rs = stmt.executeQuery(
          "SELECT * FROM log "
              + "WHERE table_name='foreis' AND operation='DELETE'")) {
        assertTrue(rs.next(),
            "Το trigger DELETE στον foreis δεν εκτελέστηκε.");
      }
    }
  }

  /**
   * Verifies that INSERT, UPDATE and DELETE triggers
   * on table {@code cashflows} execute correctly.
   *
   * @throws SQLException if database access fails
   */
  @Test
  void testCashflowsTriggers() throws SQLException {
    try (Connection conn = DatabaseSetup.getConnection();
         Statement stmt = conn.createStatement()) {

      stmt.executeUpdate(
          "INSERT INTO cashflows(year_id, type, name, amount) "
              + "VALUES(2025, 'Income', 'CashA', 200.0)");

      try (ResultSet rs = stmt.executeQuery(
          "SELECT * FROM log "
              + "WHERE table_name='cashflows' AND operation='INSERT'")) {
        assertTrue(rs.next(),
            "Το trigger INSERT στον cashflows δεν εκτελέστηκε.");
      }

      stmt.executeUpdate(
          "UPDATE cashflows SET amount=300.0 WHERE name='CashA'");

      try (ResultSet rs = stmt.executeQuery(
          "SELECT * FROM log "
              + "WHERE table_name='cashflows' AND operation='UPDATE'")) {
        assertTrue(rs.next(),
            "Το trigger UPDATE στον cashflows δεν εκτελέστηκε.");
      }

      stmt.executeUpdate(
          "DELETE FROM cashflows WHERE name='CashA'");

      try (ResultSet rs = stmt.executeQuery(
          "SELECT * FROM log "
              + "WHERE table_name='cashflows' AND operation='DELETE'")) {
        assertTrue(rs.next(),
            "Το trigger DELETE στον cashflows δεν εκτελέστηκε.");
      }
    }
  }
}
