package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class για την ForeasCompareDto.
 */
class ForeasCompareDtoTest {

  @Test
    void testForeasId() {
    ForeasCompareDto dto = new ForeasCompareDto();
    dto.setForeasId(10);
    assertEquals(10, dto.getForeasId());
  }

  @Test
    void testName() {
    ForeasCompareDto dto = new ForeasCompareDto();
    dto.setName("Test Name");
    assertEquals("Test Name", dto.getName());
  }

  @Test
    void testRegularBudget() {
    ForeasCompareDto dto = new ForeasCompareDto();

    dto.setRegularYear1(1000.0);
    dto.setRegularYear2(1200.0);
    dto.setRegularDiff(200.0);
    dto.setRegularPercentChange(20.0);

    assertEquals(1000.0, dto.getRegularYear1());
    assertEquals(1200.0, dto.getRegularYear2());
    assertEquals(200.0, dto.getRegularDiff());
    assertEquals(20.0, dto.getRegularPercentChange());
  }

  @Test
    void testPublicInvestmentBudget() {
    ForeasCompareDto dto = new ForeasCompareDto();

    dto.setPublicInvYear1(500.0);
    dto.setPublicInvYear2(600.0);
    dto.setPublicInvDiff(100.0);
    dto.setPublicInvPercentChange(20.0);

    assertEquals(500.0, dto.getPublicInvYear1());
    assertEquals(600.0, dto.getPublicInvYear2());
    assertEquals(100.0, dto.getPublicInvDiff());
    assertEquals(20.0, dto.getPublicInvPercentChange());
  }

  @Test
    void testTotal() {
    ForeasCompareDto dto = new ForeasCompareDto();

    dto.setTotalYear1(1500.0);
    dto.setTotalYear2(1800.0);
    dto.setTotalDiff(300.0);
    dto.setTotalPercentChange(20.0);

    assertEquals(1500.0, dto.getTotalYear1());
    assertEquals(1800.0, dto.getTotalYear2());
    assertEquals(300.0, dto.getTotalDiff());
    assertEquals(20.0, dto.getTotalPercentChange());
  }
}
