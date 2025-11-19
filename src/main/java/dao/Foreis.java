package dao;

/**
 * Η κλάση Foreis αναπαριστά μία εγγραφή του πίνακα "foreis".
 * Περιέχει τα στοιχεία του φορέα καθώς και τα οικονομικά του δεδομένα.
 */
public class Foreis {

  /** Το μοναδικό αναγνωριστικό του φορέα (πρωτεύον κλειδί). */
  private int foreasId;

  /** Το έτος στο οποίο αντιστοιχεί η εγγραφή. */
  private int yearId;

  /** Ο τύπος του φορέα. */
  private String type;

  /** Το όνομα του φορέα. */
  private String name;

  /** Το ποσό του τακτικού προϋπολογισμού. */
  private double regularBudget;

  /** Το ποσό του προϋπολογισμού δημοσίων επενδύσεων. */
  private double publicInvBudget;

  /** Το συνολικό ποσό. */
  private double total;

  /**
   * Δημιουργεί ένα νέο αντικείμενο Foreis αρχικοποιώντας όλα τα πεδία.
   */
  public Foreis(int foreasId, int yearId, String type, String name,
                double regularBudget, double publicInvBudget, double total) {
    this.foreasId = foreasId;
    this.yearId = yearId;
    this.type = type;
    this.name = name;
    this.regularBudget = regularBudget;
    this.publicInvBudget = publicInvBudget;
    this.total = total;
  }

  public int getForeasId() {
    return foreasId;
  }

  public void setForeasId(int foreasId) {
    this.foreasId = foreasId;
  }

  public int getYearId() {
    return yearId;
  }

  public void setYearId(int yearId) {
    this.yearId = yearId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getRegularBudget() {
    return regularBudget;
  }

  public void setRegularBudget(double regularBudget) {
    this.regularBudget = regularBudget;
  }

  public double getPublicInvBudget() {
    return publicInvBudget;
  }

  public void setPublicInvBudget(double publicInvBudget) {
    this.publicInvBudget = publicInvBudget;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  /**
   * Επιστρέφει συμβολοσειρά που περιγράφει το αντικείμενο Foreis.
   */
  @Override
  public String toString() {
    return "Foreis{"
        + "foreasId=" + foreasId
        + ", yearId=" + yearId
        + ", type='" + type + '\''
        + ", name='" + name + '\''
        + ", regularBudget=" + regularBudget
        + ", publicInvBudget=" + publicInvBudget
        + ", total=" + total
        + '}';
  }
}



