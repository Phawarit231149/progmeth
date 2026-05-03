package gui;

import game.util.SoundManager; // ⭐️ 1. อย่าลืม Import คลาส SoundManager เข้ามา
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsController extends StackPane {

    private Runnable onClose;

    public SettingsController() {
        setupUI();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    private void setupUI() {
        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // ⭐️ 2. สร้าง Slider โดยดึงค่าเริ่มต้นมาจาก SoundManager
        Slider overallSlider = createSlider(SoundManager.getOverallVolume());
        Slider sfxSlider = createSlider(SoundManager.getSfxVolume());
        Slider musicSlider = createSlider(SoundManager.getMusicVolume());

        // ⭐️ 3. เวลาเลื่อน Slider ให้สั่งอัปเดตค่าไปที่ SoundManager ด้วย
        overallSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sfxSlider.setValue(newVal.doubleValue());
            musicSlider.setValue(newVal.doubleValue());
            SoundManager.setOverallVolume(newVal.doubleValue()); // เซฟค่า Overall
        });

        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setSfxVolume(newVal.doubleValue()); // เซฟค่า SFX
        });

        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setMusicVolume(newVal.doubleValue()); // เซฟค่า Music
        });

        // ... (ปุ่ม Close Button ของคุณเหมือนเดิม) ...
        Button closeButton = new Button("X");
        closeButton.setOnAction(e -> {
            if (this.onClose != null) {
                this.onClose.run();
            }
        });

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(15, 15, 0, 0));

        VBox vbox = new VBox(20,
                title,
                createRow("Volume", overallSlider),
                createRow("SFX Volume", sfxSlider),
                createRow("Music Volume", musicSlider)
        );
        vbox.setAlignment(Pos.CENTER);
        // ...

        this.getChildren().addAll(vbox, closeButton);
    }

    // ... (เมธอด createRow เหมือนเดิม) ...
    private HBox createRow(String labelText, Slider slider) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 18px;");
        label.setPrefWidth(180);

        Button resetButton = new Button("✓");
        resetButton.setStyle("-fx-font-size: 16px;");
        resetButton.setPrefWidth(40);
        resetButton.setPrefHeight(40);
        resetButton.setOnAction(e -> slider.setValue(50));

        HBox row = new HBox(20, label, slider, resetButton);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 20, 12, 20));
        row.setPrefWidth(650);
        row.setStyle(
                "-fx-background-color: #e0e0e0;" +
                        "-fx-background-radius: 30 30 30 30;" +
                        "-fx-border-radius: 30 30 30 30;"
        );

        return row;
    }

    // ⭐️ 4. แก้ให้ createSlider รับพารามิเตอร์ initialValue
    private Slider createSlider(double initialValue) {
        Slider slider = new Slider(0, 100, initialValue); // ใช้ initialValue แทนเลข 50
        slider.setPrefWidth(400);
        return slider;
    }
}