Beadásra kész lett a projekt még sok mindent akartam volna csinálni csak már versenyt futottam az idővel. Sokkal jobban szétszedtem volna az osztályokat és például az alertnek is kellett volna simán csináljak egy osztályt és akkor nem lett volna több osztályban is külön 1-1 alert metódus.
Tisztában vagyok ezekkel a hibákkal de inkább arra feküdtem rá hogy minden lehetőség le legyen kezelve meg legyenek extra funkciók mint stopper és ehhez hasonlók. Ha lett volna még egy hetem akkor ez is meglett volna és még sokkal több funkciót is raktam volna még bele mert a profilom csak egy gomb.
Akartam volna beállítások fült is hogy a zenét lehessen halkítani vagy leállítani. A unit Tesztből tudom, kettő kellett volna és csak egyet csináltam de mivel minden osztály össze volt fonódva eléggé a az fx-el így normális unit tesztet nem lehetett csinálni csak bonyolultabban sokkal.
A database osztály tesztje jó csak ha nem irányítjuk át teszt adatbázisra akkor beleír az éles adatbázisba ami nem okoz gondot de belerondít de utána ki lehet törölni az adatbázisból egy kattintással amit beleír.
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Az adatbázishoz megírtam már az elején a kódot hogy meg legyen egyben sorban az adatbázis létrehozásához szükséges parancsok Ezt elég bemásolni és táblánként és kész.
Itt a mysql adatbázishoz szükséges kód: 
1. Felhasználók táblája
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

2. Kvízek táblája (kapcsolat a users táblával)
CREATE TABLE quizzes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

3. Kérdések táblája (kapcsolat a quizzes táblával)
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id INT NOT NULL,
    question_text TEXT NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

4. Válaszok táblája (kapcsolat a questions táblával)
CREATE TABLE answers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);
