package dao;

/**
 * Η κλάση Foreis αναπαριστά μια εγγραφή του πίνακα "foreis".
 * Περιλαμβάνει τα πεδία id, foreasId, yearId, type, name,
 * regularBudget, publicInvBudget και total.
 */
public class Foreis {

  private int id;                
  private int foreasId;          
  private int yearId;
  private String type;
  private String name;
  private double regularBudget;
  private double publicInvBudget;
  private double total;

  /**
   * Δημιουργεί ένα αντικείμενο Foreis αρχικοποιώντας όλα τα πεδία του.
   */
  public Foreis(
      int id,
      int foreasId,
      int yearId,
      String type,
      String name,
      double regularBudget,
      double publicInvBudget,
      double total
  ) {
    this.id = id;
    this.foreasId = foreasId;
    this.yearId = yearId;
    this.type = type;
    this.name = name;
    this.regularBudget = regularBudget;
    this.publicInvBudget = publicInvBudget;
    this.total = total;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
   * Επιστρέφει αναπαράσταση τύπου String του αντικειμένου Foreis.
   */
  @Override
  public String toString() {
    return "Foreis{"
        + "id=" + id
        + ", foreasId=" + foreasId
        + ", yearId=" + yearId
        + ", type='" + type + '\''
        + ", name='" + name + '\''
        + ", regularBudget=" + regularBudget
        + ", publicInvBudget=" + publicInvBudget
        + ", total=" + total
        + '}';
  }
}




