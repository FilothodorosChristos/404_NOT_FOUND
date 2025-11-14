package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση CashFlowDao χειρίζεται την επικοινωνία με τη βάση δεδομένων
 * και την πρόσβαση στον πίνακα "cashflows".
 * Παρέχει λειτουργίες για ανάγνωση, εισαγωγή, ενημέρωση και διαγραφή εγγραφών.
 */
public class CashFlowDao {

  /**
   * Μετατρέπει μια γραμμή αποτελεσμάτων (ResultSet) σε αντικείμενο CashFlow.
   * Κάθε πεδίο του ResultSet αντιστοιχίζεται στα πεδία του αντικειμένου.
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
   * Επιστρέφει όλες τις εγγραφές cashflow που ταιριάζουν
   * με το έτος και τον τύπο που δίνονται ως ορίσματα.
   * Επιστρέφει λίστα αντικειμένων CashFlow.
   *
   * @param year το έτος για φιλτράρισμα
   * @param type ο τύπος cashflow (π.χ. income, expense)
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
   *
   * @param cashflow το αντικείμενο προς εισαγωγή
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
   * Ενημερώνει μια υπάρχουσα εγγραφή cashflow με νέα δεδομένα.
   *
   * @param cashflow το αντικείμενο που περιέχει τα ενημερωμένα πεδία
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
   * Διαγράφει μια εγγραφή cashflow με βάση το ID της.
   *
   * @param id το μοναδικό αναγνωριστικό της εγγραφής
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

