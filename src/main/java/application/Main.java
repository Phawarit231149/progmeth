package application;

import game.util.SoundManager;
import gui.HomeController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SoundManager.playBGM("spongebobBGM.mp3");
        HomeController home = new HomeController();
        Scene scene = new Scene(home, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Dodoco Bombtastic");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}