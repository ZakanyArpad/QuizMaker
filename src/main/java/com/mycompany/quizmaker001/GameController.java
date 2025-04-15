package com.mycompany.quizmaker001;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;




public class GameController {
    private int answersNumber = 0;
    private int correctAnswers = 0;
    private GetQuizData quizData = new GetQuizData();
    private ObservableList<Question> questions; 
    private int currentQuestionIndex = 0;      
    @FXML 
    private Label quizTitleLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private Button gameButton1;
    @FXML
    private Button gameButton2;
    @FXML
    private Button gameButton3;
    @FXML
    private Button gameButton4;
    @FXML
    private Label stats;
    @FXML
    private VBox GamePane;
    @FXML
    private VBox statPane;
    @FXML
    private Label timerLabel;
    @FXML
    private ImageView stopperimg;
    
    private Timeline timer;
    private IntegerProperty timeLeft = new SimpleIntegerProperty();
    private int time;
    private boolean timerOn = false;
    public void setQuiz(Quizzes quiz) {
        quizData.setQuiz(quiz);
        questions = quizData.getGameData(quiz.getId());
        quizTitleLabel.setText(quiz.getTitle());
        Platform.runLater(() -> {
        answersNumber = questions.size();
        showQuestion(currentQuestionIndex);
        });
        testGameData(quiz);
    }

    @FXML
    public void initialize() {
        optionalTimerSetUp();
        MusicManager.playQuizMusic();
        GamePane.setVisible(true);
        statPane.setVisible(false);
        timerLabel.textProperty().bind(timeLeft.asString().concat(" mp"));
        timerLabel.setVisible(timerOn);
        stopperimg.setVisible(timerOn);
    }

    private void showQuestion(int index) {
            
            if (index < questions.size()) {
            Question question = questions.get(index);

            questionLabel.setText(question.getQuestion()); 

            List<String> answers = new ArrayList<>();
            answers.add(question.getCorrectAnswer());
            if (question.getWrongAnswer1() != null) answers.add(question.getWrongAnswer1());
            if (question.getWrongAnswer2() != null) answers.add(question.getWrongAnswer2());
            if (question.getWrongAnswer3() != null) answers.add(question.getWrongAnswer3());

            Collections.shuffle(answers); 

            gameButton1.setText(answers.get(0));
            gameButton2.setText(answers.get(1));
            if(question.getWrongAnswer2() != null){
                gameButton3.setVisible(true);
                gameButton3.setText(answers.get(2));
            }else{
                gameButton3.setVisible(false);
            }
            if(question.getWrongAnswer3() != null){
            gameButton4.setVisible(true);
            gameButton4.setText(answers.get(3));
            }else{
                gameButton4.setVisible(false);
            }
        } else {
            System.out.println("Játék vége!");
        }
    }
    

