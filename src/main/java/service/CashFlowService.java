package service;

import dao.CashFlowDao;
import dao.CashFlow;

import java.util.List;

public class CashFlowService {

    private final CashFlowDao cashflowDao = new CashFlowDao();
    // Προσθήκη νέου cashflow
    public void addCashflow(CashFlow cashflow) {
        if (cashflow == null) {
            throw new IllegalArgumentException("Cashflow cannot be null");
        }
        if (cashflow.getAmount() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        cashflowDao.addCashFlow(cashflow);
    }

    // Ενημέρωση υπάρχοντος cashflow με έλεγχο ±20%
    public void updateCashflow(CashFlow cashflow) {
        if (cashflow == null) {
            throw new IllegalArgumentException("Cashflow cannot be null");
        }
        if (cashflow.getId() <= 0) {
            throw new IllegalArgumentException("Invalid cashflow ID");
        }
        // Update στο DAO
        cashflowDao.updateCashFlow(cashflow);
    }

    // Διαγραφή cashflow με βάση ID
    public void deleteCashflow(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid cashflow ID");
        }
        cashflowDao.deleteCashFlow(id);
    }

    // Λήψη όλων των cashflows για συγκεκριμένο έτος και τύπο
    public List<CashFlow> getCashflows(int year, String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        return cashflowDao.selectCashFlow(year, type);
    }
}
