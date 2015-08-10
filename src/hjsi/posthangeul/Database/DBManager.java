package hjsi.posthangeul.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

public class DBManager {
  private Connection connection;
  private String dbFileName;
  private boolean isOpened = false;

  static {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public DBManager(String databaseFileName) {
    this.dbFileName = databaseFileName;
  }

  public boolean open() {
    if (isOpened == true) {
      isOpened = true;
      SQLiteConfig config = new SQLiteConfig();
      config.setReadOnly(false);
      try {
        this.connection =
            DriverManager.getConnection("jdbc:sqlite:/" + this.dbFileName, config.toProperties());
      } catch (SQLException e) {
        e.printStackTrace();
        isOpened = false;
      }
    }
    return isOpened;
  }

  public boolean close() {
    if (isOpened == true) {
      isOpened = false;
      try {
        this.connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
        isOpened = true;
      }
    }
    return !isOpened;
  }

  public boolean readWord(String filePath) throws SQLException {
    if (this.isOpened == false) {
      return false;
    }

    boolean result = false;
    String query = "SELECT * FROM media WHERE FilePath=? AND CheckSum=?;";
    PreparedStatement prep = this.connection.prepareStatement(query);
    prep.setString(1, filePath);

    ResultSet row = prep.executeQuery();
    if (row.next()) {
      row.getString(1); // index 로 가져오기
      row.getString("FileSize"); // field 이름으로 가져오기

      result = true;
    }
    row.close();
    prep.close();
    return result;
  }
}