    @FXML
    private void handleAnswer(ActionEvent event) throws IOException {
    if (timerOn && timer != null) {
        timer.stop();
    }
    Button clickedButton = (Button) event.getSource();
    String selectedAnswer = clickedButton.getText();
    String correctAnswer = questions.get(currentQuestionIndex).getCorrectAnswer();

    
    List<Button> allButtons = List.of(gameButton1, gameButton2, gameButton3, gameButton4);

    
    Button correctButton = allButtons.stream()
        .filter(b -> b.getText().equals(correctAnswer))
        .findFirst().orElse(null);

    
    if (selectedAnswer.equals(correctAnswer)) {
        MusicManager.stopQuizMusic();
        MusicManager.playCorrectSoundEffect();
        clickedButton.getStyleClass().add("correct-answer");
        correctAnswers++;
    } else {
        MusicManager.stopQuizMusic();
        MusicManager.playWrongSoundEffect();
        clickedButton.getStyleClass().add("wrong-answer");
        if (correctButton != null) {
            correctButton.getStyleClass().add("correct-answer");
        }
    }

    
    if (correctButton != null) {
        blinkButton(correctButton, 3); 
    }

    
    allButtons.forEach(b -> b.setDisable(true));

    
    new Thread(() -> {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {}

        Platform.runLater(() -> {
            allButtons.forEach(b -> {
                MusicManager.playQuizMusic();
                b.getStyleClass().removeAll("correct-answer", "wrong-answer");
                b.setDisable(false);
            });

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                if(timerOn){
                    startTimer(time);
                }
                showQuestion(currentQuestionIndex);
            } else {
                int stat = (int) (((double)correctAnswers/(double)answersNumber)*100);
                stats.setText(stat+"%");
                GamePane.setVisible(false);
                statPane.setVisible(true);
                MusicManager.stopQuizMusic();
                MusicManager.playSuccessQuiz();
            }
        });
    }).start();
        
    }
    @FXML
    public void BackToMenu(ActionEvent event) throws IOException{
        App.setRoot("myQuizzes");
    }
    
    public void testGameData(Quizzes quiz) {
    
    quizData.setQuiz(quiz);

    
    Integer quizId = quizData.getGameId(quiz);
    if (quizId == null) {
        System.out.println(" Nem található ilyen kvíz.");
        return;
    }

    questions = quizData.getGameData(quizId);
    for (Question q : questions) {
        System.out.println(" Kérdés: " + q.getQuestion());
        System.out.println(" Helyes válasz: " + q.getCorrectAnswer());

        if (q.getWrongAnswer1() != null) {
            System.out.println(" Rossz válasz 1: " + q.getWrongAnswer1());
        }
        if (q.getWrongAnswer2() != null) {
            System.out.println(" Rossz válasz 2: " + q.getWrongAnswer2());
        }
        if (q.getWrongAnswer3() != null) {
            System.out.println(" Rossz válasz 3: " + q.getWrongAnswer3());
        }
        System.out.println("--------------------------------");
    }
}
    public void showAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
}
    private void blinkButton(Button button, int times) {
    new Thread(() -> {
        try {
            for (int i = 0; i < times; i++) {
                Platform.runLater(() -> button.setOpacity(0));
                Thread.sleep(200);
                Platform.runLater(() -> button.setOpacity(1));
                Thread.sleep(200);
            }
        } catch (InterruptedException ignored) {}
    }).start();
}
    /*
    private void showEndSummary() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Játék vége");
    alert.setHeaderText(null);
    alert.setContentText("Helyes válaszok száma: " + correctAnswers + "/" + answersNumber);
    alert.setOnHidden(e -> {
        try {
            App.setRoot("myQuizzes");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    });
    alert.show();
}
*/
        private void startTimer(int seconds) {
            timeLeft.set(seconds);

            timer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeLeft.set(timeLeft.get() - 1);
                    if (timeLeft.get() <= 0) {
                        timer.stop();
                        
                        handleTimeOut();
                    }
                })
            );
            timer.setCycleCount(seconds);
            timer.play();
    }
        private void optionalTimerSetUp(){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Időzítő beállítása");
            alert.setHeaderText("Szeretnél időkorlátot beállítani a kvízhez?");
            alert.setContentText("A kérdések megválaszolására időkorlátot adhatsz meg.");

            ButtonType yesButton = new ButtonType("Igen");
            ButtonType noButton = new ButtonType("Nem", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
            
            timerOn = true;
            TextInputDialog inputDialog = new TextInputDialog("60"); 
            inputDialog.setTitle("Időkorlát beállítása");
            inputDialog.setHeaderText("Add meg az időkorlátot másodpercben!");
            inputDialog.setContentText("Másodpercek száma:");

            Optional<String> inputResult = inputDialog.showAndWait();

            if (inputResult.isPresent()) {
                try {
                    int seconds = Integer.parseInt(inputResult.get());
                        if (seconds > 0) {
                            time = seconds;
                            startTimer(seconds); 
                        } else {
                            showAlert(INFORMATION,"Kérlek pozitív számot adj meg!","Próbáld újra!","");
                        }
                    } catch (NumberFormatException e) {
                        showAlert(INFORMATION,"Nem érvényes számot adtál meg!","Próbáld újra!","");
                    }
                }
            }
        }
        
        private void handleTimeOut() {
            String correctAnswer = questions.get(currentQuestionIndex).getCorrectAnswer();
            List<Button> allButtons = List.of(gameButton1, gameButton2, gameButton3, gameButton4);

            Button correctButton = allButtons.stream()
                .filter(b -> b.getText().equals(correctAnswer))
                .findFirst().orElse(null);

            
            MusicManager.stopQuizMusic();
            MusicManager.playWrongSoundEffect();

            if (correctButton != null) {
                correctButton.getStyleClass().add("correct-answer");
                blinkButton(correctButton, 3);
            }

            
            allButtons.forEach(b -> b.setDisable(true));

            
            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ignored) {}

                Platform.runLater(() -> {
                    allButtons.forEach(b -> {
                        b.getStyleClass().removeAll("correct-answer", "wrong-answer");
                        b.setDisable(false);
                    });

                    currentQuestionIndex++;
                    if (currentQuestionIndex < questions.size()) {
                        showQuestion(currentQuestionIndex);
                        startTimer(time);
                        MusicManager.playQuizMusic();
                    } else {
                        int stat = (int) (((double)correctAnswers / (double)answersNumber) * 100);
                        stats.setText(stat + "%");
                        GamePane.setVisible(false);
                        statPane.setVisible(true);
                        MusicManager.stopQuizMusic();
                        MusicManager.playSuccessQuiz();
                    }
                });
            }).start();
    }
}
