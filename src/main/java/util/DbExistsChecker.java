package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FirstRunChecker {

    private static String flagFile = "first_run.flag";

    // Επιτρέπει αλλαγή του path για tests
    public static void setFlagFile(String filename) {
        if (filename != null && !filename.isEmpty()) {
            flagFile = filename;
        }
    }

    /**
     * Ελέγχει αν η εφαρμογή τρέχει για πρώτη φορά.
     * @return true αν είναι πρώτη φορά, false αλλιώς
     */
    public static boolean isFirstRun() {
        File file = new File(flagFile);
        return !file.exists();
    }

    /**
     * Σημειώνει ότι η εφαρμογή έχει ήδη τρέξει
     */
    public static void markAsRun() {
        File file = new File(flagFile);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("initialized=true");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Σφάλμα κατά τη δημιουργία του flag file: " + e.getMessage());
            }
        }
    }
}


