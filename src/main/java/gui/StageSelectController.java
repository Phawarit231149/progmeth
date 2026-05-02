package gui;

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

import java.util.List;

public class StageSelectController extends BorderPane {
    public StageSelectController(){
        initializeStageSelection();

        Button exit = new Button("Back");
        exit.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button rule = new Button("Rules");
        //rule.setOnAction(e -> );

        Button start = new Button("Start");
        start.setOnAction(e -> {
            GameOverController gameOverController = new GameOverController();
            this.getScene().setRoot(gameOverController);
        });

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.setPadding(new Insets(10,10,10,10));
        hbox.getChildren().addAll(exit,spacer,rule,start);

        this.setLeft(stageSelectBar());
        this.setRight(stageInformation());
        this.setBottom(hbox);

    }

    private void initializeStageSelection(){
        Label title = new Label("Stage name");
        this.setTop(title);

    }

    private VBox stageSelectBar(){
        Button stage1 = new Button("I");
        Button stage2 = new Button("II");
        Button stage3 = new Button("III");
        Button stage4 = new Button("IV");
        Button stage5 = new Button("V");

        VBox bar = new VBox(10, stage1, stage2, stage3, stage4, stage5);
        bar.setPadding(new Insets(20, 10, 20, 10));
        return bar;
    }

    private GridPane stageInformation(){
        Label story = new Label("story");
        Label information = new Label("information");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_RIGHT);
        grid.add(story,0,0);
        grid.add(information,0,1);

        return grid;
    }


}
