package service;

import dao.CashFlow;
import java.util.ArrayList;
import java.util.List;

public class ScenarioCashflowService {
	private final CashFlowService cashFlowService = new CashFlowService();

	/**
	 * Επιστρέφει λίστα CashFlow για το δοσμένο έτος και τύπο, με το ποσό τροποποιημένο κατά το δοσμένο ποσοστό.
	 * Δεν αλλάζει τη βάση δεδομένων.
	 *
	 * @param year το έτος
	 * @param type ο τύπος ("Έσοδο" ή "Έξοδο")
	 * @param percentageOfChange ο συντελεστής τροποποίησης (αν δοθεί το 4, το ποσό θα γίνει 4% του αρχικού)
	 * Γίνεται έλεγχος εγκυρότητας παραμέτρων
	 * @return λίστα CashFlow με τροποποιημένα ποσά
	 */
	public List<CashFlow> getModifiedCashFlows(int year, String type, double percentageOfChange) {
		if (!("Έσοδο".equals(type) || "Έξοδο".equals(type))) {
			throw new IllegalArgumentException("Λαναθασμένος τύπος: " + type + ". Επιτρέπονται μόνο 'Έσοδο' ή 'Έξοδο'");
		}
		if (year < 2021 || year > 2026) {
			throw new IllegalArgumentException("Λαναθασμένο έτος: " + year + ". Επιτρέπονται μόνο έτη από 2021 έως 2026.");
		}
		List<CashFlow> original = cashFlowService.getCashflows(year, type);
		List<CashFlow> modified = new ArrayList<>();
		for (CashFlow cf : original) {
			CashFlow modifiedCf = new CashFlow(
				cf.getId(),
				cf.getYearId(),
				cf.getType(),
				cf.getName(),
				cf.getAmount() * (percentageOfChange/100) + cf.getAmount()
			);
			modified.add(modifiedCf);
		}
		return modified;
	}
}
