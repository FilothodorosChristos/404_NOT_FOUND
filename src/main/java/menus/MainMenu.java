package menus;

import database.DataImporter;

public class MainMenu extends Menu {

    public MainMenu(UserIo io) {
        super(io);
    }

    public void show() {
        showMenu("""
            ---  Καλωσορίσατε! ---
            ---    Επιλέξτε:   ---
            1) Νέα προσομοίωση
            2) Συνέχεια προσομοίωσης
        """, 2);
    }

    @Override
    protected void handleChoice(int choice) {
        switch(choice) {
            case 1 -> { 
                DataImporter.importer();
                System.out.println("Νέα προσομοίωση ξεκίνησε.");
            }
            case 2 -> IO.showMessage("Φόρτωση προηγούμενης προσομοίωσης...");
        }
    }
}
