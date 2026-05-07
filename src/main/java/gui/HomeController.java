package gui;

import game.util.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeController extends StackPane {

    private SettingsController settingsPane = new SettingsController();

    public HomeController() {
        setupBackground();
        setupUI();
        SoundManager.playBGM("home.mp3");
    }

    private void setupBackground() {
        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream("/images/homeDecoration/Home.png")
            );
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO, BackgroundSize.AUTO,
                            false, false,
                            true,  // contain
                            true   // cover -> ภาพจะ scale เต็มหน้าจอ
                    )
            );
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("[Home] Background load failed: " + e.getMessage());
        }
    }

    private void setupUI() {

        // ฝั่งซ้าย — ปุ่ม pixel art ครับ
        Button startButton = createImageButton("/images/homeDecoration/startButton.png");
        Button settingButton = createImageButton("/images/homeDecoration/settingButton.png");
        Button quitButton = createImageButton("/images/homeDecoration/exitButton.png");

        // ⭐️ เสียง hover + click
        SoundManager.attachUiSfx(startButton);
        SoundManager.attachUiSfx(settingButton);
        SoundManager.attachUiSfx(quitButton);

        startButton.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        settingButton.setOnAction(e -> {
            // ⭐️ 2. ดึง Scene ปัจจุบันเก็บไว้
            javafx.scene.Scene currentScene = this.getScene();

            // ⭐️ 3. เรียกใช้ settingsPane ตัวเดิมที่เราสร้างไว้ข้างบน ไม่ต้อง new ใหม่แล้ว!
            settingsPane.setOnClose(() -> {
                currentScene.setRoot(this);
            });

            currentScene.setRoot(settingsPane);
        });
        quitButton.setOnAction(e -> System.exit(0));

        VBox leftBox = new VBox(25, startButton, settingButton, quitButton);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 80));

        // ฝั่งขวา — ชื่อเกม + รายละเอียดครับ


        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(0, 80, 0, 0));

        // รวมซ้ายขวาใน HBox ครับ
        HBox hbox = new HBox(leftBox, rightBox);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().add(hbox);
    }

    /**
     * สร้างปุ่มจากรูป pixel art พร้อม hover/press animation
     */
    private Button createImageButton(String resourcePath) {
        Button btn = new Button();
        try {
            Image img = new Image(getClass().getResourceAsStream(resourcePath));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(280);
            iv.setPreserveRatio(true);
            iv.setSmooth(false); // ปิด anti-aliasing เพื่อให้ pixel ยังคมชัด
            btn.setGraphic(iv);
        } catch (Exception e) {
            System.out.println("[Home] Button image not found: " + resourcePath);
        }

        // ทำให้ background ปุ่มโปร่งใส โชว์เฉพาะรูป
        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );

        // hover -> ขยายเล็กน้อย + เปลี่ยน cursor เป็นรูปมือ
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
            btn.setCursor(Cursor.HAND);
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        // pressed -> ย่อนิดหน่อย ให้รู้สึกเหมือนกดจริง
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.96);
            btn.setScaleY(0.96);
        });
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
        });

        return btn;
    }
}