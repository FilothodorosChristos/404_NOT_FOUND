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
  int choice = -1;
  do {
    IO.showMessage(message);
    try {
      choice = IO.getInt("Επιλογή: ");
      if (choice < 1 || choice > optionsCount) {
        IO.showMessage("Μη έγκυρη επιλογή. Δώσε αριθμό 1-" + optionsCount);
      }
    } catch (NumberFormatException e) {
      IO.showMessage("Μη έγκυρη είσοδος. Πρέπει να δώσεις έναν αριθμό.");
    }
    } while (choice < 1 || choice > optionsCount);

    handleChoice(choice);
  }

}
