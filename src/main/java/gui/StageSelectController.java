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

        Button back = new Button("X");
        back.setStyle(
                "-fx-background-radius: 20; " +
                "-fx-min-width: 30px; -fx-min-height: 30px; " +
                "-fx-background-color: #d3d3d3;"
        );
        back.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });

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
        hbox.getChildren().addAll(rule,start);

        this.setTop(back);
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

    private VBox stageInformation() {
        VBox storyBox = new VBox(5);
        Label storyTitle = new Label("Story");
        storyTitle.setFont(Font.font(30));
        Label storyDetail = new Label("กาลครั้งหนึ่งนานมาแล้ว...");
        storyDetail.setFont(Font.font(15));
        storyBox.getChildren().addAll(storyTitle, storyDetail);

        VBox infoBox = new VBox(5);
        Label infoTitle = new Label("ข้อมูลด่าน");
        infoTitle.setFont(Font.font("System Bold", 30));
        Label infoDetail = new Label("ศัตรู: 5 ตัว\nระเบิด: 10 ลูก");
        infoDetail.setFont(Font.font(15));
        infoBox.getChildren().addAll(infoTitle, infoDetail);

        HBox characterBox = new HBox(15);
        characterBox.setAlignment(Pos.CENTER_LEFT);
        characterBox.setPadding(new Insets(20, 0, 0, 0));

        Button fire = createCharacterButton("Fire");
        Button water = createCharacterButton("Water");
        Button electric = createCharacterButton("Electric");

        characterBox.getChildren().addAll(fire, water, electric);

        Label choosingCharacter = new Label("Character :");
        fire.setOnAction(e -> {
            choosingCharacter.setText("Character : Fire");
        });
        water.setOnAction(e -> {
            choosingCharacter.setText("Character : Water");
        });

        electric.setOnAction(e -> {
            choosingCharacter.setText("Character : Electric");
        });

        VBox rightLayout = new VBox(30);
        rightLayout.setPadding(new Insets(20, 40, 20, 0));
        rightLayout.getChildren().addAll(storyBox, infoBox, characterBox,choosingCharacter);

        return rightLayout;
    }

    private Button createCharacterButton(String name) {
        Button btn = new Button(name);
        btn.setPrefSize(80, 80); // ทำเป็นรูปทรงสี่เหลี่ยมจัตุรัสตามที่คุณวาด
        btn.setStyle("-fx-background-radius: 40;"); // ถ้าอยากให้ปุ่มเป็นวงกลมเหมือนในรูปสเก็ตซ์
        btn.setOnAction(e -> System.out.println("Selected: " + name));
        return btn;
    }

}
