package database;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import database.DatabaseSetup;
import database.DataImporter;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataImporterTest {

    // url για προσωρινή in-memory βάση δεδομένων για testing
    private static final String TEST_URL = "jdbc:sqlite::memory:";
    private static final String REAL_URL = "jdbc:sqlite:budgetDB.db"; 

    //Αντικαθιστούμε το URL της DataImporter κλάσης πριν από όλα τα tests
    @BeforeAll
    static void setupTestUrl() {
        DatabaseSetup.setURL(TEST_URL);
        DataImporter.setURL(TEST_URL);
    }
    
    //Εισάγουμε το url της πραγματικής βάσης μετά τα tests
    @AfterAll
    static void restoreRealUrl() {
        DatabaseSetup.setURL(REAL_URL);
        DataImporter.setURL(REAL_URL);
    }

    // Δημιουργεί το σχήμα (πίνακες) στην in-memory βάση πριν από κάθε test.
    @BeforeEach
    void setupDatabaseSchema() {
        String createForeisTable = "CREATE TABLE IF NOT EXISTS foreis (" +
                "foreas_id INTEGER, year_id INTEGER, type TEXT, name TEXT, " +
                "regular_budget REAL, public_inv_budget REAL, total REAL)";

        String createCashflowsTable = "CREATE TABLE IF NOT EXISTS cashflows (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, year_id INTEGER, " +
                "type TEXT, name TEXT, amount REAL)";
        
        try (Connection conn = DriverManager.getConnection(TEST_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS foreis"); // Καθαρισμός για κάθε test
            stmt.execute("DROP TABLE IF EXISTS cashflows"); // Καθαρισμός για κάθε test
            stmt.execute(createForeisTable);
            stmt.execute(createCashflowsTable);
        } catch (SQLException e) {
            fail("Αποτυχία δημιουργίας σχήματος βάσης δεδομένων: " + e.getMessage());
        }
        
        // Mock DatabaseSetup.cleanTables() - Ο DataImporter καλεί αυτή τη μέθοδο,
        // αλλά για τον test σκοπό, την αντικαθιστούμε με το DROP TABLE παραπάνω.
    }
    // Βοηθητική μέθοδος για μέτρηση γραμμών σε πίνακα
    private int countRows(String tableName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(TEST_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            fail("Σφάλμα κατά την μέτρηση γραμμών στον πίνακα " + tableName + ": " + e.getMessage());
        }
        return 0;
    }
    // Test Case: Εισαγωγή Φορέων (Foreis)
    @Test
    void insertForeisFromCsv_ShouldInsertValidRowsAndSkipInvalid() throws Exception {
        // Καλούμε τη μέθοδο insertForeisFromCsv με το όνομα του mock αρχείου.
        // Ο DataImporter θα ψάξει για το αρχείο στο testing classpath (/data/B23ForeisTEST.csv)
        
        DataImporter.insertForeisFromCsv("B23ForeisTEST.csv");

        // Επαλήθευση: 
        // 1. Πρέπει να έχουν εισαχθεί 2 γραμμές (1001 και 1002) - η γραμμή 1003 είναι invalid
        int count = countRows("foreis");
        assertEquals(2, count, "Πρέπει να έχουν εισαχθεί 2 έγκυρες γραμμές στον πίνακα foreis.");

        // 2. Ελέγχουμε την τιμή μιας συγκεκριμένης γραμμής
        try (Connection conn = DriverManager.getConnection(TEST_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT name, regular_budget FROM foreis WHERE foreas_id = 1001");
             ResultSet rs = pstmt.executeQuery()) {
            
            assertTrue(rs.next(), "Δεν βρέθηκε ο φορέας 1001.");
            assertEquals("Υπ. Οικονομικών", rs.getString("name"), "Το όνομα του φορέα δεν ταιριάζει.");
            assertEquals(100000.50, rs.getDouble("regular_budget"), 0.001, "Το budget δεν ταιριάζει.");
        }
    }

    // Test Case: Εισαγωγή Ροών (Cashflows)
    @Test
    void insertCashflowsFromCsv_ShouldInsertValidRowsAndSkipInvalid() throws Exception {
        // Καλούμε τη μέθοδο insertCashflowsFromCsv με το όνομα του mock αρχείου.
        
        DataImporter.insertCashflowsFromCsv("B23EsodaTEST.csv", "Έσοδο");

        // Επαλήθευση: 
        // 1. Πρέπει να έχουν εισαχθεί 2 γραμμές (ΦΠΑ, Φόρος Εισοδήματος) - η 3η γραμμή είναι invalid
        int count = countRows("cashflows");
        assertEquals(2, count, "Πρέπει να έχουν εισαχθεί 2 έγκυρες γραμμές στον πίνακα cashflows.");

        // 2. Ελέγχουμε την τιμή μιας συγκεκριμένης γραμμής
        try (Connection conn = DriverManager.getConnection(TEST_URL);
             PreparedStatement pstmt = conn.prepareStatement("SELECT amount, type FROM cashflows WHERE name = 'ΦΠΑ'");
             ResultSet rs = pstmt.executeQuery()) {
            
            assertTrue(rs.next(), "Δεν βρέθηκε η ροή 'ΦΠΑ'.");
            assertEquals(250000000.75, rs.getDouble("amount"), 0.001, "Το ποσό δεν ταιριάζει.");
            assertEquals("Έσοδο", rs.getString("type"), "Ο τύπος (type) δεν ταιριάζει.");
        }
    }

    // Test Case: Έλεγχος Missing CSV File
    @Test
    void insertForeisFromCsv_ShouldThrowFileNotFoundException() {
        // Αναμένουμε μια εξαίρεση για αρχείο που δεν υπάρχει (π.χ., "B26Foreis.csv")
        Exception exception = assertThrows(Exception.class, () -> {
            DataImporter.insertForeisFromCsv("B26Foreis.csv"); // Δεν υπάρχει
        });

        // Ελέγχουμε αν το μήνυμα της εξαίρεσης είναι αυτό που περιμένουμε.
        assertTrue(exception.getMessage().contains("Δεν βρέθηκε το αρχείο"));
    }
}