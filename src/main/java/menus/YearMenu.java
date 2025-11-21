package menus;
public class YearMenu extends Menu {

  private final MainMenu parentMenu;

  public YearMenu(UserIo io, MainMenu parentMenu) {
    super(io);
    this.parentMenu = parentMenu;
  }

  public void show() {
      showMenu("""
          --- Επιλέξτε έτος ---
          1) 2023
          2) 2024
          3) 2025
          4) Πίσω
      """, 4);
  }

   @Override
  protected void handleChoice(int option) {
    switch (option) {
      case 1 -> new ActionsMenu(IO, 2023).show();
      case 2 -> new ActionsMenu(IO, 2024).show();
      case 3 -> new ActionsMenu(IO, 2025).show();
      case 4 -> parentMenu.show();
      default -> IO.showMessage("Μη έγκυρη επιλογή.");
    }
  }
}
