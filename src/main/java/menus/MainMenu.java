public class MainMenu extends Menu {

    public MainMenu(UserIO io) {
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
            case 2 -> io.showMessage("Φόρτωση προηγούμενης προσομοίωσης...");
        }
    }
}
