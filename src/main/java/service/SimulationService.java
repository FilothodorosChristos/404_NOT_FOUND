package service;

import database.DataImporter;
import util.DbExistsChecker;

public class SimulationService {

    /**
     * Κανονική μέθοδος για να ξεκινήσει νέα προσομοίωση
     */
    public static void startNewSimulation() {
        DataImporter.importer();
    }

    /**
     * Καλείται στην εκκίνηση της εφαρμογής.
     * Αν ΔΕΝ υπάρχει το budgetDB.db, τότε το δημιουργεί ξανά.
     */
    public static void startIfDatabaseMissing() {
        if (!DbExistsChecker.databaseExists()) {
            startNewSimulation();
        }
    }
}
