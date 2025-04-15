module com.mycompany.quizmaker001 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires javafx.media;
    opens com.mycompany.quizmaker001 to javafx.fxml;
    exports com.mycompany.quizmaker001;
}
