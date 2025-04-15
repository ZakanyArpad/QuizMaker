
package com.mycompany.quizmaker001;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GetQuizData {
    Database db = Database.getInstance();
    private Quizzes quiz;

    public void setQuiz(Quizzes quiz) {
        this.quiz = quiz;
        if (quiz != null) {
            int id = getGameId(quiz);
            System.out.println("Quiz beállítva! ID: " + id);
        } else {
            System.out.println(" Warning: setQuiz() hívása null értékkel!");
        }
    }

    public Quizzes getQuiz() {
        return quiz;
    }

    public Integer getGameId(Quizzes quiz) {
        if (quiz == null) return null; 

        String sql = "SELECT id FROM quizzes WHERE title = ?";
        try (Connection connection = db.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, quiz.getTitle());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    
   public ObservableList<Question> getGameData(int quizId) {
    ObservableList<Question> questions = FXCollections.observableArrayList();
    String questionSql = "SELECT id, question_text FROM questions WHERE quiz_id = ?";

    try (Connection connection = db.getConnection();
            PreparedStatement questionStmt = connection.prepareStatement(questionSql)) {
        questionStmt.setInt(1, quizId);
        ResultSet questionRs = questionStmt.executeQuery();

        while (questionRs.next()) {
            int questionId = questionRs.getInt("id");
            String questionText = questionRs.getString("question_text");

            
            List<Answer> answers = getAnswersForQuestion(questionId);

            
            if (answers.size() >= 2) { 
                String correct = null;
                String wrong1 = null, wrong2 = null, wrong3 = null;

                for (Answer ans : answers) {
                    if (ans.isCorrect()) {
                        correct = ans.getAnswerText();
                    } else if (wrong1 == null) {
                        wrong1 = ans.getAnswerText();
                    } else if (wrong2 == null) {
                        wrong2 = ans.getAnswerText();
                    } else {
                        wrong3 = ans.getAnswerText();
                    }
                }

                
                if (wrong3 != null) {
                    questions.add(new Question(questionText, correct, wrong1, wrong2, wrong3));
                } else if (wrong2 != null) {
                    questions.add(new Question(questionText, correct, wrong1, wrong2));
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return questions;
}
        
        private List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String answerSql = "SELECT answer_text, is_correct FROM answers WHERE question_id = ?";

        try (Connection connection = db.getConnection();
                PreparedStatement answerStmt = connection.prepareStatement(answerSql)) {
            answerStmt.setInt(1, questionId);
            ResultSet answerRs = answerStmt.executeQuery();

            while (answerRs.next()) {
                String answerText = answerRs.getString("answer_text");
                boolean isCorrect = answerRs.getBoolean("is_correct");

                answers.add(new Answer(answerText, isCorrect));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
        }
        
}
