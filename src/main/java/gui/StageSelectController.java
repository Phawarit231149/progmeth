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
import model.GameProgress;
import model.StageData;

public class StageSelectController extends BorderPane {
    private int selectedStage = 0;
    private final Button start;
    private Label stageTitle;
    private Button[] stageButtons;
    private String selectedElement = "";
    private Button lastSelectedCharBtn = null;

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
            Button btn = createSelectStageButton(labels[i], GameProgress.isUnlocked(i));
            btn.setOnAction(e -> {
                if (GameProgress.isUnlocked(index)) {
                    selectStage(index);
                }
            });
            stageButtons[i] = btn;
            bar.getChildren().add(btn);
        }

        selectStage(0);
        return bar;
    }

    private void selectStage(int index) {
        if (!GameProgress.isUnlocked(index)) return;

        selectedStage = index;
        StageData config = StageData.ALL_STAGES[index];

        String[] names = {"I", "II", "III", "IV", "V"};
        stageTitle.setText("Stage " + names[index] +
                " (" + config.getCols() + " x " + config.getRows() + ")");

        for (int i = 0; i < stageButtons.length; i++) {
            javafx.scene.shape.Circle ring = getStageRing(stageButtons[i]);
            if (ring == null) continue;
            ring.setStroke(i == index
                    ? javafx.scene.paint.Color.web("#2196F3")
                    : javafx.scene.paint.Color.TRANSPARENT);
        }
    }

    private javafx.scene.shape.Circle getStageRing(Button btn) {
        Object data = btn.getUserData();
        if (data instanceof javafx.scene.shape.Circle c) return c;
        return null;
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

        Button patrick = createSelectCharacterButton("Patrick","characterProfile/patrickPfp.jpeg");
        Button squidWard = createSelectCharacterButton("Squidward","characterProfile/squidwardPfp.jpg");
        Button spongeBob = createSelectCharacterButton("SpongeBob","characterProfile/spongebobPfp.jpg");
        characterBox.getChildren().addAll(patrick, squidWard, spongeBob);

        Label choosingCharacter = new Label("Character :   None");
        choosingCharacter.setFont(Font.font(20));

        patrick.setOnAction(e -> handleCharacterSelect("Patrick",choosingCharacter,patrick));
        squidWard.setOnAction(e -> handleCharacterSelect("Squidward",choosingCharacter,squidWard));
        spongeBob.setOnAction(e -> handleCharacterSelect("SpongeBob",choosingCharacter,spongeBob));

        VBox rightLayout = new VBox(20);
        rightLayout.setPadding(new Insets(20, 40, 20, 0));
        rightLayout.getChildren().addAll(storyBox, infoBox, characterBox, choosingCharacter);

        return rightLayout;
    }

    private void handleCharacterSelect(String name, Label choosingLabel, Button clickedBtn) {
        javafx.scene.shape.Circle clickedRing = getRing(clickedBtn);

        if (selectedElement.equals(name)) {
            // Deselect
            selectedElement = "";
            choosingLabel.setText("Character :   None");
            start.setDisable(true);
            if (clickedRing != null) clickedRing.setStroke(javafx.scene.paint.Color.TRANSPARENT);
            lastSelectedCharBtn = null;
        } else {
            // Clear previous ring
            if (lastSelectedCharBtn != null) {
                javafx.scene.shape.Circle prevRing = getRing(lastSelectedCharBtn);
                if (prevRing != null) prevRing.setStroke(javafx.scene.paint.Color.TRANSPARENT);
            }
            // Select new
            selectedElement = name;
            choosingLabel.setText("Character :   " + name);
            start.setDisable(false);
            if (clickedRing != null) {
                clickedRing.setStroke(javafx.scene.paint.Color.web("#2196F3"));
            }
            lastSelectedCharBtn = clickedBtn;
        }
    }

    private javafx.scene.shape.Circle getRing(Button btn) {
        Object data = btn.getUserData();
        if (data instanceof Object[] arr && arr.length > 1 && arr[1] instanceof javafx.scene.shape.Circle c) {
            return c;
        }
        return null;
    }

    private Button createSelectStageButton(String label, boolean unlocked) {
        double btnSize = 70;

        javafx.scene.shape.Circle ring = new javafx.scene.shape.Circle(btnSize / 2 + 3);
        ring.setFill(javafx.scene.paint.Color.TRANSPARENT);
        ring.setStroke(javafx.scene.paint.Color.TRANSPARENT);
        ring.setStrokeWidth(3);

        Label text = new Label(unlocked ? label : "🔒");
        text.setFont(Font.font(unlocked ? 16 : 18));
        text.setTextFill(unlocked
                ? javafx.scene.paint.Color.BLACK
                : javafx.scene.paint.Color.web("#757575"));

        javafx.scene.shape.Circle bg = new javafx.scene.shape.Circle(btnSize / 2);
        bg.setFill(unlocked
                ? javafx.scene.paint.Color.web("#e0e0e0")
                : javafx.scene.paint.Color.web("#bdbdbd"));

        StackPane graphic = new StackPane(ring, bg, text);
        graphic.setPrefSize(btnSize + 6, btnSize + 6);

        Button btn = new Button();
        btn.setGraphic(graphic);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        btn.setUserData(ring);

        return btn;
    }

    private Button createSelectCharacterButton(String name, String imageName) {
        double btnSize = 70;

        Image image = new Image(getClass().getResourceAsStream("/images/" + imageName));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(btnSize);
        imageView.setFitHeight(btnSize);
        imageView.setPreserveRatio(false);

        double radius = btnSize / 2;
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(radius, radius, radius);
        imageView.setClip(clip);

        // Border ring — visible when selected
        javafx.scene.shape.Circle ring = new javafx.scene.shape.Circle(radius + 3);
        ring.setFill(javafx.scene.paint.Color.TRANSPARENT);
        ring.setStroke(javafx.scene.paint.Color.TRANSPARENT);
        ring.setStrokeWidth(3);

        StackPane graphic = new StackPane(ring, imageView);
        graphic.setPrefSize(btnSize + 6, btnSize + 6);

        Button btn = new Button();
        btn.setGraphic(graphic);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-background-insets: 0; " +
                        "-fx-border-color: transparent; " +
                        "-fx-padding: 0;"
        );
        btn.setUserData(new Object[]{name, ring}); // store name + ring for later

        return btn;
    }

    private Button createFooterButton(String name) {
        Button btn = new Button(name);
        btn.setPrefSize(100, 50);
        btn.setStyle("-fx-background-radius: 15;");
        return btn;
    }
}