package service;

import static org.junit.jupiter.api.Assertions.*;

import dao.CashFlow;
import dao.CashFlowDao;
import database.DatabaseSetup;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.*;

class ScenarioCashflowServiceTest {

    private static final String ORIGINAL_URL = "jdbc:sqlite:budgetDB.db";
    private static final String TEST_URL = "jdbc:sqlite:test_scenario_cashflow.db";
    private static final String TEST_FILE = "test_scenario_cashflow.db";

    private ScenarioCashflowService service;
    private CashFlowDao dao;

    @BeforeEach
    void setUp() {
        DatabaseSetup.setURL(TEST_URL);
        DatabaseSetup.setDatabase();
        DatabaseSetup.resetTables();

        service = new ScenarioCashflowService();
        dao = new CashFlowDao();
    }

    @AfterEach
    void tearDown() {
        DatabaseSetup.setURL(ORIGINAL_URL);
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }


    @Test
    void invalidTypeThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> service.updateCashflowWithModifiedAmount(2023, "Λάθος", 10));
    }

    @Test
    void invalidYearThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> service.updateCashflowWithModifiedAmount(2030, "Έσοδο", 10));
    }

    @Test
    void invalidPercentageNaNThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> service.updateCashflowWithModifiedAmount(2023, "Έσοδο", Double.NaN));
    }

    @Test
    void invalidPercentageInfiniteThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> service.updateCashflowWithModifiedAmount(2023, "Έσοδο", Double.POSITIVE_INFINITY));
    }


   @Test
    void increaseCashflowAmounts() {
    dao.addCashFlow(new CashFlow(0, 2023, "Έσοδο", "Μισθός", 1000.0));
    dao.addCashFlow(new CashFlow(1, 2023, "Έσοδο", "Bonus", 500.0));

    int updated = service.updateCashflowWithModifiedAmount(2023, "Έσοδο", 10);

    assertEquals(2, updated);

    List<CashFlow> list = dao.selectCashFlow(2023, "Έσοδο");

    for (CashFlow cf : list) {
        if (cf.getName().equals("Μισθός")) {
            assertEquals(1100.0, cf.getAmount());
        } else if (cf.getName().equals("Bonus")) {
            assertEquals(550.0, cf.getAmount());
        } else {
            fail("Άγνωστο CashFlow: " + cf.getName());
        }
    }
}


    @Test
    void decreaseCashflowAmounts() {
        dao.addCashFlow(new CashFlow(0, 2024, "Έξοδο", "Ενοίκιο", 800.0));

        int updated = service.updateCashflowWithModifiedAmount(2024, "Έξοδο", -25);

        assertEquals(1, updated);

        CashFlow cf = dao.selectCashFlow(2024, "Έξοδο").get(0);
        assertEquals(600.0, cf.getAmount());
    }

    @Test
    void noCashflowsFoundReturnsZero() {
        int updated = service.updateCashflowWithModifiedAmount(2023, "Έσοδο", 10);
        assertEquals(0, updated);
    }
}
