package menus;
import org.junit.jupiter.api.Test;
import java.io.*; // Import necessary for InputStream, ByteArrayInputStream, PrintStream, ByteArrayOutputStream
import static org.junit.jupiter.api.Assertions.*;
public class ConsoleIoTest {
    @Test
    void testGetString() {
        // Use a ByteArrayInputStream object to simulate user input
        InputStream originalIn = System.in;
        try {
            String testInput = "Hello\n";
            ByteArrayInputStream in = new ByteArrayInputStream(testInput.getBytes());
            // set the standard input to our test input
            System.setIn(in);
            ConsoleIo testobj = new ConsoleIo();
            String result = testobj.getString("Hello Test");
            assertEquals("Hello", result, "Πρέπει να επιστρέφει την σωστή συμβολοσειρά.");
        } finally {
            // Restore the original System.in
            System.setIn(originalIn);
        }
    }
    @Test
    void testGetInt_validInvalidInput() {
        InputStream originalIn = System.in;
        // Save the origial System.out
        PrintStream originalOut = System.out;
        try {
            //test invalid input first and then valid input
            String testInput = "otinanai\n42\n";
            ByteArrayInputStream in = new ByteArrayInputStream(testInput.getBytes());
            System.setIn(in);
            // set up to capture system.out output via ByteArrayOutputStream class
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            ConsoleIo testobj = new ConsoleIo();
            int result = testobj.getInt("Εισάγετε αριθμό:");
            assertEquals(42, result, "Πρέπει να επιστρέφει τον σωστό ακέραιο.");
            String output = outContent.toString();
            assertTrue(output.contains("Μη έγκυρος αριθμός. Παρακαλώ δοκιμάστε ξανά."), "Πρέπει να εμφανίζει μήνυμα λάθους για μη έγκυρο αριθμό.");

        } finally {
            // Restore original System.in and System.out
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
    @Test
    void testShowMessage(){
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            String testInput = "\n"; // Randm input to test showMessage
            ByteArrayInputStream in = new ByteArrayInputStream(testInput.getBytes());
            System.setIn(in);
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            ConsoleIo testobj = new ConsoleIo();
            String message = "This is a test message.";
            testobj.showMessage(message);
            String output = outContent.toString();
            assertTrue(output.contains(message), "Πρέπει να εμφανίζει το σωστό μήνυμα.");
        }finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
    @Test
    void testExit(){
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            String testInput = "\n"; // Random input to test exit
            ByteArrayInputStream in = new ByteArrayInputStream(testInput.getBytes());
            System.setIn(in);
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            ConsoleIo testobj = new ConsoleIo();
            // Since exit() will call System.exit(0), we need to catch the SecurityException
            try {
                testobj.exit();
                fail("Πρέπει να καλέσει το System.exit και να ρίξει SecurityException.");
            } catch (SecurityException e) {
                // Expected exception
            }
            String output = outContent.toString();
            assertTrue(output.contains("Έξοδος από την εφαρμογή. Αντίο!"), "Πρέπει να εμφανίζει το μήνυμα εξόδου.");
        }finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}

