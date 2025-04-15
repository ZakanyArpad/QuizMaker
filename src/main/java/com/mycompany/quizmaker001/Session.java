
package com.mycompany.quizmaker001;

public class Session {
    private static Session instance;
    private int userId;

    private Session(int userId) {
        this.userId = userId;
    }

    public static void createSession(int userId) {
        instance = new Session(userId);
    }

    public static Session getInstance() {
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public static void clearSession() {
        instance = null;
    }
}
