package menus;
public abstract class Menu {
    protected final UserIo IO;

    public Menu(UserIo io) {
        this.IO = io;
    }

    // η μέθοδος που κάθε υπομενού πρέπει να υλοποιήσει
    protected abstract void handleChoice(int choice);

    // κοινή μέθοδος για εμφάνιση μενού και έλεγχο επιλογών
    public void showMenu(String message, int optionsCount) {
        int choice;
        do {
            IO.showMessage(message);
            choice = IO.getInt("Επιλογή: ");
            if (choice < 1 || choice > optionsCount) {
                IO.showMessage("Μη έγκυρη επιλογή. Δώσε αριθμό 1-" + optionsCount);
            }
        } while (choice < 1 || choice > optionsCount);

        handleChoice(choice);
    }
}
