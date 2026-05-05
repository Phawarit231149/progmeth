package gui;

import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.StageData;

public class GameOverController extends BorderPane {

    public GameOverController(Status gameResult, StageData config, String name) {

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
            GameController gameController = new GameController(StageData.ALL_STAGES[config.getLevel()], name);
            this.getScene().setRoot(gameController);
        });

        retry.setOnAction(e -> {
            GameController gameController = new GameController(config, name);
            this.getScene().setRoot(gameController);
        });

        back.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(50));

        if(gameResult.equals(Status.WIN)){
            title.setText("Stage Clear!");
            vbox.getChildren().addAll(title, nextStage, retry, back);
            this.setCenter(vbox);
        }
        if(gameResult.equals(Status.LOSE)){
            title.setText("Mission Fail!");
            vbox.getChildren().addAll(title,retry,back);
            this.setCenter(vbox);
        }
        if(gameResult.equals(Status.CLEAR)){
            title.setText("Congratulations!");
            Label subtitle = new Label(".\n.\n.\n.");
            subtitle.setFont(Font.font(24));
            vbox.getChildren().addAll(title,subtitle);

            HBox hbox = new HBox(20);
            hbox.setAlignment(Pos.CENTER);
            hbox.setPadding(new Insets(50));
            hbox.getChildren().addAll(retry,back);
            this.setTop(vbox);
            this.setCenter(hbox);
        }

    }
}
