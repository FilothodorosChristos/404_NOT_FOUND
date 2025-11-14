package dao;

/**
 * Η κλάση Foreis αναπαριστά μία εγγραφή του πίνακα "foreis".
 * Περιέχει πληροφορίες για το έτος, τον τύπο φορέα, το όνομα,
 * καθώς και τα ποσά του τακτικού προϋπολογισμού, του ΠΔΕ και το συνολικό ποσό.
 */
public class Foreis {

  private int id;
  private int yearId;
  private String type;
  private String name;
  private double regularBudget;
  private double publicInvBudget;
  private double total;

  /**
    * Δημιουργεί ένα νέο αντικείμενο Foreis αρχικοποιώντας όλα τα πεδία.
    */
  public Foreis(int id, int yearId, String type, String name,
                  double regularBudget, double publicInvBudget, double total) {
    this.id = id;
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

  @Override
    public String toString() {
    return "Foreis{" 
               + "id=" + id 
               + ", yearId=" + yearId 
               + ", type='" + type + '\'' 
               + ", name='" + name + '\'' 
               + ", regularBudget=" + regularBudget 
               + ", publicInvBudget=" + publicInvBudget 
               + ", total=" + total 
               + '}';
  }
}

