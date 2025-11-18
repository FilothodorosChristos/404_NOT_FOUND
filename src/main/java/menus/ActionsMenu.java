
public class ActionsMenu extends Menu {
    private final int year;

    public ActionsMenu(UserIo io, int year) {
        super(io);
        this.year = year;
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
            case 1 -> { IO.showMessage("Προβολή δεδομένων για " + year);
            IO.showMessage("Έσοδα");
            CashFlowDao.selectCashFlow(year, "Έσοδο");
            IO.showMessage("Έξοδα");
            CashFlowDao.selectCashFlow(year, "Έξοδο");
            IO.showMessage("Φορείς");
            ForeisDao.selectForeis(year);
            }
            case 2 -> IO.showMessage("Επεξεργασία δεδομένων για " + year);
            case 3 -> IO.showMessage("Σύγκριση δεδομένων για " + year);
            case 4 -> IO.showMessage("Εκτέλεση σεναρίων για " + year);
            case 5 -> new BudgetYearMenu(IO).show(); // επιστροφή στο μενού επιλογής έτους
        }
    }
}
