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
            3) Έξοδος
        """, 3);
    }

    @Override
    protected void handleChoice(int choice) {
        switch(choice) {
            case 1 -> { 
                DataImporter.importer();
                System.out.println("Νέα προσομοίωση ξεκίνησε.");
                YearMenu myYearMenu = new YearMenu(IO, this);
                myYearMenu.show();
            }
            case 2 -> {
                IO.showMessage("Φόρτωση προηγούμενης προσομοίωσης...");
                YearMenu myYearMenu = new YearMenu(IO, this);
                myYearMenu.show();
            }
            case 3 -> {
                IO.exit();
            }
        }
    }
}
