package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση CashFlowDao είναι υπεύθυνη για την επικοινωνία
 * της Java με τη βάση δεδομένων όσον αφορά τον πίνακα "cashflow".
 * Περιέχει μεθόδους για ανάγνωση, εισαγωγή και ενημέρωση εγγραφών.
 */
public class CashFlowDao  {

  /**
   * Δημιουργεί ένα αντικείμενο Cashflow από μία γραμμή αποτελεσμάτων της SQL.
   * Κάθε στήλη της γραμμής αντιστοιχίζεται στα πεδία του αντικειμένου.
   */
  private CashFlow mapRow(ResultSet rs) throws SQLException {
    return new CashFlow(
            rs.getInt("id"),
            rs.getInt("year_id"),
            rs.getString("name"),
            rs.getString("type"),
            rs.getDouble("amount")
        );
  }

  /**
   * Επιστρέφει όλες τις εγγραφές του πίνακα cashflow που ταιριάζουν με τα ορίσματα από τη βάση δεδομένων.
   * Δημιουργεί λίστα αντικειμένων τύπου Cashflow.
   */
  public List<CashFlow> selectCashFlow(int year, String type) {
    List<CashFlow> cashflows = new ArrayList<>();
    String sql = "SELECT * FROM cashflows WHERE year_id = ? AND type = ?";

    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, year);
        statement.setString(2, type);

        try (ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                cashflows.add(mapRow(resultSet));
            }
        }

    } catch (SQLException e) {
        System.err.println("Error found at selectCashFlow");
        e.printStackTrace();
    }

    return cashflows;
}


  /**
   * Εισάγει μια νέα εγγραφή cashflow στη βάση δεδομένων.
   * Χρησιμοποιεί PreparedStatement για ασφάλεια και αποφυγή λαθών.
   */
  public void addCashFlow(CashFlow cashflow) {
    String sql = "INSERT INTO cashflows(year_id, name, type, amount) VALUES(?, ?, ?, ?)";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, cashflow.getYearId());
      preparedStatement.setString(2, cashflow.getName());
      preparedStatement.setString(3, cashflow.getType());
      preparedStatement.setDouble(4, cashflow.getAmount());
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error found at addCashFlow");
      e.printStackTrace();
    }
  }

  /**
   * Ενημερώνει μια υπάρχουσα εγγραφή cashflow στη βάση δεδομένων
   * με τα νέα δεδομένα που περιέχονται στο αντικείμενο cashflow.
   */
  public void updateCashFlow(CashFlow cashflow) {
    String sql = "UPDATE cashflows SET year_id = ?, name = ?, type = ?, amount = ? WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, cashflow.getYearId());
      preparedStatement.setString(2, cashflow.getName());
      preparedStatement.setString(3, cashflow.getType());
      preparedStatement.setDouble(4, cashflow.getAmount());
      preparedStatement.setInt(5, cashflow.getId());
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error found at updateCashFlow");
      e.printStackTrace();
    }
  }
  /**
   * Διαγράφει μια εγγραφή cashflow από τη βάση δεδομένων
   * χρησιμοποιώντας το μοναδικό id της.
   */

  public void deleteCashFlow(int id) {
    String sql = "DELETE FROM cashflows WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error found at deleteCashFlow");
      e.printStackTrace();
    }
  }

}
