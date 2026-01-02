package service;

import dao.Foreis;
import java.util.List;

public class ScenarioForeisService {
    private final ForeisService foreisService = new ForeisService();

    /**
     * Δημιουργεί λίστα Foreis για το imported έτος και τύπο, με τροποποιημένο budget (PublicInvBudget ή RegularBudget) κατά το imported ποσοστό.
     * Καλεί την updateForeis της ForeisService για να αποθηκεύσει τις αλλαγές στη βάση δεδομένων.
     *
     * @param year το έτος
     * @param type ο τύπος (π.χ. "Υπουργείο")
     * @param category η κατηγορία budget προς τροποποίηση ("PublicInvBudget" ή "RegularBudget")
     * @param percentageFactor ο συντελεστής τροποποίησης (αν δοθεί το ±4, το budget θα τροποποιηθεί κατά ±4% του αρχικού)
     */
    public void updateForeisWithModifiedBudget(int year, String type, String category, double percentageFactor) {
        if (year < 2021 || year > 2026) {
            throw new IllegalArgumentException("Λανθασμένο έτος: " + year + ". Επιτρέπονται μόνο έτη από 2021 έως 2026.");
        }
        if (!("PublicInvBudget".equals(category) || "RegularBudget".equals(category))) {
            throw new IllegalArgumentException("Λανθασμένη κατηγορία: " + category + ". Επιτρέπονται μόνο 'PublicInvBudget' ή 'RegularBudget'");
        }
        if (!("Υπουργείο".equals(type) || "Κεντρική Διοίκηση".equals(type) || "Αποκεντρωμένη Διοίκηση".equals(type))) {
            throw new IllegalArgumentException("Λανθασμένος τύπος: " + type + ". Επιτρέπονται μόνο 'Υπουργείο', 'Κεντρική Διοίκηση' ή 'Αποκεντρωμένη Διοίκηση'");
        }
        List<Foreis> original = foreisService.getForeisByYearAndType(year, type);
        for (Foreis f : original) {
            double newRegularBudget = f.getRegularBudget();
            double newPublicInvBudget = f.getPublicInvBudget();
            if ("PublicInvBudget".equals(category)) {
                newPublicInvBudget = f.getPublicInvBudget() * (percentageFactor/100) + f.getPublicInvBudget();
            } else if ("RegularBudget".equals(category)) {
                newRegularBudget = f.getRegularBudget() * (percentageFactor/100) + f.getRegularBudget();
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
        }
    }
}
