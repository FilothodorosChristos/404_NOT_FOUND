package menus;

import dao.CashFlowDao;
import dao.ForeisDao;

public class ActionsMenu extends Menu {
    private final int year;
    private final YearMenu PARENT_MENU;

    public ActionsMenu(UserIo io, int year, YearMenu parentMenu) {
        super(io);
        this.year = year;
        this.PARENT_MENU = parentMenu;
    }

    public void show() {
        showMenu("""
            --- Μενού Προσομοίωσης ---
            1) Προβολή
            2) Επεξεργασία
            3) Σύγκριση
            4) Εκτέλεση Σεναρίων
            5) Πίσω
        """, 5);
    }

    @Override
    protected void handleChoice(int choice) {
        switch(choice) {
            case 1 -> IO.showMessage("Προβολή δεδομένων για " + year);
            case 2 -> IO.showMessage("Επεξεργασία δεδομένων για " + year);
            case 3 -> IO.showMessage("Σύγκριση δεδομένων για " + year);
            case 4 -> IO.showMessage("Εκτέλεση σεναρίων για " + year);
            case 5 -> PARENT_MENU.show(); // επιστροφή στο μενού επιλογής έτους
        }
    }
}
