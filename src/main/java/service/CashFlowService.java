package service;

import dao.CashFlow;
import dao.CashFlowDao;
import java.util.List;
import util.ValidationUtils;

/**
 * Service layer για την οντότητα {@link CashFlow}.
 * Περιέχει επιχειρησιακή λογική και validation κανόνες
 * πριν την επικοινωνία με το {@link CashFlowDao}.
 */
public class CashFlowService {

  private final CashFlowDao cashflowDao = new CashFlowDao();
  /**
   * Προσθέτει νέο cashflow στη βάση δεδομένων.
   * Ελέγχει ότι το ποσό δεν είναι αρνητικό και ότι το έτος είναι έγκυρο.
   *
   * @param cashflow το αντικείμενο προς εισαγωγή
   * @throws IllegalArgumentException αν το αντικείμενο είναι null * ή οι κανόνες  αποτύχουν
   */

  public void addCashflow(CashFlow cashflow) {
    if (cashflow == null) {
      throw new IllegalArgumentException("Cashflow cannot be null");
    }
    ValidationUtils.validateNonNegative(cashflow.getAmount());
    ValidationUtils.validateYear(cashflow.getYearId());

    cashflowDao.addCashFlow(cashflow);
    // Έλεγχος συνολικών ποσών μετά την εισαγωγή
    List<CashFlow> allCashflows = 
        cashflowDao.selectCashFlow(cashflow.getYearId(), cashflow.getType());
    ValidationUtils.validateFinalTotals(allCashflows, cashflow.getYearId());
  }

  /**
   * Ενημερώνει υπάρχον cashflow με βάση το ID.
   * Ελέγχει ότι η αλλαγή ποσού δεν ξεπερνάει το ±45%.
   *
   * @param cashflow το αντικείμενο με ενημερωμένα δεδομένα
   * @throws IllegalArgumentException αν κατι απο τα παρακατω δεν ισχυει
   */
  public void updateCashflow(CashFlow cashflow) {
    if (cashflow == null) {
      throw new IllegalArgumentException("Cashflow cannot be null");
    }
    ValidationUtils.validatePositiveId(cashflow.getId(), "Cashflow");

    CashFlow existing = cashflowDao.selectCashFlowById(cashflow.getId());
    if (existing == null) {
      throw new IllegalArgumentException("Cashflow with ID " + cashflow.getId() + " not found");
    }

    ValidationUtils.validateAmountChange(existing.getAmount(), cashflow.getAmount());
    cashflowDao.updateCashFlow(cashflow);
    List<CashFlow> allCashflows =
        cashflowDao.selectCashFlow(cashflow.getYearId(), cashflow.getType());
    ValidationUtils.validateFinalTotals(allCashflows, cashflow.getYearId());
  }

  /**
   * Διαγράφει cashflow με βάση το ID.
   *
   * @param id το μοναδικό αναγνωριστικό
   * @throws IllegalArgumentException αν το ID δεν είναι θετικό
   */

  public void deleteCashflow(int id) {
    ValidationUtils.validatePositiveId(id, "Cashflow");     
    cashflowDao.deleteCashFlow(id);
  }

  /**
   * Επιστρέφει όλα τα cashflows για συγκεκριμένο έτος και τύπο.
   * 
   * @param year το έτος (2023–2025)
   * @param type ο τύπος (π.χ. "Έσοδο", "Έξοδο")
   * @return λίστα με cashflows
   * @throws IllegalArgumentException αν το έτος ή ο τύπος δεν είναι έγκυρα
   */
  public List<CashFlow> getCashflows(int year, String type) {
    ValidationUtils.validateYear(year);
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("Type cannot be null or empty");
    }
    return cashflowDao.selectCashFlow(year, type);
  }
}
