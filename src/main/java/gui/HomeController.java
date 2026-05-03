package gui;

import game.util.SoundManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeController extends StackPane {

    private SettingsController settingsPane = new SettingsController();

    public HomeController() {
        setupUI();
        SoundManager.playBGM("spongebobBGM.mp3");
    }

    private void setupUI() {

        // ฝั่งซ้าย — ปุ่มครับ
        Button startButton = new Button("Start Game");
        Button settingButton = new Button("Settings");
        Button quitButton = new Button("Quit");

        for (Button btn : new Button[]{startButton, settingButton, quitButton}) {
            btn.setPrefWidth(280);
            btn.setPrefHeight(65);
            btn.setStyle("-fx-font-size: 20px;");
        }

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
        Label title = new Label("Dodoco Bombtastic");
        title.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        Label subtitle = new Label("Adventure Puzzles");
        subtitle.setStyle("-fx-font-size: 22px;");

        VBox rightBox = new VBox(20, title, subtitle);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(0, 80, 0, 0));

        // รวมซ้ายขวาใน HBox ครับ
        HBox hbox = new HBox(leftBox, rightBox);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(rightBox, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().add(hbox);
    }
}