package com.mycompany.quizmaker001;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/quiz_maker";
    private static final String USER = "mysql";
    private static final String PASSWORD = "1234";

    private static Database instance; // Singleton példány
    private Connection connection;

    
    private Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Sikeres adatbázis kapcsolat!");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Nem sikerült csatlakozni az adatbázishoz!");
        }
    }

    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    
    public Connection getConnection() throws SQLException {
        try {
        return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Nem sikerült csatlakozni az adatbázishoz!", e);
        }
    }

    // Felhasználó regisztrációja
    public boolean registerUser(String username, String email, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if(!username.isBlank() && !email.isBlank() && !password.isBlank()){
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean loginUser(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password_hash");
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean alreadyRegistered(String email, String username){
    String checkSQL = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
    try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
        checkStmt.setString(1, username);
        checkStmt.setString(2, email);
        ResultSet rs = checkStmt.executeQuery();
        if(rs.next() && rs.getInt(1) > 0) {
            return true; 
        }else{
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    }
     public int executeInsert(String sql, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Beszúrás sikertelen, nincs érintett sor.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Az új `id` visszaadása
                } else {
                    throw new SQLException("Nem sikerült lekérni az ID-t.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  
        } 
    }
            public ObservableList<Quizzes> getQuizzesByUserId(int userId) {
            ObservableList<Quizzes> quizList = FXCollections.observableArrayList();
             String sql = "SELECT title, created_at FROM quizzes WHERE user_id = ?";

             try (Connection conn = getConnection();  
                  PreparedStatement stmt = conn.prepareStatement(sql)) {

                 stmt.setInt(1, userId);
                 ResultSet rs = stmt.executeQuery();

                 while (rs.next()) {
                     Timestamp date = rs.getTimestamp("created_at");
                     String title = rs.getString("title");
                     quizList.add(new Quizzes(title, date));
                 }

             } catch (SQLException e) {
                 e.printStackTrace();
             }

             return quizList;
            }
            
            public void deleteQuizByTitle(String title){
         String sql = "DELETE FROM quizzes WHERE title = ?";
    
            try (Connection conn = Database.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, title);
                stmt.executeUpdate();

                System.out.println("Kvíz törölve: " + title);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
         public void deleteQuizById(int id){
         String sql = "DELETE FROM quizzes WHERE id = ?";
    
            try (Connection conn = Database.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                stmt.executeUpdate();

                System.out.println("Kvíz törölve: " + id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
}
