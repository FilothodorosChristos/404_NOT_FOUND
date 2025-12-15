package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class with methods to set up and delete database tables.
 */
public class DatabaseSetup {

  private static String URL = "jdbc:sqlite:budgetDB.db";
  /** 
   * @param url
   */
  //setter για τα τεστ
  public static void setURL(String url) {
    URL = url;
  }
  /** 
   * Δημιουργεί τους πίνακες της βάσης δεδομένων μαζί με τα triggers για το log.
   */
  public static void setDatabase() {
    try (Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement()) {

      stmt.execute("PRAGMA foreign_keys = ON;");

      // Πίνακας foreis
      String createForeisTable = """
        CREATE TABLE IF NOT EXISTS foreis (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            foreas_id INTEGER,
            year_id INTEGER NOT NULL,
            type TEXT NOT NULL,
            name TEXT NOT NULL,
            regular_budget REAL,
            public_inv_budget REAL,
            total REAL,
            UNIQUE(foreas_id, year_id)
        );
        """;

      // Πίνακας cashflows
      String createCashflowsTable = """
              CREATE TABLE IF NOT EXISTS cashflows (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  year_id INTEGER NOT NULL,
                  type TEXT NOT NULL,
                  name TEXT NOT NULL,
                  amount REAL
              );
          """;

      stmt.execute(createForeisTable);
      stmt.execute(createCashflowsTable);

      String createLogTable = """
              CREATE TABLE IF NOT EXISTS log (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  table_name TEXT NOT NULL,
                  operation TEXT NOT NULL,
                  row_id INTEGER,
                  old_data TEXT,
                  new_data TEXT,
                  timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
              );
          """;

      // Trigger για INSERT στον πίνακα foreis
      String triggerInsertForeis = """
        CREATE TRIGGER IF NOT EXISTS log_insert_foreis
        AFTER INSERT ON foreis
        BEGIN
            INSERT INTO log(table_name, operation, row_id, new_data)
            VALUES('foreis', 'INSERT', NEW.id,
                  'foreas_id=' || NEW.foreas_id || ', year_id=' || NEW.year_id
                  || ', type=' || NEW.type || ', name=' || NEW.name
                  || ', regular_budget=' || NEW.regular_budget
                  || ', public_inv_budget=' || NEW.public_inv_budget
                  || ', total=' || NEW.total);
          END;
      """;

        // Trigger για UPDATE στον πίνακα foreis
      String triggerUpdateForeis = """
        CREATE TRIGGER IF NOT EXISTS log_update_foreis
        AFTER UPDATE ON foreis
        BEGIN
            INSERT INTO log(table_name, operation, row_id, old_data, new_data)
            VALUES('foreis', 'UPDATE', OLD.id,
                   'foreas_id=' || OLD.foreas_id || ', year_id=' || OLD.year_id
                   || ', type=' || OLD.type || ', name=' || OLD.name
                   || ', regular_budget=' || OLD.regular_budget
                   || ', public_inv_budget=' || OLD.public_inv_budget
                   || ', total=' || OLD.total,
                   'foreas_id=' || NEW.foreas_id || ', year_id=' || NEW.year_id
                   || ', type=' || NEW.type || ', name=' || NEW.name
                   || ', regular_budget=' || NEW.regular_budget
                   || ', public_inv_budget=' || NEW.public_inv_budget
                   || ', total=' || NEW.total);
        END;
      """;

      // Trigger για DELETE στον πίνακα foreis
      String triggerDeleteForeis = """
        CREATE TRIGGER IF NOT EXISTS log_delete_foreis
        AFTER DELETE ON foreis
        BEGIN
            INSERT INTO log(table_name, operation, row_id, old_data)
            VALUES('foreis', 'DELETE', OLD.id,
                   'foreas_id=' || OLD.foreas_id || ', year_id=' || OLD.year_id
                   || ', type=' || OLD.type || ', name=' || OLD.name
                   || ', regular_budget=' || OLD.regular_budget
                   || ', public_inv_budget=' || OLD.public_inv_budget
                   || ', total=' || OLD.total);
        END;
      """;

      stmt.execute(createLogTable);
      stmt.execute(triggerInsertForeis);
      stmt.execute(triggerUpdateForeis);
      stmt.execute(triggerDeleteForeis);

      // Trigger για INSERT στον πίνακα cashflows
      String triggerInsertCashflows = """
          CREATE TRIGGER IF NOT EXISTS log_insert_cashflows
          AFTER INSERT ON cashflows
          BEGIN
              INSERT INTO log(table_name, operation, row_id, new_data)
              VALUES('cashflows', 'INSERT', NEW.id,
                    'year_id=' || NEW.year_id || ', type=' || NEW.type
                    || ', name=' || NEW.name || ', amount=' || NEW.amount);
          END;
      """;
      

      // Trigger για UPDATE στον πίνακα cashflows
      String triggerUpdateCashflows = """
          CREATE TRIGGER IF NOT EXISTS log_update_cashflows
          AFTER UPDATE ON cashflows
          BEGIN
              INSERT INTO log(table_name, operation, row_id, old_data, new_data)
              VALUES('cashflows', 'UPDATE', OLD.id,
                    'year_id=' || OLD.year_id || ', type=' || OLD.type
                    || ', name=' || OLD.name || ', amount=' || OLD.amount,
                    'year_id=' || NEW.year_id || ', type=' || NEW.type
                    || ', name=' || NEW.name || ', amount=' || NEW.amount);
          END;
      """;
      

      // Trigger για DELETE στον πίνακα cashflows
      String triggerDeleteCashflows = """
          CREATE TRIGGER IF NOT EXISTS log_delete_cashflows
          AFTER DELETE ON cashflows
          BEGIN
              INSERT INTO log(table_name, operation, row_id, old_data)
              VALUES('cashflows', 'DELETE', OLD.id,
                    'year_id=' || OLD.year_id || ', type=' || OLD.type
                    || ', name=' || OLD.name || ', amount=' || OLD.amount);
          END;
      """;

      stmt.execute(triggerInsertCashflows);
      stmt.execute(triggerUpdateCashflows);
      stmt.execute(triggerDeleteCashflows);


      System.out.println("Οι πίνακες δημιουργήθηκαν με επιτυχία.");

    } catch (SQLException e) {
      //e.printStackTrace();
      System.err.println("Σφάλμα κατά τη δημιουργία των πινάκων: " + e.getMessage());
      //throw new RuntimeException("Σφάλμα κατά τη δημιουργία των πινάκων", e);
    }
}
  /** 
   * Επαναφέρει τη βάση δεδομένων διαγράφοντας και δημιουργώντας ξανά τους πίνακες.
   */
  public static void resetTables() {
    cleanTables();
    setDatabase();
}
  /** 
   * Διαγράφει τους πίνακες foreis, cashflows και log από τη βάση δεδομένων.
   * Διαγράφει επίσης τα αντίστοιχα triggers.
   * Απενεργοποιεί προσωρινά τους foreign keys για την αποφυγή σφαλμάτων.
   */
  public static void cleanTables() {
    try (Connection conn = DriverManager.getConnection(URL);
        Statement stmt = conn.createStatement()) {

          stmt.execute("PRAGMA foreign_keys = OFF;");

          stmt.executeUpdate("DROP TRIGGER IF EXISTS log_insert_foreis;");
          stmt.executeUpdate("DROP TRIGGER IF EXISTS log_update_foreis;");
          stmt.executeUpdate("DROP TRIGGER IF EXISTS log_delete_foreis;");

          stmt.executeUpdate("DROP TRIGGER IF EXISTS log_insert_cashflows;");
          stmt.executeUpdate("DROP TRIGGER IF EXISTS log_update_cashflows;");
          stmt.executeUpdate("DROP TRIGGER IF EXISTS log_delete_cashflows;");

          stmt.executeUpdate("DROP TABLE IF EXISTS cashflows;");
          stmt.executeUpdate("DROP TABLE IF EXISTS foreis;");
          stmt.executeUpdate("DROP TABLE IF EXISTS log;");


      stmt.execute("PRAGMA foreign_keys = ON;");

      System.out.println("Επιτυχής διαγραφή των πινάκων.");

    } catch (SQLException e) {
      System.err.println("Σφάλμα κατά τη διαγραφή των πινάκων: " + e.getMessage());
    }
  }
  /** 
   * @return Connection
   * @throws SQLException
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
  }

}
