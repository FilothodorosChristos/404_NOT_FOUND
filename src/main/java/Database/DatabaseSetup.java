package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String URL = "jdbc:sqlite:budgetDB.db";

    public static void setDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Πίνακας foreis
            String createForeisTable = """
                CREATE TABLE IF NOT EXISTS foreis (
                    foreas_id INTEGER PRIMARY KEY,
                    year_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    name TEXT NOT NULL,
                    regular_budget REAL,
                    public_inv_budget REAL,
                    total REAL
                );
            """;

            // Πίνακας cashflows
            String createCashflowsTable = """
                CREATE TABLE IF NOT EXISTS cashflows (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    year_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    name TEXT NOT NULL,
                    amount REAL,
                    FOREIGN KEY (year_id) REFERENCES foreis(year_id)
                );
            """;

            stmt.execute(createForeisTable);
            stmt.execute(createCashflowsTable);

            System.out.println("Οι πίνακες δημιουργήθηκαν με επιτυχία.");

        } catch (SQLException e) {
            System.out.println("Σφάλμα κατά τη δημιουργία των πινάκων: " + e.getMessage());
        }
    }

    public static void dropTables() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = OFF;");

            stmt.executeUpdate("DROP TABLE IF EXISTS cashflows;");
            stmt.executeUpdate("DROP TABLE IF EXISTS foreis;");

            stmt.execute("PRAGMA foreign_keys = ON;");

            System.out.println("Επιτυχής διαγραφή των πινάκων.");

            // Επαναδημιουργία των πινάκων
            setDatabase();

            System.out.println("Οι πίνακες επαναδημιουργήθηκαν με επιτυχία.");

        } catch (SQLException e) {
            System.out.println("Σφάλμα κατά τη διαγραφή των πινάκων: " + e.getMessage());
        }
    }
}

