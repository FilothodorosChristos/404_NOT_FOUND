/**
 * Η κλάση Cashflow αναπαριστά μία εγγραφή του πίνακα "cashflow".
 * Περιέχει τα πεδία id, yearId, name, type και amount,
 * καθώς και τους απαραίτητους constructors και μεθόδους πρόσβασης.
 */
public class CashFlow {

  private int id;
  private int yearId;
  private String name;
  private String type;
  private double amount;

  /**
   * Δημιουργεί ένα νέο αντικείμενο Cashflow αρχικοποιώντας όλα τα πεδία του.
   */
  public Cashflow(int id, int yearId, String name, String type, double amount) {
    this.id = id;
    this.yearId = yearId;
    this.name = name;
    this.type = type;
    this.amount = amount;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }
}


