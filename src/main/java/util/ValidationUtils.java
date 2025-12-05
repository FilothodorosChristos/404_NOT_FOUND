package util;

public class ValidationUtils {

  private static final double MAXPERCENTCHANGE = 45.0; // μεγιστη δυνατοτητα αλλαγης ποσου
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
    if (changePercent > MAXPERCENTCHANGE) {
      throw new IllegalArgumentException(
     "Cannot change amount by more than " + MAXPERCENTCHANGE 
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
    * Έλεγχος τύπου cashflow (μόνο income ή expense).
    *
    * @param type τύπος cashflow
    * @throws IllegalArgumentException αν ο τύπος δεν είναι αποδεκτός
    */

  public static void validateCashflowType(String type) {
    if (!"income".equals(type) && !"expense".equals(type)) {
      throw new IllegalArgumentException("Μη έγκυρος τύπος cashflow: " + type);
    }
  }

}
