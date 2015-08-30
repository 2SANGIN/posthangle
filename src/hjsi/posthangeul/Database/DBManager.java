package hjsi.posthangeul.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

import org.sqlite.SQLiteConfig;

/**
 * DBManager <br>
 * 2015. 8. 29.
 *
 * @author SANGIN
 */
public class DBManager {
   static {
      try {
         Class.forName("org.sqlite.JDBC");
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }

   /**
    * 테이블 이름
    */
   private static final String DB_TABLE_NAME = "WORDS";

   /**
    *
    */
   private Connection connection;

   /**
    * 파일시스템 상의 DB 파일 이름
    */
   private String dbFileName;

   /**
    * DB가 열려있는지 여부
    */
   private boolean isOpened = false;

   /**
    * DB를 관리하는 객체를 생성한다.
    *
    * @param databaseFileName DB관리 클래스가 관리할 DB 파일명
    */
   public DBManager(String databaseFileName) {
      this.dbFileName = databaseFileName;

      if (this.open()) {
         System.out.println(this.dbFileName);
         String sql = "select name from sqlite_master where name = '" + DB_TABLE_NAME + "';";
         Statement statement = null;
         try {
            statement = this.connection.createStatement();
            ResultSet row = statement.executeQuery(sql);
            boolean notExistTable = true;
            while (row.next()) {
               System.out.println("TABLE NAME: " + row.getString(1));
               if (row.getString(1).compareToIgnoreCase(DB_TABLE_NAME) == 0) {
                  notExistTable = false;
                  break;
               }
            }
            row.close();
            if (notExistTable) {
               if (this.createWordTable())
                  System.out.println("테이블 생성 성공!");
               else
                  System.out.println("테이블 생성 실패!");
            } else {
               System.out.println("이미 테이블이 존재합니다.");
            }
         } catch (SQLException e) {
            e.printStackTrace();
         } finally {
            try {
               if (statement != null)
                  statement.close();
            } catch (SQLException e) {
               e.printStackTrace();
            }
            this.close();
         }
      }
   }

   /**
    * 단어, 카운트 정보를 관리하는 TABLE을 생성한다.
    *
    * @return 생성에 성공했으면 true, 아니라면 false
    * @throws SQLException
    */
   public boolean createWordTable() throws SQLException {
      boolean result = false;
      if (this.open()) {
         /* SQL 작성 */
         StringBuffer sql = new StringBuffer();
         sql.append("CREATE TABLE " + DB_TABLE_NAME + " (\n");
         sql.append("word TEXT PRIMARY KEY,\n");
         sql.append("count INT NOT NULL\n");
         sql.append(");");

         /* SQL state 준비 */
         @SuppressWarnings("resource")
         Statement statement = this.connection.createStatement();

         /* 준비된 SQL 실행 */
         statement.execute(sql.toString());

         /* 로그 출력 */
         System.out.println("executed Query: ");
         System.out.println(sql);
         System.out.println("a table \"" + DB_TABLE_NAME + "\" is created.");

         /* 마무리 */
         statement.close();
         this.close();
         result = true;
      }
      return result;
   }

   /**
    * 주어진 단어를 제거한다.
    *
    * @param word 제거할 단어
    * @return 제거에 성공했으면 true, 아니라면 false
    * @throws SQLException
    */
   public boolean deleteWord(String word) throws SQLException {
      boolean result = false;
      if (this.open()) {
         /* SQL 작성 */
         StringBuffer sql = new StringBuffer();
         sql.append("DELETE FROM " + DB_TABLE_NAME + "\n");
         sql.append("WHERE word = '" + word + "';");

         /* SQL state 준비 */
         @SuppressWarnings("resource")
         Statement statement = this.connection.createStatement();

         /* 준비된 SQL 실행 */
         statement.execute(sql.toString());

         /* 로그 출력 */
         System.out.println("executed Query: ");
         System.out.println(sql);
         System.out.println("1 row deleted.");

         /* 마무리 */
         statement.close();
         this.close();
         result = true;
      }
      return result;
   }

   /**
    * 새로운 단어를 삽입한다. 저장되는 카운트는 기본 1이다.
    *
    * @param word 추가할 단어
    * @return 삽입에 성공했으면 true, 아니라면 false
    * @throws SQLException
    */
   public boolean insertWord(String word) throws SQLException {
      boolean result = false;
      if (this.open()) {
         /* SQL 작성 */
         StringBuffer sql = new StringBuffer();
         sql.append("INSERT INTO " + DB_TABLE_NAME + "\n");
         sql.append("(word, count)\n");
         sql.append("VALUES ('" + word + "', 1);");

         /* SQL state 준비 */
         @SuppressWarnings("resource")
         Statement statement = this.connection.createStatement();

         /* 준비된 SQL 실행 */
         statement.execute(sql.toString());

         /* 로그 출력 */
         System.out.println("executed Query: ");
         System.out.println(sql);
         System.out.println("1 row inserted.");

         /* 마무리 */
         statement.close();
         this.close();
         result = true;
      }
      return result;
   }

   /**
    * DB의 모든 단어, 카운트 정보를 메모리로 읽어온다.
    *
    * @return 단어, 카운트 정보가 들어있는 TreeMap 객체, size >= 0, not null
    * @throws SQLException
    */
   @SuppressWarnings("resource")
   public Map<String, Integer> loadAllWords() throws SQLException {
      Map<String, Integer> result = new TreeMap<>();
      if (this.open()) {
         /* SQL 쿼리 작성 */
         String query = "SELECT * FROM " + DB_TABLE_NAME + ";";

         /* SQL state 준비 */
         Statement statement = this.connection.createStatement();
         ResultSet row = statement.executeQuery(query);

         /* 준비된 SQL 쿼리 실행 */
         int selectedRows = 0;
         while (row.next()) {
            String word = row.getString("word");
            Integer count = Integer.valueOf(row.getInt("count"));
            result.put(word, count);
            selectedRows++;
            System.out.println("row[" + row.getRow() + "]: \"" + word + "\", " + count);
         }

         /* 로그 출력 */
         System.out.println("executed Query: ");
         System.out.println(query);
         System.out.println(selectedRows + " row(s) loaded.");

         /* 마무리 */
         row.close();
         statement.close();
         this.close();
      }
      return result;
   }

   /**
    * 주어진 단어의 카운트를 올린다.
    *
    * @param word 갱신할 단어
    * @return 정상적으로 갱신했으면 true, 실패했으면 false
    * @throws SQLException
    */
   public boolean updateCount(String word) throws SQLException {
      boolean result = false;
      if (this.open()) {
         /* SQL 작성 */
         StringBuffer query = new StringBuffer();
         query.append("UPDATE " + DB_TABLE_NAME + "\n");
         query.append("SET count = count + 1\n");
         query.append("WHERE word = '" + word + "';");

         /* SQL state 준비 */
         @SuppressWarnings("resource")
         Statement statement = this.connection.createStatement();

         /* 준비된 SQL 실행 */
         statement.execute(query.toString());

         /* 로그 출력 */
         System.out.println("executed Query: ");
         System.out.println(query);
         System.out.println("1 row updated.");

         /* 마무리 */
         statement.close();
         this.close();
         result = true;
      }
      return result;
   }

   /**
    * 현재 DB가 열린 상태라면 DB를 닫는다.
    *
    * @return 성공적으로 DB를 닫았다면 true, 아니라면 false
    */
   private boolean close() {
      if (this.isOpened == true) {
         this.isOpened = false;
         try {
            this.connection.close();
         } catch (SQLException e) {
            e.printStackTrace();
            this.isOpened = true;
         }
      }
      return !this.isOpened;
   }

   /**
    * DB가 닫혀있는 상태일 경우, DB를 연다.
    *
    * @return 성공적으로 DB를 열었다면 true, 아니라면 false
    */
   private boolean open() {
      if (this.isOpened != true) {
         this.isOpened = true;
         SQLiteConfig config = new SQLiteConfig();
         config.setReadOnly(false);
         try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:/" + this.dbFileName,
                  config.toProperties());
         } catch (SQLException e) {
            e.printStackTrace();
            this.isOpened = false;
         }
      }
      return this.isOpened;
   }
}
