package dao;

import database.DatabaseSetup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση ForeisDao χειρίζεται την επικοινωνία με τη βάση δεδομένων
 * και την πρόσβαση στον πίνακα "foreis".
 * Παρέχει λειτουργίες για ανάγνωση, εισαγωγή, ενημέρωση και διαγραφή εγγραφών.
 */
public class ForeisDao {

  /**
   * Μετατρέπει μια γραμμή αποτελεσμάτων (ResultSet) σε αντικείμενο Foreis.
   */
  private Foreis mapRow(ResultSet rs) throws SQLException {
    return new Foreis(
            rs.getInt("id"),
            rs.getInt("foreas_id"),
            rs.getInt("year_id"),
            rs.getString("type"),
            rs.getString("name"),
            rs.getDouble("regular_budget"),
            rs.getDouble("public_inv_budget"),
            rs.getDouble("total")
    );
  }

  /**
   * Επιστρέφει όλες τις εγγραφές foreis που ταιριάζουν
   * με το έτος και τον τύπο που δίνονται ως ορίσματα.
   */
  public List<Foreis> selectForeis(int year, String type) {
    List<Foreis> foreisList = new ArrayList<>();
    final String sql = "SELECT * FROM foreis WHERE year_id = ? AND type = ?";

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
      throw new RuntimeException("Σφάλμα στη βάση (selectForeis): " + e.getMessage(), e);
    }

    return foreisList;
  }

  /**
   * Εισάγει μια νέα εγγραφή foreis στη βάση δεδομένων.
   */
  public void addForeis(Foreis foreis) {
    final String sql =
        "INSERT INTO foreis(" 
        + "year_id, type, name, regular_budget," 
        + "public_inv_budget, total, foreas_id) " 
        + "VALUES(?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, foreis.getYearId());
      preparedStatement.setString(2, foreis.getType());
      preparedStatement.setString(3, foreis.getName());
      preparedStatement.setDouble(4, foreis.getRegularBudget());
      preparedStatement.setDouble(5, foreis.getPublicInvBudget());
      preparedStatement.setDouble(6, foreis.getTotal());
      preparedStatement.setInt(7, foreis.getForeasId());
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (addForeis): " + e.getMessage(), e);
    }
  }

  /**
   * Ενημερώνει μια υπάρχουσα εγγραφή foreis με νέα δεδομένα.
   */
  public void updateForeis(Foreis foreis) {
    final String sql =
        "UPDATE foreis SET year_id = ?, type = ?, name = ?, regular_budget = ?, "
        + "public_inv_budget = ?, total = ?, foreas_id = ? WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, foreis.getYearId());
      preparedStatement.setString(2, foreis.getType());
      preparedStatement.setString(3, foreis.getName());
      preparedStatement.setDouble(4, foreis.getRegularBudget());
      preparedStatement.setDouble(5, foreis.getPublicInvBudget());
      preparedStatement.setDouble(6, foreis.getTotal());
      preparedStatement.setInt(7, foreis.getForeasId());
      preparedStatement.setInt(8, foreis.getId());
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (updateForeis): " + e.getMessage(), e);
    }
  }

  /**
   * Διαγράφει μια εγγραφή foreis με βάση το ID της.
   */
  public void deleteForeis(int id) {
    final String sql = "DELETE FROM foreis WHERE id = ?";

    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (deleteForeis): " + e.getMessage(), e);
    }
  }
}



