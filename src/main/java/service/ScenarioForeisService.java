package service;

import dao.Foreis;
import java.util.List;
import util.ValidationUtils;

/**
 * Service κλάση για την εφαρμογή σεναρίων τροποποίησης budgets
 * σε φορείς του δημοσίου.
 * Παρέχει λειτουργικότητα για την ποσοστιαία μεταβολή
 * του RegularBudget ή του PublicInvBudget φορέων,
 * με έλεγχο εγκυρότητας μέσω ValidationUtils.
 */

public class ScenarioForeisService {
  /**
   * Service για ανάκτηση και ενημέρωση δεδομένων φορέων.
   */
  private final ForeisService foreisService = new ForeisService(); 

  /**
   * Δημιουργεί λίστα Foreis για το imported έτος και τύπο, με τροποποιημένο budget 
   * (PublicInvBudget ή RegularBudget) κατά το imported ποσοστό.
   * Καλεί την updateForeis της ForeisService για να αποθηκεύσει τις αλλαγές στη βάση δεδομένων.
   *
   * @param year το έτος
   * @param type ο τύπος (π.χ. "Υπουργείο")
   * @param category η κατηγορία budget προς τροποποίηση ("PublicInvBudget" ή "RegularBudget")
   * @param percentageFactor ο συντελεστής τροποποίησης 
   * @return πλήθος φορέων που ενημερώθηκαν
   */
     
  public int updateForeisWithModifiedBudget(
      int year,
      String type,
      String category,
      double percentageFactor
  ) {

    ValidationUtils.validateYear(year);

    if (!("Υπουργείο".equals(type)
                || "Κεντρική Διοίκηση".equals(type)
                || "Αποκεντρωμένη Διοίκηση".equals(type))) {
      throw new IllegalArgumentException(
                    "Λανθασμένος τύπος: " + type
            );
    }

    if (!("PublicInvBudget".equals(category)
                || "RegularBudget".equals(category))) {
      throw new IllegalArgumentException(
                    "Λανθασμένη κατηγορία: " + category
            );
    }

    if (Double.isNaN(percentageFactor) || Double.isInfinite(percentageFactor)) {
      throw new IllegalArgumentException("Μη έγκυρο ποσοστό μεταβολής");
    }
       
    List<Foreis> original = foreisService.getForeisByYearAndType(year, type);
    int updated = 0;

    for (Foreis f : original) {

      double newRegularBudget = f.getRegularBudget();
      double newPublicInvBudget = f.getPublicInvBudget();

      ValidationUtils.validateNonNegative(newRegularBudget);
      ValidationUtils.validateNonNegative(newPublicInvBudget);

      if ("PublicInvBudget".equals(category)) {
        double oldValue = newPublicInvBudget;
        newPublicInvBudget =
                newPublicInvBudget + newPublicInvBudget * (percentageFactor / 100);

        ValidationUtils.validateNonNegative(newPublicInvBudget);
        ValidationUtils.validateAmountChange(oldValue, newPublicInvBudget);
      }

      if ("RegularBudget".equals(category)) {
        double oldValue = newRegularBudget;
        newRegularBudget =
                newRegularBudget + newRegularBudget * (percentageFactor / 100);

        ValidationUtils.validateNonNegative(newRegularBudget);
        ValidationUtils.validateAmountChange(oldValue, newRegularBudget);
      }

      double newTotal = newRegularBudget + newPublicInvBudget;

      Foreis modifiedF = new Foreis(
            f.getId(),
            f.getForeasId(),
            f.getYearId(),
            f.getType(),
            f.getName(),
            newRegularBudget,
            newPublicInvBudget,
            newTotal
      );
       
      foreisService.updateForeis(modifiedF);
      updated++;
    }
    return updated;
  }
}
