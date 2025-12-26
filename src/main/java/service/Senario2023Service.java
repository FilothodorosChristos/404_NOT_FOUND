package service;

import dao.CashFlow;
import java.util.ArrayList;
import java.util.List;

public class Senario2023Service {
	 private final CashFlowService cashFlowService = new CashFlowService();

	/**
	 * Επιλέγει όλα τα "Έσοδα" του 2023, μειώνει το ποσό τους κατά 2% και επιστρέφει τη νέα λίστα.
	 * Δεν αλλάζει τη βάση δεδομένων.
	 *
	 * @return λίστα CashFlow με μειωμένα ποσά
	 */
	public List<CashFlow> getDicreasedEsoda2023() {
		List<CashFlow> original = cashFlowService.getCashflows(2023, "Έσοδο");
		List<CashFlow> dicreased = new ArrayList<>();
		for (CashFlow cf : original) {
			// Δημιουργούμε νέο αντικείμενο CashFlow με μειωμένο ποσό
			CashFlow decreasedCf = new CashFlow(
				cf.getId(),
				cf.getYearId(),
				cf.getType(),
				cf.getName(),
				cf.getAmount() * 0.98
			);
			dicreased.add(decreasedCf);
		}
		return dicreased;
	}
	/**
	 * Επιλέγει όλα τα "Έξοδα" του 2023, αυξάνει το ποσό τους κατά 3% και επιστρέφει τη νέα λίστα.
	 * Δεν αλλάζει τη βάση δεδομένων.
	 *
	 * @return λίστα CashFlow με αυξημένα ποσά
	 */
	public List<CashFlow> getIncreasedExoda2023() {
		List<CashFlow> original = cashFlowService.getCashflows(2023, "Έξοδο");
		List<CashFlow> increased = new ArrayList<>();
		for (CashFlow cf : original) {
			// Δημιουργούμε νέο αντικείμενο CashFlow με αυξημένο ποσό
			CashFlow increasedCf = new CashFlow(
				cf.getId(),
				cf.getYearId(),
				cf.getType(),
				cf.getName(),
				cf.getAmount() * 1.03
			);
			increased.add(increasedCf);
		}
		return increased;
	}
}
