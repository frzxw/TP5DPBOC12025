import java.sql.*;

public class Database {
    private final Connection connection;

    // constructor untuk menghubungkan ke database
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_mahasiswa", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException("error saat menghubungkan ke database: " + e.getMessage(), e);
        }
    }

    // method untuk menjalankan query select dengan parameter yang aman
    public ResultSet selectQuery(String sql, Object... params) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("error saat menjalankan select query: " + e.getMessage(), e);
        }
    }

    // method untuk menjalankan query insert, update, dan delete dengan parameter yang aman
    public int executeUpdate(String sql, Object... params) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("error saat menjalankan update query: " + e.getMessage(), e);
        }
    }

    // method untuk menutup koneksi database
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("error saat menutup koneksi database: " + e.getMessage(), e);
        }
    }
}