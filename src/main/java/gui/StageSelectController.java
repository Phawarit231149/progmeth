package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class StageSelectController extends BorderPane {
    public StageSelectController(){
        initializeStageSelection();
        Button exit = new Button("Exit");
        exit.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });
        this.setLeft(stageSelectBar());
        this.setRight(stageInformation());
        this.setBottom(exit);

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
        grid.setAlignment(Pos.CENTER);
        grid.add(story,0,0);
        grid.add(information,0,1);

        return grid;
    }


}
