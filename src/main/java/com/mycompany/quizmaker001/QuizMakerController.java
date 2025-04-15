
package com.mycompany.quizmaker001;

import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.Alert.AlertType.WARNING;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class QuizMakerController {
    @FXML
    private TextField questionField;
    @FXML
    private TextField correctAnswer;
    @FXML
    private TextField wrongAnswer1;
    @FXML
    private TextField wrongAnswer2;
    @FXML
    private TextField wrongAnswer3;
    @FXML
    private TextField quizName;
    @FXML
    private ComboBox<String> answersNumber;
    @FXML
    private Label wrongAnswerText1;
    @FXML
    private Label wrongAnswerText2;
    @FXML
    private TableView<Question> tableView;
    @FXML
    private TableColumn<Question, String> questionColumn;
    @FXML
    private TableColumn<Question, String> correctAnswerColumn;
    @FXML
    private TableColumn<Question, String> wrongAnswer1Column;
    @FXML
    private TableColumn<Question, String> wrongAnswer2Column;
    @FXML
    private TableColumn<Question, String> wrongAnswer3Column;
    @FXML
    private TableColumn<Question, Void> editColumn;
    @FXML
    private TableColumn<Question, Void> deleteColumn;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    Database db = Database.getInstance();
    ObservableList<Question> questionsList = FXCollections.observableArrayList();
    Session session = Session.getInstance();
    private boolean edit = false;
    private int id;
    private final ObjectProperty<Question> currentlyEditingQuestion = new SimpleObjectProperty<>(null);
    @FXML
    public void initialize(){
        MusicManager.playMenuMusic();
        tableView.setItems(questionsList);
        
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        wrongAnswer1Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer1"));
        wrongAnswer2Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer2"));
        wrongAnswer3Column.setCellValueFactory(new PropertyValueFactory<>("wrongAnswer3"));
        
        
        answersNumber.getItems().addAll("2","3","4");
        answersNumber.setValue("4");
        answersNumber.valueProperty().addListener((observable, oldValue, newValue)->{
        switch(newValue){
            case "2":
                wrongAnswerText1.setVisible(false);
                wrongAnswerText2.setVisible(false);
                wrongAnswer2.setVisible(false);
                wrongAnswer3.setVisible(false);
                wrongAnswer2.clear();
                wrongAnswer3.clear();
                break;
            case "3":
                wrongAnswerText1.setVisible(true);
                wrongAnswerText2.setVisible(false);
                wrongAnswer2.setVisible(true);
                wrongAnswer3.setVisible(false);
                wrongAnswer3.clear();
                break;
            case "4":
                wrongAnswerText1.setVisible(true);
                wrongAnswerText2.setVisible(true);
                wrongAnswer2.setVisible(true);
                wrongAnswer3.setVisible(true);
        }});
        
                    editColumn.setCellFactory(param -> new TableCell<>() {
                    private final Button editButton = new Button("Szerkesztés");

            {
                    editButton.setOnAction(event -> {
                    if(currentlyEditingQuestion.get() == null){
                        Question question = getTableView().getItems().get(getIndex());
                        questionField.setText(question.getQuestion());
                        correctAnswer.setText(question.getCorrectAnswer());
                        wrongAnswer1.setText(question.getWrongAnswer1());
                        wrongAnswer2.setText(question.getWrongAnswer2());
                        wrongAnswer3.setText(question.getWrongAnswer3());
                        currentlyEditingQuestion.set(question);
                        cancelButton.setVisible(true);    
                        addButton.setText("Szerkesztés");
                    }else{
                        cancelEditingConfirmation();
                        if(currentlyEditingQuestion.get() == null) clear();
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
                    if(currentlyEditingQuestion.get() == null){
                        Question questions = getTableView().getItems().get(getIndex());
                        questionsList.remove(questions);
                    }else{
                        cancelEditingConfirmation();
                        if(currentlyEditingQuestion.get() == null) clear();
                    }
                    
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
    
        currentlyEditingQuestion.addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            cancelButton.setVisible(true);
            addButton.setText("Mentés");
            tableView.refresh();
        } else {
            cancelButton.setVisible(false);
            addButton.setText("Hozzáadás");
            tableView.refresh();
        }
    });
    tableView.setRowFactory(tv -> new TableRow<Question>() {
    @Override
    protected void updateItem(Question item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setStyle("");
        } else if (item.equals(currentlyEditingQuestion.get())) {
            setStyle("-fx-background-color: #f3c675;"); 
        } else {
            setStyle("");
        }
    }
});
    }
    @FXML
    public void addQuestion(ActionEvent event) throws IOException {

    String q = questionField.getText();
    String c = correctAnswer.getText();    
    String w1 = wrongAnswer1.getText();
    String w2 = wrongAnswer2.getText();
    String w3 = wrongAnswer3.getText();
    String selectedValue = answersNumber.getValue();
    if(q.isBlank() || c.isBlank() || w1.isBlank() || w2.isBlank() || w3.isBlank() ){
        showAlert(WARNING, "Figyelmeztetés", "Töltsd ki az összes cellát!", "");
    }else if (c.equals(w1) || c.equals(w2) || c.equals(w3) || 
    w1.equals(w2) || w1.equals(w3) || w2.equals(w3)) {
        showAlert(WARNING, "Figyelmeztetés", "Nem lehet két ugyanolyan válaszlehetőség!", "Alakítsd át!");
    }else if (currentlyEditingQuestion.get() != null) {

    currentlyEditingQuestion.get().setQuestion(q);
    currentlyEditingQuestion.get().setCorrectAnswer(c);
    currentlyEditingQuestion.get().setWrongAnswer1(w1);
    currentlyEditingQuestion.get().setWrongAnswer2(w2);
    currentlyEditingQuestion.get().setWrongAnswer3(w3);


    tableView.refresh();
    currentlyEditingQuestion.set(null);
    clear();
} else {
    Question newQuestion;


    if (selectedValue.equals("2") && w2.isEmpty() && w3.isEmpty()  && !w1.isBlank() && !c.isBlank() && !q.isBlank()) {
        newQuestion = new Question(questionField.getText(), correctAnswer.getText(), w1);
        questionsList.add(newQuestion);
        clear();
    } else if (selectedValue.equals("3") && w3.isEmpty()  && !w1.isBlank() && !w2.isBlank() && !c.isBlank() && !q.isBlank()) {
        newQuestion = new Question(questionField.getText(), correctAnswer.getText(), w1, w2);
        questionsList.add(newQuestion);
        clear();
    } else if(selectedValue.equals("4") && !w1.isBlank() && !w2.isBlank() && !w3.isBlank() && !c.isBlank() && !q.isBlank()){
        newQuestion = new Question(questionField.getText(), correctAnswer.getText(), w1, w2, w3);
        questionsList.add(newQuestion);
        clear();
    }else{
        showAlert(WARNING,"figyelmeztetés","Valamelyik mező üres, töltsd ki rendesen az összeset!","");
    }
        tableView.refresh();
        }
    }
    @FXML
    public void completed(ActionEvent event) throws IOException{
        if (quizName.getText().isBlank()) {
            showAlert(WARNING, "Figyelmeztetés", "Adj nevet kvízednek!", "Ha nem adsz nevet akkor nem kerül elmentésre a kvízed!");
        return;
    }
    int userId = session.getUserId();

    String insertQuizSQL = "INSERT INTO quizzes (user_id, title) VALUES (?, ?)";
    
    int quizId = db.executeInsert(insertQuizSQL, userId, quizName.getText()); 

    if (quizId == -1) {
        System.out.println("Hiba történt a kvíz mentése közben!");
        return;
    }


    String insertQuestionSQL = "INSERT INTO questions (quiz_id, question_text) VALUES (?, ?)";
    String insertAnswerSQL = "INSERT INTO answers (question_id, answer_text, is_correct) VALUES (?, ?, ?)";

    for (Question q : questionsList) {
        int questionId = db.executeInsert(insertQuestionSQL, quizId, q.getQuestion());

        if (questionId == -1) {
            System.out.println("Hiba történt egy kérdés mentése közben!");
            continue;
        }

        // Válaszok beszúrása
        db.executeInsert(insertAnswerSQL, questionId, q.getCorrectAnswer(), true);
        if (q.getWrongAnswer1() != null && !q.getWrongAnswer1().isBlank()) {
            db.executeInsert(insertAnswerSQL, questionId, q.getWrongAnswer1(), false);
        }
        if (q.getWrongAnswer2() != null && !q.getWrongAnswer2().isBlank()) {
            db.executeInsert(insertAnswerSQL, questionId, q.getWrongAnswer2(), false);
        }
        if (q.getWrongAnswer3() != null && !q.getWrongAnswer3().isBlank()) {
            db.executeInsert(insertAnswerSQL, questionId, q.getWrongAnswer3(), false);
        }
    }

        System.out.println("Kvíz sikeresen mentve!");
        questionsList.clear();
        quizName.clear();
        tableView.refresh();
        if(edit){
            db.deleteQuizById(id);
        }
        showAlert(INFORMATION,"Sikeres mentés","Kvíz sikeresen Mentve!","");
        App.setRoot("myQuizzes");
    }
    
    @FXML
    public void cancelEditing(ActionEvent event) throws IOException {
        cancelEditingConfirmation();
        if(currentlyEditingQuestion.get() == null)clear();
    }
    @FXML
    public void BackToMenu(ActionEvent evet) throws IOException{
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("Biztosan kilépsz mentés nélkül?");
        ButtonType confirmationButtonType = new ButtonType("Igen");
        ButtonType cancelButtonType = new ButtonType("Nem");
        alert.getButtonTypes().setAll(confirmationButtonType, cancelButtonType);

        
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmationButtonType) {
           App.setRoot("myQuizzes");
        }
    }
    
    private void clear(){
        questionField.clear();
        correctAnswer.clear();
        wrongAnswer1.clear();
        wrongAnswer2.clear();
        wrongAnswer3.clear();
    }
    public void editQuiz(Quizzes quiz){
        GetQuizData data = new GetQuizData();
        id = data.getGameId(quiz);
        questionsList = data.getGameData(id);
        tableView.setItems(questionsList);
        quizName.setText(quiz.getTitle());
    }
    public void setEdit(boolean edit){
        this.edit = edit;
    }

    public void cancelEditingConfirmation(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("megerősítés");
        alert.setHeaderText("Biztosan kilépsz szerkesztésből mentés nélkül?");
        ButtonType confirmationButtonType = new ButtonType("Igen");
        ButtonType cancelButtonType = new ButtonType("Nem");
        alert.getButtonTypes().setAll(confirmationButtonType, cancelButtonType);

        
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == confirmationButtonType) {
           currentlyEditingQuestion.set(null);
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
