package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsController extends StackPane {

    private Runnable onClose; // ✅ callback

    public SettingsController() {
        setupUI();
    }

    // ✅ ให้ HomeController set callback ตัวนี้
    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    private void setupUI() {

        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        Slider overallSlider = createSlider();
        Slider sfxSlider = createSlider();
        Slider musicSlider = createSlider();

        overallSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sfxSlider.setValue(newVal.doubleValue());
            musicSlider.setValue(newVal.doubleValue());
        });

        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-background-radius: 50;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-background-color: #d1d1d1;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 18px;"
        );

        // ✅ กดปุ่ม X แล้ว trigger callback กลับไป
        closeButton.setOnAction(e -> {
            HomeController homeController = new HomeController();
            this.getScene().setRoot(homeController);
        });

        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(15, 15, 0, 0));

        VBox vbox = new VBox(20,
                title,
                createRow("Volume", overallSlider),
                createRow("SFX Volume", sfxSlider),
                createRow("Music Volume", musicSlider)
        );
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(50, 0, 0, 80));

        this.getChildren().addAll(vbox, closeButton);
    }

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

    private Slider createSlider() {
        Slider slider = new Slider(0, 100, 50);
        slider.setPrefWidth(400);
        return slider;
    }
}