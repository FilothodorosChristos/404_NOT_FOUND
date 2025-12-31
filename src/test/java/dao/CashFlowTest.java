package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class για την κλάση CashFlow.
 * Ελέγχει πλήρως constructor, getters, setters και toString().
 */
public class CashFlowTest {

  @Test
    public void testConstructorAndGetters() {
    CashFlow cf = new CashFlow(1, 2023, "Income", "Revenue", 1000.0);

    // Ελέγχουμε ότι ο constructor έχει αρχικοποιήσει σωστά όλα τα πεδία
    assertEquals(1, cf.getId());
    assertEquals(2023, cf.getYearId());
    assertEquals("Income", cf.getType());
    assertEquals("Revenue", cf.getName());
    assertEquals(1000.0, cf.getAmount());
  }

  @Test
    public void testSetters() {
    CashFlow cf = new CashFlow(0, 0, "", "", 0.0);

    // Αλλάζουμε όλες τις τιμές με τους setters
    cf.setId(2);
    cf.setYearId(2024);
    cf.setName("Expense");
    cf.setType("Cost");
    cf.setAmount(500.0);

    // Ελέγχουμε ότι οι setters άλλαξαν τις τιμές σωστά
    assertEquals(2, cf.getId());
    assertEquals(2024, cf.getYearId());
    assertEquals("Cost", cf.getType());
    assertEquals("Expense", cf.getName());
    assertEquals(500.0, cf.getAmount());
  }

  @Test
    public void testToString() {
    CashFlow cf = new CashFlow(3, 2025, "Revenue", "Donation", 200.0);
    String str = cf.toString();

    // Ελέγχουμε ότι η toString περιέχει τα σωστά δεδομένα
    assertTrue(str.contains("id=3"));
    assertTrue(str.contains("yearId=2025"));
    assertTrue(str.contains("type='Revenue'"));
    assertTrue(str.contains("name='Donation'"));
    assertTrue(str.contains("amount=200.0"));
  }
}


