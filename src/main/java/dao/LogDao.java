package dao;

import database.DatabaseSetup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση LogDao χειρίζεται την επικοινωνία με τη βάση δεδομένων
 * και την πρόσβαση στον πίνακα "log".
 * Παρέχει λειτουργίες για ανάγνωση εγγραφών.
 */
public class LogDao {

  private Log mapRow(ResultSet rs) throws SQLException {
    return new Log(
                rs.getInt("id"),
                rs.getString("table_name"),
                rs.getString("operation"),
                rs.getInt("row_id"),
                rs.getString("old_data"),
                rs.getString("new_data"),
                rs.getString("timestamp")
    );
  }

  /**
   * Επιστρέφει όλες τις εγγραφές log με id μεγαλύτερο ή ίσο από το δοσμένο index.
   * 
   * @param index το ελάχιστο id για τις εγγραφές log
   */
    
  public List<Log> selectLog(int index) {
    List<Log> log = new ArrayList<>();

    final String sql = "SELECT * FROM log WHERE id >= ? ORDER BY id";

    try (Connection connection = DatabaseSetup.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setInt(1, index);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          log.add(mapRow(resultSet));
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Σφάλμα στη βάση (selectLog): " + e.getMessage(), e);
    }

    return log;
  }
}