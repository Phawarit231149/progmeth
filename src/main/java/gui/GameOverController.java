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
import model.StageData;

import java.util.List;

public class GameOverController extends BorderPane {

    public GameOverController(status gameResult, StageData config) {

        Label title = new Label("");
        title.setFont(Font.font(50));
        title.setPrefSize(500,200);
        title.setAlignment(Pos.CENTER);

        Button nextStage = new Button("Next Stage");
        Button retry = new Button("Retry");
        Button back = new Button("Back to stage");

        nextStage.setPrefSize(200,50);
        retry.setPrefSize(200,50);
        back.setPrefSize(200,50);

        nextStage.setOnAction(e -> {
            GameController gameController = new GameController(StageData.ALL_STAGES[config.getLevel() + 1]);
            this.getScene().setRoot(gameController);
        });

        retry.setOnAction(e -> {
            GameController gameController = new GameController(config);
            this.getScene().setRoot(gameController);
        });

        back.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(50));

        if(gameResult.equals(status.WIN)){
            title.setText("Stage Clear!");
            vbox.getChildren().addAll(title, nextStage, retry, back);
        }
        if(gameResult.equals(status.LOSE)){
            title.setText("You Lose!");
            vbox.getChildren().addAll(title,retry,back);
        }
        if(gameResult.equals(status.CLEAR)){
            title.setText("Congratulations!");
            vbox.getChildren().addAll(title,retry,back);
        }

        this.setCenter(vbox);

    }
}
