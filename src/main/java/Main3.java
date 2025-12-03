import database.DatabaseSetup;
import database.DataImporter;

public class Main3 {
    public static void main(String args[]) {
    DatabaseSetup.setDatabase();
    DataImporter.importer();
    }
}
