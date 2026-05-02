package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class StageSelectController extends BorderPane {
    public StageSelectController(){
        Label title = new Label("Stage name");
        title.setFont(Font.font(30));

        Button back = new Button("X");
        back.setStyle(
                "-fx-background-radius: 50;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-background-color: #d1d1d1;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 18px;"
        );
        back.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });

        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(10, 10, 0, 20));

        StackPane.setAlignment(title, Pos.CENTER_LEFT);
        StackPane.setAlignment(back, Pos.TOP_RIGHT);

        topPane.getChildren().addAll(title, back);

        Button rule = createFooterButton("Rule");
        Button start = createFooterButton("Start");

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.setPadding(new Insets(10,10,10,10));
        hbox.getChildren().addAll(rule,start);

        this.setTop(topPane);
        this.setLeft(stageSelectBar());
        this.setRight(stageInformation());
        this.setBottom(hbox);

    }

    private VBox stageSelectBar(){
        Button stage1 = createMainButton("I");
        Button stage2 = createMainButton("II");
        Button stage3 = createMainButton("III");
        Button stage4 = createMainButton("IV");
        Button stage5 = createMainButton("V");

        VBox bar = new VBox(10, stage1, stage2, stage3, stage4, stage5);
        bar.setPadding(new Insets(20, 10, 20, 30));
        return bar;
    }

    private VBox stageInformation() {
        VBox storyBox = new VBox(5);
        Label storyTitle = new Label("Story");
        storyTitle.setFont(Font.font(30));
        Label storyDetail = new Label("กาลครั้งหนึ่งนานมาแล้ว...");
        storyDetail.setFont(Font.font(20));
        storyBox.getChildren().addAll(storyTitle, storyDetail);

        VBox infoBox = new VBox(5);
        Label infoTitle = new Label("Stage Details");
        infoTitle.setFont(Font.font(30));
        Label infoDetail = new Label(".\n.\n.\n.\n.\n.");
        infoDetail.setFont(Font.font(20));
        infoBox.getChildren().addAll(infoTitle, infoDetail);

        HBox characterBox = new HBox(15);
        characterBox.setAlignment(Pos.CENTER_LEFT);
        characterBox.setPadding(new Insets(20, 0, 0, 0));

        Button fire = createMainButton("Fire");
        Button water = createMainButton("Water");
        Button electric = createMainButton("Electric");

        characterBox.getChildren().addAll(fire, water, electric);

        Label choosingCharacter = new Label("Character :");
        choosingCharacter.setFont(Font.font(20));
        fire.setOnAction(e -> {
            choosingCharacter.setText("Character :   Fire");
        });
        water.setOnAction(e -> {
            choosingCharacter.setText("Character :   Water");
        });

        electric.setOnAction(e -> {
            choosingCharacter.setText("Character :   Electric");
        });

        VBox rightLayout = new VBox(20);
        rightLayout.setPadding(new Insets(20, 40, 20, 0));
        rightLayout.getChildren().addAll(storyBox, infoBox, characterBox,choosingCharacter);

        return rightLayout;
    }

    private Button createMainButton(String name) {
        Button btn = new Button(name);
        btn.setPrefSize(70, 70);
        btn.setStyle("-fx-background-radius: 40;");
        return btn;
    }

    private Button createFooterButton(String name){
        Button btn = new Button(name);
        btn.setPrefSize(100, 50);
        btn.setStyle("-fx-background-radius: 15;");
        return btn;
    }

}
