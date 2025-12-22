package util;

import dao.CashFlow;
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
   * Έλεγχος έτους (μόνο 2021–2026).
   */

  public static void validateYear(int year) {
    if (year < 2021 || year > 2026) {
      throw new IllegalArgumentException("Επιτρέπονται μόνο έτη 2021–2026");
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
}
 