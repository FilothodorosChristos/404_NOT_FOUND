package dao;

/**
 * Η κλάση Cashflow αναπαριστά μία εγγραφή του πίνακα "cashflow".
 * Περιέχει τα πεδία id, yearId, type, name, και amount,
 * καθώς και τους απαραίτητους constructors και μεθόδους πρόσβασης.
 */

public class CashFlow {

  private int id;
  private int yearId;
  private String type;
  private String name;
  private double amount;

  /**
   * Δημιουργεί ένα νέο αντικείμενο Cashflow αρχικοποιώντας όλα τα πεδία του.
   */
  public CashFlow(int id, int yearId, String type, String name, double amount) {
    this.id = id;
    this.yearId = yearId;
    this.type = type;
    this.name = name;
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

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }
  /**
   * Επιστρέφει μια αναγνώσιμη αναπαράσταση του αντικειμένου CashFlow,
   * εμφανίζοντας όλες τις τιμές των πεδίων του.
   */

  @Override
   public String toString() {
    return "CashFlow{" 
             + "id=" + id 
             + ", yearId=" + yearId 
             + ", type='" + type + '\''
             + ", name='" + name + '\'' 
             + ", amount=" + amount 
             + '}';
  }

}


