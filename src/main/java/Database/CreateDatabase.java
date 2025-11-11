package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class CreateDatabase {
    public static void createDB() {
        String url = "jdbc:sqlite:budgetDB.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Η βάση δημιουργήθηκε με επιτυχία.");
            }
        } catch (SQLException e) {
            System.out.println("Σφάλμα: " + e.getMessage());
        }
    }


}
