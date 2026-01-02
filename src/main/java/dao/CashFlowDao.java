package dao;

import database.DatabaseSetup;
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
                rs.getString("type"),
                rs.getString("name"),
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
    final String sql = "SELECT * FROM cashflows WHERE year_id = ? AND type = ? "
                     + "ORDER BY year_id ASC, type ASC, name ASC;";
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
      throw new RuntimeException("Σφάλμα στη βάση (selectCashFlow): " + e.getMessage(), e);
    }

    return cashflows;
  }

  /**
   * Εισάγει μια νέα εγγραφή cashflow στη βάση δεδομένων.
   *
   * @param cashflow το αντικείμενο προς εισαγωγή
   */
  public void addCashFlow(CashFlow cashflow) {
    final String sql = "INSERT INTO cashflows(year_id, type, name, amount) VALUES(?, ?, ?, ?)";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, cashflow.getYearId());
      preparedStatement.setString(2, cashflow.getType());
      preparedStatement.setString(3, cashflow.getName());
      preparedStatement.setDouble(4, cashflow.getAmount());
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (addCashFlow): " + e.getMessage(), e);
    }
  }

  /**
   * Ενημερώνει μια υπάρχουσα εγγραφή cashflow με νέα δεδομένα.
   *
   * @param cashflow το αντικείμενο που περιέχει τα ενημερωμένα πεδία
   */
  public void updateCashFlow(CashFlow cashflow) {
    final String sql = "UPDATE cashflows SET year_id = ?, "
        + "type = ?, name = ?, amount = ? WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, cashflow.getYearId());
      preparedStatement.setString(2, cashflow.getType());
      preparedStatement.setString(3, cashflow.getName());
      preparedStatement.setDouble(4, cashflow.getAmount());
      preparedStatement.setInt(5, cashflow.getId());
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (updateCashFlow): " + e.getMessage(), e);
    }
  }

  /**
   * Διαγράφει μια εγγραφή cashflow με βάση το ID της.
   *
   * @param id το μοναδικό αναγνωριστικό της εγγραφής
   */
  public void deleteCashFlow(int id) {
    final String sql = "DELETE FROM cashflows WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (deleteCashFlow): " + e.getMessage(), e);
    }
  }
  /**
   * Αναζητά και επιστρέφει ένα αντικείμενο CashFlow από τη βάση δεδομένων
   * με βάση το μοναδικό αναγνωριστικό (ID).
   *
   * @param id το μοναδικό αναγνωριστικό της εγγραφής cashflow
   * @return το αντικείμενο CashFlow αν βρεθεί, αλλιώς null
   * @throws RuntimeException αν προκύψει σφάλμα κατά την επικοινωνία με τη βάση
   */
  
  public CashFlow selectCashFlowById(int id) {
    final String sql = "SELECT * FROM cashflows WHERE id = ?";
    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, id);
      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (selectCashFlowById): " + e.getMessage(), e);
    }
    return null;
  }

}

