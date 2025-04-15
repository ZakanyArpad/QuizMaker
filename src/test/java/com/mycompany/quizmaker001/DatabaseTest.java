
package com.mycompany.quizmaker001;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DatabaseTest {
    
 static Database db;

    @BeforeAll
    public static void setUpClass() {
        db = Database.getInstance();
    }

    @Test
    public void testRegisterUser() {
        
        String username = "testUser" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "testpass123";

        boolean result = db.registerUser(username, email, password);
        assertTrue(result, "A regisztrációnak sikeresnek kell lennie.");
    }

    @Test
    public void testLoginUser() {
        String username = "loginuser_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "securepass";

        
        db.registerUser(username, email, password);

        
        boolean loginResult = db.loginUser(username, password);
        assertTrue(loginResult, "A bejelentkezésnek sikeresnek kell lennie a helyes jelszóval.");
    }

    @Test
    public void testAlreadyRegistered() {
        String username = "existinguser_" + System.currentTimeMillis();
        String email = username + "@example.com";
        String password = "pass123";

        
        db.registerUser(username, email, password);

        
        boolean alreadyExists = db.alreadyRegistered(email, username);
        assertTrue(alreadyExists, "Létező felhasználónál igazat kell visszaadnia.");
    }
    
    @Test
    public void testLoginUser_fail() {
    String username = "nemlétezőnév";
    String password = "rosszjelszó";

    boolean result = db.loginUser(username, password);
    assertFalse(result, "Sikertelen bejelentkezésnek kellene lennie, mert nem létezik ilyen felhasználó!");
}
}
