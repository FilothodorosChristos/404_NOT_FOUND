package service;

import database.DataImporter;
import util.FirstRunChecker;

public class SimulationService {

    // Κανονική μέθοδος για να ξεκινά προσομοίωση
    public void startNewSimulation() {
        DataImporter.importer();
    }

    // Μέθοδος που καλείται κατά την εκκίνηση
    public void startIfFirstRun() {
        if (FirstRunChecker.isFirstRun()) {
            startNewSimulation();
            FirstRunChecker.markAsRun();
        }
    }
}
