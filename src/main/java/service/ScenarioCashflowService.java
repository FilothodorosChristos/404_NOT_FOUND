package service;

import dao.CashFlow;
import java.util.List;

/**
 * Service κλάση για την εφαρμογή σεναρίων τροποποίησης ποσών
 * σε εγγραφές cashflow.
 * Παρέχει λειτουργικότητα για την ποσοστιαία μεταβολή
 * του Amount εγγραφών CashFlow,
 * με έλεγχο εγκυρότητας μέσω ValidationUtils.
 */
public class ScenarioCashflowService {
  private final CashFlowService cashFlowService = new CashFlowService();

  /**
   * Δημιουργεί λίστα CashFlow για το δοσμένο έτος και τύπο, 
   * με το ποσό τροποποιημένο κατά το δοσμένο ποσοστό.
   * Καλεί την updateCashflow της CashFlowService για να αποθηκεύσει τις αλλαγές στη βάση δεδομένων.
   *
   * @param year το έτος
   * @param type ο τύπος ("Έσοδο" ή "Έξοδο")
   * @param percentageOfChange ο συντελεστής τροποποίησης 
   *        (αν δοθεί το ±4, θα γίνει τροποποίηση κατά ±4% του αρχικού)
   * @return πλήθος εγγραφών που ενημερώθηκαν.
   *         Γίνεται έλεγχος εγκυρότητας παραμέτρων
   */

  public int updateCashflowWithModifiedAmount(int year, String type, double percentageOfChange) {
    if (!("Έσοδο".equals(type) || "Έξοδο".equals(type))) {
      throw new IllegalArgumentException(
                "Λανθασμένος τύπος: " + type + ". Επιτρέπονται μόνο 'Έσοδο' ή 'Έξοδο'"
            );
    }

    // Validation έτους
    if (year < 2021 || year > 2026) {
      throw new IllegalArgumentException(
                "Λανθασμένο έτος: " + year + ". Επιτρέπονται μόνο έτη από 2021 έως 2026."
            );
    }

    if (Double.isNaN(percentageOfChange) || Double.isInfinite(percentageOfChange)) {
      throw new IllegalArgumentException(
                "Μη έγκυρο ποσοστό μεταβολής: " + percentageOfChange
            );
    }

    List<CashFlow> original = cashFlowService.getCashflows(year, type);
    int updatedCount = 0;

    for (CashFlow cf : original) {
      double newAmount =
                cf.getAmount() + (cf.getAmount() * percentageOfChange / 100);
      CashFlow modifiedCf = new CashFlow(
          cf.getId(),
          cf.getYearId(),
          cf.getType(),
          cf.getName(),
          newAmount
      );
      cashFlowService.updateCashflow(modifiedCf);
      updatedCount++;
    }
    return updatedCount;
  }
}
