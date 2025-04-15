
package com.mycompany.quizmaker001;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class myQuizzesController {
    @FXML
    private TableView<Quizzes> quizTable;  
    @FXML
    private TableColumn<Quizzes, String> titleColumn;
    @FXML
    private TableColumn<Quizzes, Timestamp> dateColumn;
    @FXML
    private TableColumn<Quizzes, Void> playColumn;
    @FXML
    private TableColumn<Quizzes, Void> editColumn;
    @FXML
    private TableColumn<Quizzes, Void> deleteColumn;
    
    
    @FXML
    public void quizMaker(ActionEvent event) throws IOException{
        App.setRoot("quizMaker");
    }

    @FXML
    public void initialize() {
    MusicManager.playMenuMusic();
    Session session = Session.getInstance();
    int userId = session.getUserId();
    Database db = Database.getInstance();
    ObservableList<Quizzes> quizzes = db.getQuizzesByUserId(userId);
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    
    quizTable.setItems(quizzes);
    
    playColumn.setCellFactory(param -> new TableCell<>() {
    private final Button playButton = new Button("Játék");

    {
        playButton.setOnAction(event -> {
            Quizzes quiz = getTableView().getItems().get(getIndex());
            System.out.println("Játék indítása: " + quiz.getTitle());

     try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent root = loader.load();

        GameController gameController = loader.getController();
        gameController.setQuiz(quiz); 

        App.setRoot(root);
    } catch (IOException e) {
        e.printStackTrace();
    }
        });
    }
    
    

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(playButton);
        }
    }
    });
    
            editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Szerkesztés");

            {
                editButton.setOnAction(event -> {
                    Quizzes quiz = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("quizMaker.fxml"));
                        Parent root = loader.load();
                        
                        QuizMakerController quizMaker = loader.getController();
                        
                        
                        System.out.println("Szerkesztés indítása: " + quiz.getTitle());
                        quizMaker.setEdit(true);
                        quizMaker.editQuiz(quiz);
                        App.setRoot(root);
                    } catch (IOException ex) {
                        Logger.getLogger(myQuizzesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
            
            deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("törlés");

            {
                deleteButton.setOnAction(event -> {
                    Quizzes quiz = getTableView().getItems().get(getIndex());
                    System.out.println("Törölt quiz: " + quiz.getTitle());
                    db.deleteQuizByTitle(quiz.getTitle());
                    reloadScene();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

    }
    private void reloadScene() {
    try {
        App.setRoot("myQuizzes");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
