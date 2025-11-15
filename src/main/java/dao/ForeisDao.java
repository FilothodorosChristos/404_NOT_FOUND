package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση ForeisDao είναι υπεύθυνη για την επικοινωνία με τη βάση δεδομένων
 * για τον πίνακα "foreis". Παρέχει τις CRUD λειτουργίες.
 */
public class ForeisDao {

  /**
   * Δημιουργεί ένα αντικείμενο Foreis από μια γραμμή αποτελεσμάτων.
   */
  private Foreis mapRow(ResultSet rs) throws SQLException {
    return new Foreis(
        rs.getInt("id"),
        rs.getInt("year_id"),
        rs.getString("type"),
        rs.getString("name"),
        rs.getDouble("regular_budget"),
        rs.getDouble("public_inv_budget"),
        rs.getDouble("total")
    );
  }

  /**
   * Επιστρέφει όλες τις εγγραφές foreis που ταιριάζουν με τα ορίσματα.
   */
  public List<Foreis> selectForeis(int year, String type) {
    List<Foreis> foreisList = new ArrayList<>();
    String sql = "SELECT * FROM foreis WHERE year_id = ? AND type = ?";

    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, year);
      statement.setString(2, type);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          foreisList.add(mapRow(resultSet));
        }
      }

    } catch (SQLException e) {
      System.err.println("Error found at selectForeis");
      e.printStackTrace();
    }

    return foreisList;
  }

  /**
   * Εισάγει μια νέα εγγραφή foreis στη βάση δεδομένων.
   */
  public void addForeis(Foreis foreis) {
    String sql =
        "INSERT INTO foreis(year_id, type, name, regular_budget, public_inv_budget, total) "
            + "VALUES(?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, foreis.getYearId());
      preparedStatement.setString(2, foreis.getType());
      preparedStatement.setString(3, foreis.getName());
      preparedStatement.setDouble(4, foreis.getRegularBudget());
      preparedStatement.setDouble(5, foreis.getPublicInvBudget());
      preparedStatement.setDouble(6, foreis.getTotal());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error found at addForeis");
      e.printStackTrace();
    }
  }

  /**
   * Ενημερώνει υπάρχουσα εγγραφή foreis στη βάση δεδομένων.
   */
  public void updateForeis(Foreis foreis) {
    String sql =
        "UPDATE foreis SET year_id = ?, type = ?, name = ?, regular_budget = ?, "
            + "public_inv_budget = ?, total = ? WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, foreis.getYearId());
      preparedStatement.setString(2, foreis.getType());
      preparedStatement.setString(3, foreis.getName());
      preparedStatement.setDouble(4, foreis.getRegularBudget());
      preparedStatement.setDouble(5, foreis.getPublicInvBudget());
      preparedStatement.setDouble(6, foreis.getTotal());
      preparedStatement.setInt(7, foreis.getId());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error found at updateForeis");
      e.printStackTrace();
    }
  }

  /**
   * Διαγράφει μια εγγραφή foreis από τη βάση δεδομένων.
   */
  public void deleteForeis(int id) {
    String sql = "DELETE FROM foreis WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      System.err.println("Error found at deleteForeis");
      e.printStackTrace();
    }
  }
}


