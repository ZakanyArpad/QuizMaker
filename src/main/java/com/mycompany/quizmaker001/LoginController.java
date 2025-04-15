package com.mycompany.quizmaker001;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private ToggleGroup entry;
    
    @FXML
    private VBox entryMenu;
    @FXML
    private VBox mainMenu;
    @FXML
    private RadioButton loginRadioButton;
    @FXML
    private RadioButton registerRadioButton;
    
    Database db = Database.getInstance();


    @FXML
    public void initialize() {
    emailField.setVisible(false);
    entry.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        RadioButton selected = (RadioButton) newValue;

        if ("loginRadioButton".equals(selected.getId())) {  
            emailField.setVisible(false);
            loginButton.setText("Bejelentkezés");
        } else if ("registerRadioButton".equals(selected.getId())) {  
            emailField.setVisible(true);
            loginButton.setText("Regisztráció");
        }
    });
    
    }

    @FXML
    public void loginAndRegister(ActionEvent event) throws IOException, SQLException{
        db.getConnection();
        RadioButton selected = (RadioButton) entry.getSelectedToggle();
        String selectedId = selected.getId();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        switch (selectedId) {
            case "loginRadioButton":
                if(db.loginUser(username, password)){
                String sql = "SELECT id FROM users WHERE username = ?";
                try (PreparedStatement stmt = db.getConnection().prepareStatement(sql)) {
                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        int userId = rs.getInt("id");  
                        System.out.println("Felhasználó ID: " + userId);

                        
                        Session.createSession(userId);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                App.setRoot("mainMenu");
                }else{
                    showAlert(AlertType.ERROR,"Hiba", "Sikertelen bejelentkezés","Hibás Felhasználónév vagy jelszó. Próbáld újra!");
                }
                break;
                case "registerRadioButton":
                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        showAlert(AlertType.ERROR, "Hiba", "Sikertelen regisztráció!", "Hibás email, felhasználónév vagy jelszó. Próbáld újra!");
                        break;
                    }

                    if (db.alreadyRegistered(email, username)) {
                        showAlert(AlertType.INFORMATION, "Próbálkozás sikertelen!", "Sikertelen regisztráció!", "Már használatban levő email cím vagy felhasználónév. Próbáld újra!");
                        break;
                    }

                    if (!db.registerUser(username, email, password)) {
                        showAlert(AlertType.ERROR, "Hiba", "Sikertelen regisztráció!", "Valami hiba történt mentés közben.");
                        break;
                    }

                    loginRadioButton.setSelected(true);
                    showAlert(AlertType.CONFIRMATION, "Regisztráció", "A regisztráció sikeres volt!", "");
                break;
        }
    }
    public void showAlert(AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
}
    
}
