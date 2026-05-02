package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class    HomeController extends StackPane {

    public HomeController() {
        setupUI();
    }

    private void setupUI() {

        Label title = new Label("Dodoco Bombtastic");
        Label subtitle = new Label("Adventure Puzzles");

        Button startButton = new Button("Start Game");
        Button settingButton = new Button("Settings");
        Button quitButton = new Button("Quit");

        startButton.setOnAction(e -> System.out.println("Start clicked!"));
        settingButton.setOnAction(e -> System.out.println("Settings clicked!"));
        quitButton.setOnAction(e -> System.exit(0));

        startButton.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        VBox vbox = new VBox(10, title, subtitle, startButton, settingButton, quitButton);
        vbox.setAlignment(Pos.CENTER);

        this.getChildren().add(vbox);
    }
}
