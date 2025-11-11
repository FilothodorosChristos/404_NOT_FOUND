package database;

public class MainT1 {

    public static void main(String[] args) {
        CreateDatabase.createDB();  
        DatabaseSetup.setDatabase();
        DataImp2.importer();
    }
}