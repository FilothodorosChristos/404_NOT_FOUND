package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class DatabaseManager {

    // Το path της working database (εκεί που αλλάζει ο χρήστης)
    private static final Path WORKING_DB = Path.of("budgetDB.db");

    // Το path του template μέσα στα resources
    private static final String CLEAN_DB_RESOURCE = "/db/OgBudgetDB.db";

    /**
     * Επιστρέφει το JDBC URL της τρέχουσας working DB.
     */
    public static String getJdbcUrl() {
        return "jdbc:sqlite:" + WORKING_DB.toAbsolutePath();
    }

    /**
     * Αντιγράφει την καθαρή βάση από τα resources στο working DB.
     */
    public static void copyOgDatabase() throws IOException {
        try (InputStream is = DatabaseManager.class.getResourceAsStream(CLEAN_DB_RESOURCE)) {

            if (is == null) {
                throw new IOException("Δεν βρέθηκε το OgBudgetDB.db μέσα στα resources!");
            }

            Files.copy(is, WORKING_DB, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Αντιγραφή της καθαρής βάσης ολοκληρώθηκε.");
        }
    }

    /**
     * Επιστρέφει true αν υπάρχει ήδη working DB στο filesystem.
     */
    public static boolean workingDbExists() {
        return Files.exists(WORKING_DB);
    }

    /**
     * Αν δεν υπάρχει working DB, προσπάθησε να αντιγράψεις clean DB.
     * Αν δεν υπάρχει ούτε clean DB → fallback σε importer().
     */
    public static void initializeDatabase() {

        if (!workingDbExists()) {
            System.out.println("Δεν υπάρχει working database. Προσπάθεια αντιγραφής clean DB...");

            try {
                copyOgDatabase();

            } catch (Exception e) {
                System.err.println("Αποτυχία αντιγραφής clean DB: " + e.getMessage());
                System.err.println("Fallback: Δημιουργία βάσης από CSV...");

                // Fallback: φτιάχνουμε τη βάση από CSV
            DatabaseSetup.setURL(getJdbcUrl());
            DataImporter.setURL(getJdbcUrl());
            DataImporter.importer();
            }

            
        }

        System.out.println("Η βάση δεδομένων είναι έτοιμη.");
    }

    /**
     * Διαγράφει την working DB και αντιγράφει ξανά την clean DB.
     */
    public static void resetDatabase() {
        try {
            Files.deleteIfExists(WORKING_DB);
            copyOgDatabase();
            System.out.println("Η βάση επαναφέρθηκε στην αρχική της κατάσταση.");
        } catch (Exception e) {
            System.err.println("Reset DB failed: " + e.getMessage());
        }
    }
}
 