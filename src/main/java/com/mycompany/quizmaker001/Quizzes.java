
package com.mycompany.quizmaker001;

import java.sql.Timestamp;

public class Quizzes {
    private int id;
    private String title;
    private Timestamp date;
    
    public Quizzes(int id, String title, Timestamp date ){
        this.id = id;
        this.title = title;
        this.date = date;
    }
        public Quizzes(String title, Timestamp date ){
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    
    
}
