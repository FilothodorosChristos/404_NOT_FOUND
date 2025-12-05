package util;

import dao.CashFlow;
import dao.CashFlowType;
import java.util.List;

public class ValidationUtils {
  private ValidationUtils() {}
  
  private static final double MAX_PERCENT_CHANGE = 45.0; // μεγιστη δυνατοτητα αλλαγης ποσου
  /**
   * Ελέγχει ότι η αλλαγή ενός αριθμητικού πεδίου δεν ξεπερνάει το ποσοστό που ορίζεται.
   *
   * @param oldAmount παλιό ποσό
   * @param newAmount νέο ποσό
   * @throws IllegalArgumentException αν η αλλαγή ξεπερνάει το όριο
   */

  public static void validateAmountChange(double oldAmount, double newAmount) {
    if (oldAmount == 0) { 
      return; // αποφυγη διαιρεσης με το μηδεν
    } 
    double changePercent = Math.abs(newAmount - oldAmount) / oldAmount * 100;
    if (changePercent > MAX_PERCENT_CHANGE) {
      throw new IllegalArgumentException(
     "Cannot change amount by more than " + MAX_PERCENT_CHANGE 
      + "% (old: " + oldAmount + ", new: " + newAmount + ")"
            );
    }
  }
  /**
   * Έλεγχος έτους (μόνο 2023–2025).
   */

  public static void validateYear(int year) {
    if (year < 2023 || year > 2025) {
      throw new IllegalArgumentException("Επιτρέπονται μόνο έτη 2023–2025");
    }
  }
  /**
   * Έλεγχος μη αρνητικών ποσών.
   *
   * @param amount ποσό προς έλεγχο
   * @throws IllegalArgumentException αν το ποσό είναι αρνητικό
   */

  public static void validateNonNegative(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Το ποσό δεν μπορεί να είναι αρνητικό: " + amount);
    }
  }
  /**
   *  
   * @param id να μην ειναι αρνητικο
   * @throws IllegalArgumentException αν ειναι αρνητικο
   */

  public static void validatePositiveId(int id, String fieldName) {
    if (id <= 0) {
      throw new IllegalArgumentException(fieldName + " πρέπει να είναι θετικό (id: " + id + ")");
    }
  }
  /**
   * Δισδιάστατος πίνακας που περιέχει τα συνολικά ποσά του κρατικού προϋπολογισμού.
   * Κάθε γραμμή αντιστοιχεί σε ένα έτος (2023–2025).
   * Κάθε στήλη αντιστοιχεί σε τύπο cashflow:
   *   - Στήλη 0 = income (έσοδα)
   *   - Στήλη 1 = expense (έξοδα)
   */

  private static final double[][] BUDGET_TOTALS = {
      {798_039_000_000.0, 806_878_193_000.0},   // 2023
      {1_107_649_000_000.0, 1_108_188_270_000.0}, // 2024
      {1_304_827_000_000.0, 1_307_907_506_000.0}  // 2025
  };

  /**
   * Ελέγχει ότι μετά τις αλλαγές τα συνολικά έσοδα και έξοδα ενός έτους
   * ταιριάζουν με τα αντίστοιχα ποσά του κρατικού προϋπολογισμού.
   * Η μέθοδος:
   * <ul>
   *   <li>Υπολογίζει ξεχωριστά το άθροισμα των εσόδων (income).</li>
   *   <li>Υπολογίζει ξεχωριστά το άθροισμα των εξόδων (expense).</li>
   *   <li>Συγκρίνει κάθε άθροισμα με το αντίστοιχο ποσό από τον πίνακα 
   * {@code BUDGET_TOTALS}.</li>
   *   <li>Αν κάποιο άθροισμα δεν ταιριάζει, πετάει {@link IllegalArgumentException}.</li>
   * </ul>
   *
   * @param cashflows λίστα με όλα τα CashFlow (income + expense) για το συγκεκριμένο έτος
   * @param year το έτος (2023–2025)
   * @throws IllegalArgumentException αν τα αθροίσματα δεν ταιριάζουν με τον κρατικό προϋπολογισμό
   */

  public static void validateFinalTotals(List<CashFlow> cashflows, int year) {
    // Υπολογισμός αθροισμάτων ξεχωριστά
    double incomeSum = cashflows.stream()
                                    .filter(cf -> CashFlowType.INCOME == cf.getType())
                                    .mapToDouble(CashFlow::getAmount)
                                    .sum();

    double expenseSum = cashflows.stream()
                                     .filter(cf -> CashFlowType.EXPENSE == cf.getType())
                                     .mapToDouble(CashFlow::getAmount)
                                     .sum();

    // Εύρεση αναμενόμενων ποσών από τον πίνακα
    validateYear(year);
    int yearIndex = year - 2023; // 2023->0, 2024->1, 2025->2
    double expectedIncome = BUDGET_TOTALS[yearIndex][0];
    double expectedExpense = BUDGET_TOTALS[yearIndex][1];
    // Έλεγχος εσόδων
    if (Math.abs(incomeSum - expectedIncome) > 0.0001) {
      throw new IllegalArgumentException(
                "Τα έσοδα για το έτος " + year 
               + " (" + incomeSum + ") δεν ταιριάζουν με τον κρατικό προϋπολογισμό (" 
               + expectedIncome + ")"
            );
    }

    // Έλεγχος εξόδων
    if (Math.abs(expenseSum - expectedExpense) > 0.0001) {
      throw new IllegalArgumentException(
                "Τα έξοδα για το έτος " + year 
                + " (" + expenseSum + ") δεν ταιριάζουν με τον κρατικό προϋπολογισμό (" 
                + expectedExpense + ")"
            );
    }
  }
}
