package util;

import java.io.File;

public class DbExistsChecker {

    private static String dbFile = "budgetDB.db";

    // Επιτρέπει αλλαγή του filename για tests ή άλλες χρήσεις
    public static void setDbFile(String filename) {
        if (filename != null && !filename.isEmpty()) {
            dbFile = filename;
        }
    }

    /**
     * Έλεγχος αν υπάρχει το αρχείο της βάσης στο working directory.
     * @return true αν υπάρχει, false αν ΔΕΝ υπάρχει
     */
    public static boolean databaseExists() {
        File file = new File(dbFile);
        return file.exists();
    }
}

