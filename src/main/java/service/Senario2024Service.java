package service;
import dao.Foreis;
import java.util.ArrayList;
import java.util.List;

public class Senario2024Service {
    private final ForeisService foreisService = new ForeisService();
    /**
     * Επιλέγει όλα τα "Υπουργεία" του 2024, αυξάνει τις δημόσιες επενδύσεις τους κατά 4% και επιστρέφει τη νέα λίστα, άρα και 
     * Δεν αλλάζει τη βάση δεδομένων.
     *
     * @return λίστα Foreis με αυξημένες δημόσιες επενδύσεις
     */
    public List<Foreis> getIncreasedPublicInvBudget2024() {
        List<Foreis> original = foreisService.getForeisByYearAndType(2024, "Υπουργείο");
        List<Foreis> increased = new ArrayList<>();
        for (Foreis f : original) {
            // Δημιουργούμε νέο αντικείμενο Foreis με αυξημένες δημόσιες επενδύσεις
            Foreis increasedF = new Foreis(
                f.getId(),
                f.getForeasId(),
                f.getYearId(),
                f.getType(),
                f.getName(),
                f.getRegularBudget(),
                f.getPublicInvBudget() * 1.04,
                f.getTotal() - f.getPublicInvBudget() + (f.getPublicInvBudget() * 1.04)
            );
            increased.add(increasedF);
        }
        return increased;
    }
}
