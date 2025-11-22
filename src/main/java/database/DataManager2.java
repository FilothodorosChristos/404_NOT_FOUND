package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

/**
 * ΓΙΑ BACKUP ΜΟΝΟ! ΜΗ ΓΡΑΨΕΤΕ ΤΕΣΤ ΓΙΑ ΑΥΤΗ ΤΗΝ ΚΛΑΣΗ!
 * Διαχείριση της SQLite βάσης δεδομένων της εφαρμογής.
 * 
 * Περιλαμβάνει:
 * - Απόλυτο path στο home folder του χρήστη για ασφαλή αποθήκευση.
 * - Αντιγραφή clean database από resources.
 * - Fallback σε CSV import αν χρειαστεί.
 * - Επαναφορά βάσης με reset.
 */
public class DataManager2 {

    // Απόλυτο path της working DB στον φάκελο χρήστη
    private static Path workingDb = Path.of(System.getProperty("user.home"), "myapp", "budgetDB.db");

    // Resource της clean database
    private static final String CLEAN_DB_RESOURCE = "/db/originalDB.db";

    /**
     * Επιστρέφει το JDBC url της τρέχουσας working DB.
     */
    public static String getJdbcUrl() {
        return "jdbc:sqlite:" + workingDb.toAbsolutePath();
    }
    

    public static void setWorkingDb(Path path) {
        workingDb = path;
    }

    /**
     * Αντιγράφει την original database από τα resources στη working DB.
     */
    public static void copyOgDatabase() throws IOException {
        Files.createDirectories(workingDb.getParent());

        try (InputStream is = DataManager2.class.getResourceAsStream(CLEAN_DB_RESOURCE)) {
            if (is == null) {
                throw new IOException("Το originalDB.db δεν βρέθηκε στα resources!");
            }

            Files.copy(is, workingDb, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Αντιγραφή της καθαρής βάσης ολοκληρώθηκε: " + workingDb);
        }
    }

    /**
     * Επιστρέφει true αν υπάρχει ήδη working DB στο filesystem.
     */
    public static boolean workingDbExists() {
        return Files.exists(workingDb);
    }

    /**
     * Αρχικοποιεί τη βάση δεδομένων:
     * - Αν υπάρχει working DB → δεν κάνει τίποτα.
     * - Αν δεν υπάρχει → προσπαθεί να αντιγράψει clean DB.
     * - Αν αποτύχει → fallback σε CSV import.
     */
    public static void initializeDatabase() {
        if (!workingDbExists()) {
            System.out.println("Δεν υπάρχει working database. Προσπάθεια αντιγραφής originalDB...");

            try {
                copyOgDatabase();
                return;
            } catch (Exception e) {
                System.err.println("Αποτυχία αντιγραφής originalDB: " + e.getMessage());
                System.err.println("Fallback: Δημιουργία βάσης από CSV...");
            }

            // Fallback: φτιάχνουμε τη βάση από CSV
            DatabaseSetup.setURL(getJdbcUrl());
            DataImporter.setURL(getJdbcUrl());
            DataImporter.importer();
        }

        System.out.println("Η βάση δεδομένων είναι έτοιμη: " + workingDb);
    }

    /**
     * Διαγράφει την working DB και αντιγράφει ξανά την clean DB.
     */
    public static void resetDatabase() {
        try {
            Files.createDirectories(workingDb.getParent());

            try (InputStream is = DataManager2.class.getResourceAsStream(CLEAN_DB_RESOURCE)) {
                if (is == null) {
                    throw new IOException("Το originalDB δεν βρέθηκε στα resources!");
                }

                Files.copy(is, workingDb, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Η βάση επαναφέρθηκε στην αρχική της κατάσταση: " + workingDb);
            }

        } catch (IOException e) {
            System.err.println("Αποτυχία reset της βάσης: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
