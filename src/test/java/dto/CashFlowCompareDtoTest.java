package dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CashFlowCompareDtoTest {

  @Test
    public void testToString_BothYearsAvailable() {
    CashFlowCompareDto dto = new CashFlowCompareDto("Ενοίκιο", 1000, 1100, false, false);
    String expected = "Όνομα: Ενοίκιο | Έτος 1: 1000.0 | Έτος 2: 1100.0";
    assertEquals(expected, dto.toString());
  }

  @Test
    public void testToString_MissingInYear1() {
    CashFlowCompareDto dto = new CashFlowCompareDto("Μισθός", 0, 3000, true, false);
    String expected = "Όνομα: Μισθός | Έτος 1: Μη διαθέσιμο | Έτος 2: 3000.0";
    assertEquals(expected, dto.toString());
  }

  @Test
    public void testToString_MissingInYear2() {
    CashFlowCompareDto dto = new CashFlowCompareDto("Δώρο", 500, 0, false, true);
    String expected = "Όνομα: Δώρο | Έτος 1: 500.0 | Έτος 2: Μη διαθέσιμο";
    assertEquals(expected, dto.toString());
  }
}

