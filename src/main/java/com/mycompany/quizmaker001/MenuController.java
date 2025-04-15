
package com.mycompany.quizmaker001;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {
    @FXML
    public void myQuizzes(ActionEvent event) throws IOException{
        App.setRoot("myQuizzes");
    }
    @FXML
    public void initialize(){
        MusicManager.playMenuMusic();
    }
}
