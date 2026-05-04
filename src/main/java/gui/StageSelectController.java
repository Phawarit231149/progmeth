package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.StageData;

public class StageSelectController extends BorderPane {
    private int selectedStage = 0;
    private final Button start;
    private Label stageTitle;
    private Button[] stageButtons;
    private String selectedElement = "";

    public StageSelectController() {
        initializeStageSelection(); // ✅ ต้องมาก่อน

        Button back = new Button("X");

        back.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });

        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(10, 10, 0, 20));
        StackPane.setAlignment(stageTitle, Pos.CENTER_LEFT);
        StackPane.setAlignment(back, Pos.TOP_RIGHT);
        topPane.getChildren().addAll(stageTitle, back);

        Button rule = createFooterButton("Rule");
        this.start = createFooterButton("Start");

        rule.setOnAction(e -> {
            RuleController ruleController = new RuleController();
            this.getScene().setRoot(ruleController);
        });

        start.setOnAction(e -> {
            StageData config = StageData.ALL_STAGES[selectedStage];
            GameController gameController = new GameController(config, selectedElement);
            this.getScene().setRoot(gameController);
        });

        start.setDisable(true);

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.getChildren().addAll(rule, start);

        this.setTop(topPane);
        this.setLeft(stageSelectBar());
        this.setRight(stageInformation());
        this.setBottom(hbox);
    }

    private void initializeStageSelection() {
        stageTitle = new Label("Stage I");
        stageTitle.setFont(Font.font(30));
    }

    private VBox stageSelectBar() {
        String[] labels = {"I", "II", "III", "IV", "V"};
        stageButtons = new Button[5];

        VBox bar = new VBox(10);
        bar.setPadding(new Insets(20, 10, 20, 30));

        for (int i = 0; i < 5; i++) {
            final int index = i;
            Button btn = createSelectStageButton(labels[i]);
            btn.setOnAction(e -> selectStage(index));
            stageButtons[i] = btn;
            bar.getChildren().add(btn);
        }

        selectStage(0);
        return bar;
    }

    private void selectStage(int index) {
        selectedStage = index;
        StageData config = StageData.ALL_STAGES[index];

        String[] names = {"I", "II", "III", "IV", "V"};
        stageTitle.setText("Stage " + names[index] +
                " (" + config.getCols() + " x " + config.getRows() + ")");

        for (int i = 0; i < stageButtons.length; i++) {
            stageButtons[i].setStyle(i == index
                    ? "-fx-border-color: #2196F3; -fx-border-width: 2px; -fx-background-radius: 40;"
                    : "-fx-background-radius: 40;");
        }
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

        Button mrKrabs = createSelectCharacterButton("Mr.Krabs","characterProfile/mrKrabPfp.jpg");
        Button squidWard = createSelectCharacterButton("Squidward","characterProfile/squidwardPfp.jpg");
        Button spongeBob = createSelectCharacterButton("SpongeBob","characterProfile/spongebobPfp.jpg");
        characterBox.getChildren().addAll(mrKrabs, squidWard, spongeBob);

        Label choosingCharacter = new Label("Character :   None");
        choosingCharacter.setFont(Font.font(20));

        mrKrabs.setOnAction(e -> handleCharacterSelect("Mr.Krabs",choosingCharacter,mrKrabs));
        squidWard.setOnAction(e -> handleCharacterSelect("Squidward",choosingCharacter,squidWard));
        spongeBob.setOnAction(e -> handleCharacterSelect("SpongeBob",choosingCharacter,spongeBob));

        VBox rightLayout = new VBox(20);
        rightLayout.setPadding(new Insets(20, 40, 20, 0));
        rightLayout.getChildren().addAll(storyBox, infoBox, characterBox, choosingCharacter);

        return rightLayout;
    }

    private void handleCharacterSelect(String name, Label choosingLabel, Button clickedBtn) {
        if (selectedElement.equals(name)) {
            selectedElement = "";
            choosingLabel.setText("Character :   None");
            start.setDisable(true);
        } else {
            selectedElement = name;
            choosingLabel.setText("Character :   " + name);
            start.setDisable(false);
        }
    }

    private Button createSelectStageButton(String name) {
        Button btn = new Button(name);
        btn.setPrefSize(70, 70);
        btn.setStyle("-fx-background-radius: 40;");

        return btn;
    }

    private Button createSelectCharacterButton(String name,String imageName) {
        Button btn = new Button(name);
        double btnSize = 70;
        btn.setPrefSize(btnSize, btnSize);
        btn.setMinSize(btnSize, btnSize);
        btn.setMaxSize(btnSize, btnSize);

        btn.setStyle("-fx-background-color: transparent; " +
                "-fx-background-insets: 0; " +
                "-fx-border-color: transparent; " +
                "-fx-background-radius: 40;");

        Image image = new Image(getClass().getResourceAsStream("/images/" + imageName));
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(btnSize);
        imageView.setFitHeight(btnSize);

        // ⭐️ สำคัญ: เปลี่ยนเป็น false เพื่อให้ภาพยืด/หดจนเต็มวงกลมพอดี
        imageView.setPreserveRatio(false);

        // 3. ตัดรูปให้เป็นวงกลม (เช็คจุดศูนย์กลางให้แม่น)
        double radius = btnSize / 2;
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(radius, radius, radius);
        imageView.setClip(clip);

        btn.setGraphic(imageView);

        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        return btn;
    }

    private Button createFooterButton(String name) {
        Button btn = new Button(name);
        btn.setPrefSize(100, 50);
        btn.setStyle("-fx-background-radius: 15;");
        return btn;
    }
}