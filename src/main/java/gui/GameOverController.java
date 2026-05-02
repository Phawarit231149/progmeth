package gui;

import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class GameOverController extends BorderPane {

    public GameOverController() {

        Label title = new Label("Stage Clear!");
        title.setFont(Font.font(50));
        title.setPrefSize(500,200);
        title.setAlignment(Pos.CENTER);

        Button nextStage = new Button("Next Stage");
        Button retry = new Button("Retry");
        Button quit = new Button("Quit");

        nextStage.setPrefSize(200,50);
        retry.setPrefSize(200,50);
        quit.setPrefSize(200,50);

        nextStage.setOnAction(e -> {});

        retry.setOnAction(e -> {});

        quit.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        //if win but not stage 5
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(50));
        vbox.getChildren().addAll(title, nextStage, retry, quit);

        this.setCenter(vbox);

        //if lose

        // clear stage 5

    }
}
