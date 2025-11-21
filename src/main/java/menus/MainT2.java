package menus;

public class MainT2 {
    
    public static void main(String[] args) {
        UserIo consoleIo = new ConsoleIo();
        MainMenu myMainMenu = new MainMenu(consoleIo);
        myMainMenu.show();
        myMainMenu.handleChoice(2);



}

}