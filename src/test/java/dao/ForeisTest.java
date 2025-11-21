package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test για την κλάση Foreis.
 * Καλύπτει getters, setters, constructor και toString().
 */
public class ForeisTest {

  @Test
    public void testConstructorAndGetters() {
    // Δημιουργία αντικειμένου με constructor
    Foreis f = new Foreis(
                1,
                10,
                2024,
                "TYPE_A",
                "Original Name",
                100.0,
                200.0,
                300.0
        );

    // Έλεγχος getters
    assertEquals(1, f.getId());
    assertEquals(10, f.getForeasId());
    assertEquals(2024, f.getYearId());
    assertEquals("TYPE_A", f.getType());
    assertEquals("Original Name", f.getName());
    assertEquals(100.0, f.getRegularBudget());
    assertEquals(200.0, f.getPublicInvBudget());
    assertEquals(300.0, f.getTotal());
  }

  @Test
    public void testSettersAndGetters() {
    Foreis f = new Foreis(0, 0, 0, "", "", 0, 0, 0);

    // Ορισμός νέων τιμών με setters
    f.setId(2);
    f.setForeasId(20);
    f.setYearId(2025);
    f.setType("TYPE_B");
    f.setName("New Name");
    f.setRegularBudget(111.1);
    f.setPublicInvBudget(222.2);
    f.setTotal(333.3);

    // Έλεγχος getters
    assertEquals(2, f.getId());
    assertEquals(20, f.getForeasId());
    assertEquals(2025, f.getYearId());
    assertEquals("TYPE_B", f.getType());
    assertEquals("New Name", f.getName());
    assertEquals(111.1, f.getRegularBudget());
    assertEquals(222.2, f.getPublicInvBudget());
    assertEquals(333.3, f.getTotal());
  }

  @Test
    public void testToString() {
    Foreis f = new Foreis(
                1,
                10,
                2024,
                "TYPE_A",
                "Original Name",
                100.0,
                200.0,
                300.0
        );

    String str = f.toString();
    assertTrue(str.contains("id=1"));
    assertTrue(str.contains("foreasId=10"));
    assertTrue(str.contains("yearId=2024"));
    assertTrue(str.contains("type='TYPE_A'"));
    assertTrue(str.contains("name='Original Name'"));
    assertTrue(str.contains("regularBudget=100.0"));
    assertTrue(str.contains("publicInvBudget=200.0"));
    assertTrue(str.contains("total=300.0"));
  }
}


