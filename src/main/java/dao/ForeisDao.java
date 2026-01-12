package dao;

import database.DatabaseSetup;
import dto.ForeasCompareDto;
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
    final String sql = "SELECT * FROM foreis WHERE year_id = ? AND type = ? "
                     + "ORDER BY foreas_id ASC, type ASC, name ASC";

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
  /**
   * Αναζητά και επιστρέφει ένα αντικείμενο Foreis από τη βάση δεδομένων
   * με βάση το μοναδικό αναγνωριστικό (ID).
   *
   * @param id το μοναδικό αναγνωριστικό της εγγραφής foreis
   * @return το αντικείμενο Foreis αν βρεθεί, αλλιώς null
   * @throws RuntimeException αν προκύψει σφάλμα κατά την επικοινωνία με τη βάση
   */

  public Foreis selectForeisById(int id) {
    final String sql = "SELECT * FROM foreis WHERE id = ?";
    try (Connection connection = DatabaseSetup.getConnection();
          PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, id);

      try (ResultSet rs = statement.executeQuery()) {
        if (rs.next()) {
          return mapRow(rs);
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (selectForeisById): " + e.getMessage(), e);
    }
    return null;
  }

  /**
   *  Συγκρίνει τα Foreis μεταξύ δύο ετών και επιστρέφει μια λίστα DTO
   *  με τα αποτελέσματα της σύγκρισης.
   * * @param year1 πρώτο έτος
   * * @param year2 δεύτερο έτος
   * 
   * @return λίστα DTO με τα αποτελέσματα σύγκρισης
   * @throws SQLException αν προκύψει σφάλμα κατά την επικοινωνία με τη βάση
   */
  public List<ForeasCompareDto> compareYears(int year1, int year2) throws SQLException {
    List<ForeasCompareDto> results = new ArrayList<>();

    String sql = """
        SELECT
          f1.foreas_id,
          f1.name,

          -- regular budget
          f1.regular_budget AS regular_year1,
          f2.regular_budget AS regular_year2,
          (f2.regular_budget - f1.regular_budget) AS regular_diff,
          CASE
            WHEN f1.regular_budget = 0 THEN NULL
            ELSE ((f2.regular_budget - f1.regular_budget) / f1.regular_budget) * 100
          END AS regular_percent_change,

          -- public investment budget
          f1.public_inv_budget AS public_inv_year1,
          f2.public_inv_budget AS public_inv_year2,
          (f2.public_inv_budget - f1.public_inv_budget) AS public_inv_diff,
          CASE
            WHEN f1.public_inv_budget = 0 THEN NULL
            ELSE ((f2.public_inv_budget - f1.public_inv_budget) / f1.public_inv_budget) * 100
          END AS public_inv_percent_change,

          -- total
          f1.total AS total_year1,
          f2.total AS total_year2,
          (f2.total - f1.total) AS total_diff,
          CASE
            WHEN f1.total = 0 THEN NULL
            ELSE ((f2.total - f1.total) / f1.total) * 100
          END AS total_percent_change

        FROM foreis f1
        JOIN foreis f2
          ON f1.foreas_id = f2.foreas_id
        WHERE f1.year_id = ?
          AND f2.year_id = ?
        ORDER BY f1.name;
          """;

    try (Connection conn = DatabaseSetup.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, year1);
      ps.setInt(2, year2);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        ForeasCompareDto dto = new ForeasCompareDto();

        dto.setForeasId(rs.getInt("foreas_id"));
        dto.setName(rs.getString("name"));

        // regular
        dto.setRegularYear1(rs.getDouble("regular_year1"));
        dto.setRegularYear2(rs.getDouble("regular_year2"));
        dto.setRegularDiff(rs.getDouble("regular_diff"));
        dto.setRegularPercentChange((Double) rs.getObject("regular_percent_change"));

        // public investment
        dto.setPublicInvYear1(rs.getDouble("public_inv_year1"));
        dto.setPublicInvYear2(rs.getDouble("public_inv_year2"));
        dto.setPublicInvDiff(rs.getDouble("public_inv_diff"));
        dto.setPublicInvPercentChange((Double) rs.getObject("public_inv_percent_change"));

        // total
        dto.setTotalYear1(rs.getDouble("total_year1"));
        dto.setTotalYear2(rs.getDouble("total_year2"));
        dto.setTotalDiff(rs.getDouble("total_diff"));
        dto.setTotalPercentChange((Double) rs.getObject("total_percent_change"));

        results.add(dto);
      }
    }

    return results;
  }
}




