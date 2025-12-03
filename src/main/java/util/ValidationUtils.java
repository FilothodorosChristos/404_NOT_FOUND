package util;

public class ValidationUtils {

    /**
     * Ελέγχει ότι η αλλαγή ενός αριθμητικού πεδίου δεν ξεπερνάει το ποσοστό που ορίζεται.
     *
     * @param oldAmount παλιό ποσό
     * @param newAmount νέο ποσό
     * @param maxPercent μέγιστο επιτρεπόμενο ποσοστό αλλαγής (π.χ. 20)
     * @throws IllegalArgumentException αν η αλλαγή ξεπερνάει το όριο
     */
    public static void validateAmountChange(double oldAmount, double newAmount, double maxPercent) {
        if (oldAmount == 0) return; // αποφυγή διαίρεσης με μηδέν
        double changePercent = Math.abs(newAmount - oldAmount) / oldAmount * 100;
        if (changePercent > maxPercent) {
            throw new IllegalArgumentException(
                "Cannot change amount by more than " + maxPercent + "% (old: " + oldAmount + ", new: " + newAmount + ")"
            );
        }
    }
}
