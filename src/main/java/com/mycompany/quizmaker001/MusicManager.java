
package com.mycompany.quizmaker001;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer menuPlayer;
    private static MediaPlayer quizPlayer;
    private static MediaPlayer correct;
    private static MediaPlayer wrong;

    public static void playMenuMusic() {
        if (menuPlayer == null) {
            Media media = new Media(MusicManager.class.getResource("/audio/MenuMusic.mp3").toExternalForm());
            menuPlayer = new MediaPlayer(media);
            menuPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        stopQuizMusic();
        menuPlayer.play();
    }

    public static void playQuizMusic() {
        if (quizPlayer == null) {
            Media media = new Media(MusicManager.class.getResource("/audio/inGameMusic.mp3").toExternalForm());
            quizPlayer = new MediaPlayer(media);
            quizPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        stopMenuMusic();
        quizPlayer.play();
    }
    public static void playCorrectSoundEffect(){
        Media media = new Media(MusicManager.class.getResource("/audio/correct.mp3").toExternalForm());
        correct = new MediaPlayer(media);
        correct.play();
    }
        public static void playWrongSoundEffect(){
        Media media = new Media(MusicManager.class.getResource("/audio/wrong.mp3").toExternalForm());
        wrong = new MediaPlayer(media);
        wrong.play();
    }
        public static void playSuccessQuiz(){
        Media media = new Media(MusicManager.class.getResource("/audio/quizSuccess.mp3").toExternalForm());
        wrong = new MediaPlayer(media);
        wrong.play();
    }

    public static void stopMenuMusic() {
        if (menuPlayer != null) {
            menuPlayer.stop();
        }
    }

    public static void stopQuizMusic() {
        if (quizPlayer != null) {
            quizPlayer.stop();
        }
    }
}
