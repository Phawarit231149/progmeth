package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
        setupBackground();

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

        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.setPadding(new Insets(10, 15, 8, 10));
        hbox.getChildren().addAll(rule, start);

        this.setTop(topPane);
        this.setLeft(stageSelectBar());
        this.setRight(stageInformation());
        this.setBottom(hbox);
    }

    private void initializeStageSelection() {
        stageTitle = new Label("Stage I");
        stageTitle.setFont(pixelFont(24));
    }

    /** ใช้ SelectStageBG.png เป็นพื้นหลัง */
    private void setupBackground() {
        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream("/images/StageSelect/SelectStageBG.png")
            );
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO, BackgroundSize.AUTO,
                            false, false, true, true
                    )
            );
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("[StageSelect] Background load failed: " + e.getMessage());
        }
    }

    /** โหลด pixel font จาก /Font/pixelFont.ttf — เรียกใช้เมื่อต้องการ font ทุกขนาด */
    private Font pixelFont(double size) {
        Font f = Font.loadFont(
                getClass().getResourceAsStream("/font/pixelFont.TTF"), size
        );
        // ถ้าโหลดไม่สำเร็จให้ fallback เป็น default font ขนาดเดียวกัน
        return f != null ? f : Font.font(size);
    }

    /** สร้าง Background ไม้ (BackStage.png) แบบ stretch เต็มกรอบ ให้ขอบไม้โผล่ตามขอบของ panel */
    private Background createWoodBackground() {
        try {
            Image woodImage = new Image(
                    getClass().getResourceAsStream("/images/StageSelect/BackStage.png")
            );
            BackgroundImage bgImg = new BackgroundImage(
                    woodImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            1.0, 1.0,
                            true, true,    // ใช้เป็น percentage
                            false, false   // ไม่ contain ไม่ cover -> stretch เต็ม
                    )
            );
            return new Background(bgImg);
        } catch (Exception e) {
            System.out.println("[StageSelect] Wood background not found");
            return null;
        }
    }

    private VBox stageSelectBar() {
        String[] labels = {"I", "II", "III", "IV", "V"};
        stageButtons = new Button[5];

        VBox bar = new VBox(3);
        // padding ด้านในขอบไม้ — เผื่อพื้นที่ให้ขอบไม้โผล่
        bar.setPadding(new Insets(12, 18, 12, 18));
        bar.setAlignment(Pos.CENTER);
        Background wood = createWoodBackground();
        if (wood != null) bar.setBackground(wood);
        // ใส่ margin ระหว่างกรอบไม้กับขอบ window
        BorderPane.setMargin(bar, new Insets(5, 5, 5, 10));

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
        Label storyTitle = new Label("  Story");
        storyTitle.setFont(pixelFont(26));
        Label storyDetail = new Label(" SpongeBomb must blast through obstacles and tiny \n" +
                " invaders to clear the grid before his five minute\n" +
                " timer hits zero");

        storyDetail.setFont(pixelFont(16));
        storyBox.getChildren().addAll(storyTitle, storyDetail);

        VBox infoBox = new VBox(5);
        Label infoTitle = new Label("Stage Details");
        infoTitle.setFont(pixelFont(26));
        Label infoDetail = new Label(".\n.\n.\n.\n.\n.");
        infoDetail.setFont(pixelFont(16));
        infoBox.getChildren().addAll(infoTitle, infoDetail);

        HBox characterBox = new HBox(15);
        characterBox.setAlignment(Pos.CENTER_LEFT);
        characterBox.setPadding(new Insets(20, 0, 0, 0));

        Button patrick = createSelectCharacterButton("Patrick","characterProfile/patrickPfp.jpeg");
        Button squidWard = createSelectCharacterButton("Squidward","characterProfile/squidwardPfp.jpg");
        Button spongeBob = createSelectCharacterButton("SpongeBob","characterProfile/spongebobPfp.jpg");
        characterBox.getChildren().addAll(patrick, squidWard, spongeBob);

        Label choosingCharacter = new Label("Character :   None");
        choosingCharacter.setFont(pixelFont(16));

        patrick.setOnAction(e -> handleCharacterSelect("Patrick",choosingCharacter,patrick));
        squidWard.setOnAction(e -> handleCharacterSelect("Squidward",choosingCharacter,squidWard));
        spongeBob.setOnAction(e -> handleCharacterSelect("SpongeBob",choosingCharacter,spongeBob));

        VBox rightLayout = new VBox(15);
        rightLayout.setPadding(new Insets(22, 28, 22, 28));
        rightLayout.getChildren().addAll(storyBox, infoBox, characterBox, choosingCharacter);

        // ใส่กรอบไม้ BackStage.png ให้กล่อง story/details/character
        Background wood = createWoodBackground();
        if (wood != null) rightLayout.setBackground(wood);
        BorderPane.setMargin(rightLayout, new Insets(5, 10, 5, 5));

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
        double btnSize = 60;

        // ⭐️ รูป pixel art ของ stage (I, II, III, IV, V)
        ImageView iv = new ImageView();
        try {
            Image img = new Image(
                    getClass().getResourceAsStream("/images/StageSelect/" + label + ".png")
            );
            iv.setImage(img);
            iv.setFitWidth(btnSize);
            iv.setFitHeight(btnSize);
            iv.setPreserveRatio(true);
            iv.setSmooth(false);
        } catch (Exception e) {
            System.out.println("[StageSelect] Button image not found: " + label);
        }

        // ถ้ายังไม่ปลด lock — ทำให้รูปจางลง
        if (!unlocked) {
            iv.setOpacity(0.4);
        }

        // Ring แสดงว่า stage นี้ถูกเลือก
        javafx.scene.shape.Circle ring = new javafx.scene.shape.Circle(btnSize / 2 + 4);
        ring.setFill(javafx.scene.paint.Color.TRANSPARENT);
        ring.setStroke(javafx.scene.paint.Color.TRANSPARENT);
        ring.setStrokeWidth(3);

        StackPane graphic = new StackPane(ring, iv);
        graphic.setPrefSize(btnSize + 12, btnSize + 12);

        // ถ้า lock ใส่ overlay 🔒 ทับรูป
        if (!unlocked) {
            Label lockLabel = new Label("🔒");
            lockLabel.setFont(Font.font(28));
            graphic.getChildren().add(lockLabel);
        }

        Button btn = new Button();
        btn.setGraphic(graphic);
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );
        btn.setUserData(ring);

        // hover/press scale effect (เฉพาะตอน unlock)
        if (unlocked) {
            btn.setOnMouseEntered(e -> {
                btn.setScaleX(1.1);
                btn.setScaleY(1.1);
                btn.setCursor(Cursor.HAND);
            });
            btn.setOnMouseExited(e -> {
                btn.setScaleX(1.0);
                btn.setScaleY(1.0);
            });
            btn.setOnMousePressed(e -> {
                btn.setScaleX(0.97);
                btn.setScaleY(0.97);
            });
            btn.setOnMouseReleased(e -> {
                btn.setScaleX(1.1);
                btn.setScaleY(1.1);
            });
        }

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
        Button btn = new Button();
        try {
            Image img = new Image(
                    getClass().getResourceAsStream("/images/StageSelect/" + name + ".png")
            );
            ImageView iv = new ImageView(img);
            // กำหนดทั้ง fitWidth + fitHeight + preserveRatio เพื่อ cap ขนาดไม่ให้ใหญ่เกิน
            iv.setFitWidth(210);
            iv.setFitHeight(100);
            iv.setPreserveRatio(true);
            iv.setSmooth(false);
            btn.setGraphic(iv);
        } catch (Exception e) {
            // fallback: text-only
            btn.setText(name);
            btn.setPrefSize(100, 50);
        }

        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );

        // hover/press effect
        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                btn.setScaleX(1.08);
                btn.setScaleY(1.08);
                btn.setCursor(Cursor.HAND);
            }
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                btn.setScaleX(0.96);
                btn.setScaleY(0.96);
            }
        });
        btn.setOnMouseReleased(e -> {
            if (!btn.isDisabled()) {
                btn.setScaleX(1.08);
                btn.setScaleY(1.08);
            }
        });

        return btn;
    }
}