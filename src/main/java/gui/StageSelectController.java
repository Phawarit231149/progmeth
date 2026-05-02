package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class StageSelectController extends BorderPane {
    public StageSelectController(){
        initializeStageSelection();

        Button back = new Button("Back");
        back.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button rule = new Button("Rules");
        rule.setOnAction(e -> {
            RuleController rules = new RuleController();
            this.getScene().setRoot(rules);
        });

        Button start = new Button("Start");
        start.setOnAction(e -> {
            GameOverController gameOverController = new GameOverController();
            this.getScene().setRoot(gameOverController);
        });

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.setPadding(new Insets(10,10,10,10));
        hbox.getChildren().addAll(back,spacer,rule,start);

        this.setLeft(stageSelectBar());
        this.setRight(stageInformation());
        this.setBottom(hbox);

    }

    private void initializeStageSelection(){
        Label title = new Label("Stage name");
        title.setFont(Font.font(30));
        this.setTop(title);

        BorderPane.setMargin(title,new Insets(20,0,0,20));
    }

    private VBox stageSelectBar(){
        Button stage1 = new Button("I");
        Button stage2 = new Button("II");
        Button stage3 = new Button("III");
        Button stage4 = new Button("IV");
        Button stage5 = new Button("V");

        stage1.setPrefSize(50,50);
        stage2.setPrefSize(50,50);
        stage3.setPrefSize(50,50);
        stage4.setPrefSize(50,50);
        stage5.setPrefSize(50,50);

        VBox bar = new VBox(10, stage1, stage2, stage3, stage4, stage5);
        bar.setPadding(new Insets(20, 10, 20, 10));
        return bar;
    }

    private GridPane stageInformation(){
        Label story = new Label("story");
        story.setFont(Font.font(30));
        Label information = new Label("information");
        information.setFont(Font.font(30));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_RIGHT);

        grid.add(story, 0, 0);
        grid.add(information, 0, 1);
        grid.setVgap(10);
       BorderPane.setMargin(grid,new Insets(0,0,50,0));

        return grid;
    }


}
