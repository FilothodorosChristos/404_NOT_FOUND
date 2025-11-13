package database;  
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
public class CreateDatabaseTest {

    @Test
    public void testCreateDB() throws Exception {
        Path dbPath = Paths.get("budgetDB.db");
        // Ensure the database does not exist before the test
        Files.deleteIfExists(dbPath);

        // Call the method to create the database   
        database.CreateDatabase.createDB();

        // Verify the database file was created
        Assertions.assertTrue(Files.exists(dbPath));

        // Clean up after test
        Files.deleteIfExists(dbPath);
    }

    @Test
    public void testCreateDB_idempotent_whenCalledTwice() throws Exception {
        Path dbPath = Paths.get("budgetDB.db");
        Files.deleteIfExists(dbPath);
        database.CreateDatabase.createDB();
        database.CreateDatabase.createDB();

        // Verify the database file still exists and did not cause an error 
        Assertions.assertTrue(Files.exists(dbPath));
        Files.deleteIfExists(dbPath);
    }
}
