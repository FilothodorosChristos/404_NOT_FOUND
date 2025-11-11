package database;

public class MainT1 {

    public static void main(String[] args) {
        CreateDatabase.createDatabase();  
        DatabaseSetup.setupDatabase();
        DataImport.importer();
    }
}