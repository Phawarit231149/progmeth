package gui;

import game.Element;
import game.util.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    // กล่องไอคอนศัตรูประจำ stage — ต้องอัปเดตเมื่อ stage เปลี่ยน
    private HBox enemyIconBox;
    // รูป preview แผนที่ stage ตรงกลาง — อัปเดตเมื่อ stage เปลี่ยน
    private ImageView stagePreview;

    public StageSelectController() {
        initializeStageSelection(); // ✅ ต้องมาก่อน
        setupBackground();
        SoundManager.playBGM("home.mp3");   // ใช้เพลงเดียวกับ Home (ไม่ restart ถ้าเล่นอยู่แล้ว)

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

        // ⭐️ เสียง hover + click
        SoundManager.attachUiSfx(back);
        SoundManager.attachUiSfx(rule);
        SoundManager.attachUiSfx(this.start);

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
        this.setCenter(buildStagePreview());
        this.setBottom(hbox);
    }

    /** สร้าง ImageView ตรงกลางที่แสดงรูปแผนที่ของ stage ที่เลือก */
    private StackPane buildStagePreview() {
        stagePreview = new ImageView();
        stagePreview.setPreserveRatio(true);
        stagePreview.setSmooth(false);
        // ขนาดเล็กลง — พอดีกับช่องว่างระหว่าง stage bar กับ panel ขวา
        stagePreview.setFitWidth(330);
        stagePreview.setFitHeight(330);

        StackPane wrapper = new StackPane(stagePreview);
        wrapper.setAlignment(Pos.CENTER);
        // จำกัดขนาด wrapper ไม่ให้ดัน BorderPane กว้างเกิน
        wrapper.setMaxWidth(360);
        wrapper.setMaxHeight(360);
        wrapper.setPadding(new Insets(8));
        BorderPane.setMargin(wrapper, new Insets(20, 10, 20, 10));

        // โหลดรูปแรก (Stage 1)
        updateStagePreview(0);
        return wrapper;
    }

    /** เปลี่ยนรูปแผนที่ตาม stage ที่เลือก */
    private void updateStagePreview(int stageIndex) {
        if (stagePreview == null) return;
        // Stage 1 ใช้ StageI.png (Roman numeral) ส่วน 2-5 ใช้ Stage2-5.png
        String fileName = (stageIndex == 0) ? "StageI" : "Stage" + (stageIndex + 1);
        try {
            Image img = new Image(
                    getClass().getResourceAsStream("/images/Stage/" + fileName + ".png")
            );
            stagePreview.setImage(img);
        } catch (Exception e) {
            System.out.println("[StageSelect] Stage preview not found: " + fileName);
            stagePreview.setImage(null);
        }
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
            SoundManager.attachUiSfx(btn);
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

        // ⭐️ อัปเดต icon ศัตรูตาม stage ที่เลือก
        updateEnemyIcons(index);
        // ⭐️ อัปเดตรูป preview แผนที่
        updateStagePreview(index);
    }

    private javafx.scene.shape.Circle getStageRing(Button btn) {
        Object data = btn.getUserData();
        if (data instanceof javafx.scene.shape.Circle c) return c;
        return null;
    }

    private VBox stageInformation() {
        // ⭐️ Enemies in this stage
        VBox enemyBox = new VBox(8);
        Label enemyTitle = new Label("Enemies");
        enemyTitle.setFont(pixelFont(34));
        enemyIconBox = new HBox(12);
        enemyIconBox.setAlignment(Pos.CENTER_LEFT);
        enemyBox.getChildren().addAll(enemyTitle, enemyIconBox);

        // ⭐️ Characters
        HBox characterBox = new HBox(15);
        characterBox.setAlignment(Pos.CENTER_LEFT);
        characterBox.setPadding(new Insets(10, 0, 0, 0));

        Button patrick   = createSelectCharacterButton("Patrick",   "characterProfile/patrickPfp.jpeg",   Element.FIRE);
        Button squidWard = createSelectCharacterButton("Squidward", "characterProfile/squidwardPfp.jpg",  Element.WATER);
        Button spongeBob = createSelectCharacterButton("SpongeBob", "characterProfile/spongebobPfp.jpg",  Element.ELECTRIC);
        characterBox.getChildren().addAll(patrick, squidWard, spongeBob);
        SoundManager.attachUiSfx(patrick);
        SoundManager.attachUiSfx(squidWard);
        SoundManager.attachUiSfx(spongeBob);

        Label choosingCharacter = new Label("Character   None");
        choosingCharacter.setFont(pixelFont(20));

        patrick.setOnAction(e -> handleCharacterSelect("Patrick",  choosingCharacter, patrick));
        squidWard.setOnAction(e -> handleCharacterSelect("Squidward", choosingCharacter, squidWard));
        spongeBob.setOnAction(e -> handleCharacterSelect("SpongeBob", choosingCharacter, spongeBob));

        Label characterTitle = new Label("Choose Character");
        characterTitle.setFont(pixelFont(34));

        VBox rightLayout = new VBox(18);
        rightLayout.setPadding(new Insets(30, 28, 22, 28));
        rightLayout.getChildren().addAll(enemyBox, characterTitle, characterBox, choosingCharacter);

        // กรอบไม้
        Background wood = createWoodBackground();
        if (wood != null) rightLayout.setBackground(wood);
        BorderPane.setMargin(rightLayout, new Insets(5, 10, 5, 5));

        // อัปเดต icon ศัตรูตาม stage แรก (Stage 1)
        updateEnemyIcons(0);

        return rightLayout;
    }

    /** อัปเดต icon ศัตรูตาม stage ที่เลือก */
    private void updateEnemyIcons(int stageIndex) {
        if (enemyIconBox == null) return;
        enemyIconBox.getChildren().clear();

        // mapping: ใช้ stage index (0-based) เพื่อรู้ว่า stage นี้มี enemy อะไร
        switch (stageIndex) {
            case 0: // Stage 1 — NPC easy + Gary (water)
                enemyIconBox.getChildren().addAll(
                        createEnemyIcon("/images/gamePlay/npc/NpcFont.png",     null),
                        createEnemyIcon("/images/gamePlay/gary/GaryFont.png",   Element.WATER)
                );
                break;
            case 1: // Stage 2 — 70% Sandy (electric), 30% MrKrab (fire)
                enemyIconBox.getChildren().addAll(
                        createEnemyIcon("/images/gamePlay/sandy/SandyFont.png", Element.ELECTRIC),
                        createEnemyIcon("/images/gamePlay/mrkrab/MrKrabFont.png", Element.FIRE)
                );
                break;
            case 2: // Stage 3 — 70% MrKrab (fire), 30% Gary (water)
                enemyIconBox.getChildren().addAll(
                        createEnemyIcon("/images/gamePlay/mrkrab/MrKrabFont.png", Element.FIRE),
                        createEnemyIcon("/images/gamePlay/gary/GaryFont.png",     Element.WATER)
                );
                break;
            case 3: // Stage 4 — 70% Gary (water), 30% Sandy (electric)
                enemyIconBox.getChildren().addAll(
                        createEnemyIcon("/images/gamePlay/gary/GaryFont.png",   Element.WATER),
                        createEnemyIcon("/images/gamePlay/sandy/SandyFont.png", Element.ELECTRIC)
                );
                break;
            case 4: // Stage 5 — ทั้ง 3 medium + KingNeptune (hard, random element)
                enemyIconBox.getChildren().addAll(
                        createEnemyIcon("/images/gamePlay/mrkrab/MrKrabFont.png", Element.FIRE),
                        createEnemyIcon("/images/gamePlay/gary/GaryFont.png",     Element.WATER),
                        createEnemyIcon("/images/gamePlay/sandy/SandyFont.png",   Element.ELECTRIC),
                        createEnemyIcon("/images/gamePlay/kingneptune/KingNepFront.png", null)
                );
                break;
        }
    }

    /** สร้างไอคอนศัตรูแบบวงกลม + element badge ที่มุมขวาบน */
    private StackPane createEnemyIcon(String imagePath, Element element) {
        double size = 60;

        ImageView iv = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            iv.setImage(img);
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            iv.setPreserveRatio(false);
            iv.setSmooth(false);
        } catch (Exception e) {
            System.out.println("[StageSelect] Enemy icon not found: " + imagePath);
        }

        // clip รูปให้เป็นวงกลม
        double r = size / 2;
        Circle clip = new Circle(r, r, r);
        iv.setClip(clip);

        // ขอบวงกลม
        Circle border = new Circle(r);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web("#3a2614"));
        border.setStrokeWidth(2);

        StackPane container = new StackPane(border, iv);
        container.setPrefSize(size, size);

        // element badge — ใช้ translate ดันไปมุมขวาบนของวงกลมจริงๆ
        if (element != null && element != Element.NONE) {
            StackPane badge = createElementBadge(element);
            // ดันไปมุมขวาบน: x = +size/2*0.7 (ออกขวา), y = -size/2*0.7 (ขึ้นบน)
            badge.setTranslateX(size * 0.35);
            badge.setTranslateY(-size * 0.35);
            container.getChildren().add(badge);
        }

        return container;
    }

    /** สร้าง badge เล็กๆ แสดง element (F = Fire, W = Water, E = Electric) */
    private StackPane createElementBadge(Element element) {
        String letter;
        Color color;
        switch (element) {
            case FIRE:     letter = "F"; color = Color.web("#e74c3c"); break;
            case WATER:    letter = "W"; color = Color.web("#3498db"); break;
            case ELECTRIC: letter = "E"; color = Color.web("#f1c40f"); break;
            default:       letter = "?"; color = Color.GRAY;           break;
        }
        Circle bg = new Circle(11, color);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);

        Label text = new Label(letter);
        text.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        StackPane badge = new StackPane(bg, text);
        return badge;
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
        //
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

    private Button createSelectCharacterButton(String name, String imageName, Element element) {
        double btnSize = 70;

        Image image = new Image(getClass().getResourceAsStream("/images/" + imageName));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(btnSize);
        imageView.setFitHeight(btnSize);
        imageView.setPreserveRatio(false);

        double radius = btnSize / 2;
        Circle clip = new Circle(radius, radius, radius);
        imageView.setClip(clip);

        // Border ring — visible when selected
        Circle ring = new Circle(radius + 3);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(Color.TRANSPARENT);
        ring.setStrokeWidth(3);

        StackPane graphic = new StackPane(ring, imageView);
        graphic.setPrefSize(btnSize + 6, btnSize + 6);

        // ⭐️ Element badge ที่มุมขวาบนของวงกลมจริงๆ (ใช้ translate)
        if (element != null && element != Element.NONE) {
            StackPane badge = createElementBadge(element);
            badge.setTranslateX(btnSize * 0.35);
            badge.setTranslateY(-btnSize * 0.35);
            graphic.getChildren().add(badge);
        }

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