package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import database.DataImporter;
import database.DatabaseSetup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class για την LogDao.
 * Χρησιμοποιεί test database (test_db_temp.db)
 * ώστε να μην πειράξει την κανονική βάση.
 */
public class LogDaoTest {

  private static final String TEST_DB_URL = "jdbc:sqlite:test_db_temp.db";
  private static final String REAL_DB_URL = "jdbc:sqlite:budgetDB.db";
  private int testIndex = 1;

  private LogDao dao;

  /**
   * Εκτελείται μία φορά πριν από όλα τα tests.
   * Δημιουργεί την test database και τους πίνακες.
   */
  @BeforeAll
  public static void setupDatabase() {
    DatabaseSetup.setURL(TEST_DB_URL);
    DataImporter.setURL(TEST_DB_URL);
    DatabaseSetup.setDatabase();
  }

  /**
   * Εκτελείται πριν από κάθε test.
   * Δημιουργεί νέο αντικείμενο dao και καθαρίζει τη βάση.
   */
  @BeforeEach
  public void init() throws Exception {
    dao = new LogDao();
    DatabaseSetup.resetTables();
  }

  /**
   * Ελέγχει ότι η selectLog επιστρέφει εγγραφή που εισάγουμε χειροκίνητα.
   */
  @Test
  public void testSelectLogReturnsInsertedRow() throws Exception {
    try (Connection conn = DatabaseSetup.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO log (table_name, operation, row_id, old_data, new_data, timestamp) " 
             + "VALUES (?, ?, ?, ?, ?, ?)"
         )) {
      // stmt.setInt(1, 1);
      stmt.setString(1, "cashflow");
      stmt.setString(2, "UPDATE");
      stmt.setInt(3, 101);
      stmt.setString(4, "{amount:100}");
      stmt.setString(5, "{amount:120}");
      stmt.setString(6, "2025-12-06 16:45:00");
      stmt.executeUpdate();
    }

    List<Log> logs = dao.selectLog(testIndex);

    assertEquals(1, logs.size());
    Log log = logs.get(0);
    // assertEquals(1, log.getId());
    assertEquals("cashflow", log.getTableName());
    assertEquals("UPDATE", log.getOperation());
    assertEquals(101, log.getRowId());
    assertEquals("{amount:100}", log.getOldData());
    assertEquals("{amount:120}", log.getNewData());
    assertEquals("2025-12-06 16:45:00", log.getTimestamp());
  }

  /**
   * Ελέγχει ότι η selectLog επιστρέφει empty list όταν δεν υπάρχουν εγγραφές.
   */
  @Test
  public void testSelectLogEmptyTable() {
    List<Log> logs = dao.selectLog(testIndex);
    assertTrue(logs.isEmpty());
  }

  @Test
public void testSelectLogRespectsIndex() throws Exception {
    try (Connection conn = DatabaseSetup.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
             "INSERT INTO log (table_name, operation, row_id, old_data, new_data, timestamp) " 
            + "VALUES (?, ?, ?, ?, ?, ?)"
         )) {

      // id = 1
      stmt.setString(1, "table1");
      stmt.setString(2, "INSERT");
      stmt.setInt(3, 1);
      stmt.setString(4, null);
      stmt.setString(5, "{}");
      stmt.setString(6, "2025-01-01 10:00:00");
      stmt.executeUpdate();

      // id = 2
      stmt.setString(1, "table2");
      stmt.setString(2, "UPDATE");
      stmt.setInt(3, 2);
      stmt.setString(4, "{}");
      stmt.setString(5, "{x:1}");
      stmt.setString(6, "2025-01-01 11:00:00");
      stmt.executeUpdate();
    }

    List<Log> logs = dao.selectLog(2);

    assertEquals(1, logs.size());
    assertEquals("table2", logs.get(0).getTableName());
  }

  @Test
public void testSelectLogIndexTooHighReturnsEmpty() {
    List<Log> logs = dao.selectLog(999);
    assertTrue(logs.isEmpty());
  }

  /**
   * Εκτελείται μία φορά στο τέλος όλων των tests.
   * Επαναφέρει το URL στην κύρια βάση.
   */
  @AfterAll
  public static void restoreDatabaseURL() {
    DatabaseSetup.setURL(REAL_DB_URL);
  }
}
