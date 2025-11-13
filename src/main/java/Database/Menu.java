package database;
import java.util.Scanner;

public class Cli {
    private int answer;
    public void menu() {
        Scanner myScan = new Scanner(System.in);
        System.out.printf("Καλωσορίσατε στο Goverlens!\n Θέλετε να ξεκινήσετε νέα προσωομίωση; \n 1.Ναι \n 2.Όχι \n");
        answer = myScan.nextInt();
        DatabaseSetup.setDatabase();
        DataImporter.importer();
    }
}