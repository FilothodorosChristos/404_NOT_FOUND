package service;

import dao.CashFlow;
import dao.CashFlowDao;
import dto.CashFlowCompareDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
   * @param year το έτος (2021–2026)
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

  /**
   * Συγκρίνει amounts για ίδια έσοδα ή έξοδα μεταξύ δύο ετών.
   * 
   * @param year1 πρώτο έτος
   * @param year2 δεύτερο έτος
   * @param type τύπος cashflow ("income" ή "expense")
   * @return λίστα DTO με αποτελέσματα σύγκρισης
   */
  public List<CashFlowCompareDto> compareCashFlows(int year1, int year2, String type) {
    List<CashFlow> listYear1 = cashflowDao.selectCashFlow(year1, type);
    List<CashFlow> listYear2 = cashflowDao.selectCashFlow(year2, type);

    Map<String, Double> mapYear1 = new HashMap<>();
    for (CashFlow cf : listYear1) {
      mapYear1.put(cf.getName(), cf.getAmount());
    }

    Map<String, Double> mapYear2 = new HashMap<>();
    for (CashFlow cf : listYear2) {
      mapYear2.put(cf.getName(), cf.getAmount());
    }

    // Συγκεντρώνουμε όλα τα ονόματα
    Map<String, CashFlowCompareDto> comparisonMap = new HashMap<>();

    for (Map.Entry<String, Double> entry : mapYear1.entrySet()) {
      String name = entry.getKey();
      double amountYear1 = entry.getValue();

      if (mapYear2.containsKey(name)) {
        double amountYear2 = mapYear2.get(name);
        comparisonMap.put(
              name,
              new CashFlowCompareDto(name, amountYear1, amountYear2, false, false)
        );
      } else {
        comparisonMap.put(
            name,
          new CashFlowCompareDto(name, amountYear1, 0, false, true)
        );
      }
    }

    for (Map.Entry<String, Double> entry : mapYear2.entrySet()) {
      String name = entry.getKey();
      double amountYear2 = entry.getValue();

      if (!comparisonMap.containsKey(name)) {
        comparisonMap.put(
            name,
            new CashFlowCompareDto(name, 0, amountYear2, true, false)
        );
      }
    }

    return new ArrayList<>(comparisonMap.values());
  }
}
