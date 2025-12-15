package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DatabaseSetup;

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
    
    public List<Log> selectLog() {
    List<Log> log = new ArrayList<>();
    final String SQL = "SELECT * FROM log";  //where id>168 
    try (Connection connection = DatabaseSetup.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL)) {

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
