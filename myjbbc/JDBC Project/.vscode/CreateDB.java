import java.sql.*;

public class CreateDB {
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "Chitra@123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            if (conn != null) {
                System.out.println("Connection has been established");
                createDatabase(conn);
                conn.setCatalog("My_db");
                createTable(conn);
                batchInsertData(conn);
                readData(conn);
                searchData(conn, "Female");
                sortData(conn, "gender");
                aggregateData(conn);
                performTransaction(conn);
                conn.close();
            }
        } catch (SQLException | ClassNotFoundException excp) {
            System.out.println("Connection error");
            excp.printStackTrace();
        }
    }

    private static void createDatabase(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE DATABASE IF NOT EXISTS My_db";
        stmt.executeUpdate(sql);
        System.out.println("Database 'My_db' created successfully");
        stmt.close();
    }

    private static void createTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS Students (" +
                     "roll_number INT PRIMARY KEY," +
                     "gender VARCHAR(10)" +
                     ")";
        stmt.executeUpdate(sql);
        System.out.println("Table 'Students' created successfully");
        stmt.close();
    }

    private static void batchInsertData(Connection conn) throws SQLException {
        String sql = "INSERT INTO Students (roll_number, gender) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 1; i <= 5; i++) {
            pstmt.setInt(1, i);
            pstmt.setString(2, i % 2 == 0 ? "Male" : "Female");
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        System.out.println("Batch data inserted successfully");
        pstmt.close();
    }

    private static void readData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM Students";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int rollNumber = rs.getInt("roll_number");
            String gender = rs.getString("gender");
            System.out.println("Roll Number: " + rollNumber + ", Gender: " + gender);
        }
        rs.close();
        stmt.close();
    }

    private static void searchData(Connection conn, String gender) throws SQLException {
        String sql = "SELECT * FROM Students WHERE gender = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, gender);
        ResultSet rs = pstmt.executeQuery();
        System.out.println("Search results for gender = " + gender + ":");
        while (rs.next()) {
            int rollNumber = rs.getInt("roll_number");
            System.out.println("Roll Number: " + rollNumber);
        }
        rs.close();
        pstmt.close();
    }

    private static void sortData(Connection conn, String columnName) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM Students ORDER BY " + columnName;
        ResultSet rs = stmt.executeQuery(sql);
        System.out.println("Sorted data by " + columnName + ":");
        while (rs.next()) {
            int rollNumber = rs.getInt("roll_number");
            String gender = rs.getString("gender");
            System.out.println("Roll Number: " + rollNumber + ", Gender: " + gender);
        }
        rs.close();
        stmt.close();
    }

    private static void aggregateData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT COUNT(*), gender FROM Students GROUP BY gender";
        ResultSet rs = stmt.executeQuery(sql);
        System.out.println("Aggregate data:");
        while (rs.next()) {
            int count = rs.getInt(1);
            String gender = rs.getString("gender");
            System.out.println("Gender: " + gender + ", Count: " + count);
        }
        rs.close();
        stmt.close();
    }

    private static void performTransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        try {
            String sql1 = "UPDATE Students SET gender = 'Unknown' WHERE roll_number = 1";
            String sql2 = "DELETE FROM Students WHERE roll_number = 2";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            conn.commit();
            System.out.println("Transaction completed successfully");
            stmt.close();
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Transaction failed, rolled back");
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
