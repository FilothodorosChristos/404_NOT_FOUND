package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.Foreis;
import database.DatabaseSetup;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.*;

public class ForeisServiceTest {

  private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
  private static final String TEST_URL = "jdbc:sqlite:test_foreis.db";
  private static final String TEST_FILE = "test_foreis.db";

  private ForeisService service;

  @BeforeEach
    void setup() {
        
    DatabaseSetup.setURL(TEST_URL);

       
    DatabaseSetup.resetTables();

    service = new ForeisService();
  }

  @AfterAll
    static void tearDown() {
        
    DatabaseSetup.setURL(ORIGINAL_URL);
    File f = new File(TEST_FILE);
    if (f.exists()) {
      f.delete();
    }
  }

  @Test
    void testAddForeisSuccess() {
    Foreis f = new Foreis(
                0, 101, 2023, "Τακτικός",
                "Υπουργείο Παιδείας",
                100.0, 50.0, 150.0
        );

    assertDoesNotThrow(() -> service.addForeis(f));

    List<Foreis> list = service.getForeisByYearAndType(2023, "Τακτικός");
    assertEquals(1, list.size());
    assertEquals(150.0, list.get(0).getTotal());
  }

  @Test
    void testAddForeisInvalidTotal() {
    Foreis f = new Foreis(
                0, 101, 2023, "Τακτικός",
                "Υπουργείο",
                100.0, 50.0, 200.0 
        );

    assertThrows(IllegalArgumentException.class, () -> service.addForeis(f));
  }

  @Test
    void testUpdateForeisSuccess() {
       
    Foreis f = new Foreis(
                0, 101, 2023, "Τακτικός",
                "Υπουργείο",
                100.0, 50.0, 150.0
        );
    service.addForeis(f);

        
    Foreis existing = service.getForeisByYearAndType(2023, "Τακτικός").get(0);

        
    Foreis updated = new Foreis(
                existing.getId(),
                101,
                2023,
                "Τακτικός",
                "Υπουργείο",
                120.0,
                50.0,
                170.0 
        );

    assertDoesNotThrow(() -> service.updateForeis(updated));

    Foreis after = service.getForeisByYearAndType(2023, "Τακτικός").get(0);
    assertEquals(170.0, after.getTotal());
  }

  @Test
    void testUpdateForeisTooLargeChange() {
        
    Foreis f = new Foreis(
                0, 101, 2023, "Τακτικός",
                "Υπουργείο",
                100.0, 50.0, 150.0
        );
    service.addForeis(f);

        
    Foreis existing = service.getForeisByYearAndType(2023, "Τακτικός").get(0);

        
    Foreis updated = new Foreis(
                existing.getId(),
                101,
                2023,
                "Τακτικός",
                "Υπουργείο",
                300.0,
                300.0,
                600.0 
        );

    assertThrows(IllegalArgumentException.class, () -> service.updateForeis(updated));
  }

  @Test
    void testDeleteForeisSuccess() {
    Foreis f = new Foreis(
                0, 101, 2023, "Τακτικός",
                "Υπουργείο",
                100.0, 50.0, 150.0
        );
    service.addForeis(f);

    Foreis existing = service.getForeisByYearAndType(2023, "Τακτικός").get(0);

    assertDoesNotThrow(() -> service.deleteForeis(existing.getId()));

    List<Foreis> list = service.getForeisByYearAndType(2023, "Τακτικός");
    assertEquals(0, list.size());
  }

  @Test
    void testDeleteForeisInvalidId() {
    assertThrows(IllegalArgumentException.class, () -> service.deleteForeis(0));
  }

  @Test
void testGetForeisByYearAndTypeSuccess() {
    Foreis f = new Foreis(
            0, 101, 2023, "Τακτικός",
            "Υπουργείο",
            100.0, 50.0, 150.0
    );
    service.addForeis(f);

    List<Foreis> list = service.getForeisByYearAndType(2023, "Τακτικός");

    assertEquals(1, list.size());
    assertEquals("Υπουργείο", list.get(0).getName());
  }

  @Test
void testGetForeisByYearAndTypeInvalidYear() {
    assertThrows(IllegalArgumentException.class,
            () -> service.getForeisByYearAndType(2027, "Τακτικός"));
  }

  @Test
void testGetForeisByYearAndTypeInvalidType() {
    assertThrows(IllegalArgumentException.class,
            () -> service.getForeisByYearAndType(2023, ""));
  }

  @Test
void testAddForeisNull() {
    assertThrows(IllegalArgumentException.class, () -> service.addForeis(null));
}

@Test
void testUpdateForeisNullOrZeroId() {
    assertThrows(IllegalArgumentException.class, () -> service.updateForeis(null));
    Foreis f = new Foreis(0, 101, 2023, "TYPE", "Name", 10, 20, 30);
    assertThrows(IllegalArgumentException.class, () -> service.updateForeis(f));
}

@Test
void testUpdateForeisNonExisting() {
    Foreis f = new Foreis(999, 101, 2023, "TYPE", "Name", 10, 20, 30);
    assertThrows(IllegalArgumentException.class, () -> service.updateForeis(f));
}

@Test
void testUpdateForeisTotalMismatch() {
    service.addForeis(new Foreis(0, 101, 2023, "TYPE", "Name", 10, 20, 30));
    Foreis existing = service.getForeisByYearAndType(2023, "TYPE").get(0);
    Foreis updated = new Foreis(existing.getId(), 101, 2023, "TYPE", "Name", 10, 20, 50);
    assertThrows(IllegalArgumentException.class, () -> service.updateForeis(updated));
}

@Test
void testCompareYearsSameYear() {
    assertThrows(IllegalArgumentException.class, () -> service.compareYears(2023, 2023));
}

@Test
void testCompareYearsInvalidYear() {
    assertThrows(IllegalArgumentException.class, () -> service.compareYears(2020, 2023));
    assertThrows(IllegalArgumentException.class, () -> service.compareYears(2023, 2027));
}

}


