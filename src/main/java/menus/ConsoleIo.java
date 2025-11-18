import java.util.Scanner;

public class ConsoleIO implements UserIO {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String getString(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    @Override
    public int getInt(String message) {
        while (true) {
            System.out.println(message);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Μη έγκυρος αριθμός. Παρακαλώ δοκιμάστε ξανά.");
            }
        }
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }
}
 