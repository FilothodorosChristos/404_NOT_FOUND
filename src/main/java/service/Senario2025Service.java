package service;

import dao.CashFlow;
import java.util.ArrayList;
import java.util.List;

public class Senario2025Service {
	private final CashFlowService cashFlowService = new CashFlowService();

	/**
	 * Επιλέγει όλα τα "Έσοδα" του 2025, αυξάνει το ποσό τους κατά 5% και επιστρέφει τη νέα λίστα.
	 * Δεν αλλάζει τη βάση δεδομένων.
	 *
	 * @return λίστα CashFlow με αυξημένα ποσά
	 */
	public List<CashFlow> getIncreasedEsoda2025() {
		List<CashFlow> original = cashFlowService.getCashflows(2025, "Έσοδο");
		List<CashFlow> increased = new ArrayList<>();
		for (CashFlow cf : original) {
			// Δημιουργούμε νέο αντικείμενο CashFlow με αυξημένο ποσό
			CashFlow increasedCf = new CashFlow(
				cf.getId(),
				cf.getYearId(),
				cf.getType(),
				cf.getName(),
				cf.getAmount() * 1.05
			);
			increased.add(increasedCf);
		}
		return increased;
	}
       
    
}
