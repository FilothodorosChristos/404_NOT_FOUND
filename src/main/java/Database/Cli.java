package database;

public class Cli {

    public static void main(String[] args) {
          
        DatabaseSetup.setDatabase();
        DataImporter.importer();
    }
}