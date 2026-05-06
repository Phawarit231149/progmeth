package gui;

import game.util.SoundManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsController extends StackPane {

    // ขนาด panel ตรงกลาง — ปรับได้ตามต้องการ
    private static final double PANEL_WIDTH = 760;
    private static final double PANEL_HEIGHT = 520;

    private Runnable onClose;

    public SettingsController() {
        setupBackground();
        setupUI();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    /** พื้นหลังเต็มจอ — ใช้ Home.png เหมือนหน้า Home */
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
                            false, false, true, true
                    )
            );
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("[Settings] Background load failed: " + e.getMessage());
        }
    }

    private void setupUI() {
        // ⭐️ Panel ตรงกลาง — ใช้ bgSetting.png เป็นพื้นหลังของ panel
        StackPane panel = new StackPane();
        panel.setMaxWidth(PANEL_WIDTH);
        panel.setMaxHeight(PANEL_HEIGHT);
        panel.setMinWidth(PANEL_WIDTH);
        panel.setMinHeight(PANEL_HEIGHT);
        try {
            Image panelBg = new Image(
                    getClass().getResourceAsStream("/images/settingDecoration/bgSetting.png")
            );
            BackgroundImage panelBgImage = new BackgroundImage(
                    panelBg,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    new BackgroundSize(
                            PANEL_WIDTH, PANEL_HEIGHT,
                            false, false, false, false
                    )
            );
            panel.setBackground(new Background(panelBgImage));
        } catch (Exception e) {
            System.out.println("[Settings] Panel background not found");
        }

        // ⭐️ Title — รูป setting.png ขนาดให้พอดีกรอบ
        ImageView titleImage = new ImageView();
        try {
            Image titleImg = new Image(
                    getClass().getResourceAsStream("/images/settingDecoration/setting.png")
            );
            titleImage.setImage(titleImg);
            titleImage.setFitWidth(260);
            titleImage.setPreserveRatio(true);
            titleImage.setSmooth(false);
        } catch (Exception e) {
            System.out.println("[Settings] Title image not found");
        }

        // ⭐️ Sliders
        Slider overallSlider = createSlider(SoundManager.getOverallVolume());
        Slider sfxSlider = createSlider(SoundManager.getSfxVolume());
        Slider musicSlider = createSlider(SoundManager.getMusicVolume());

        overallSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sfxSlider.setValue(newVal.doubleValue());
            musicSlider.setValue(newVal.doubleValue());
            SoundManager.setOverallVolume(newVal.doubleValue());
        });
        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setSfxVolume(newVal.doubleValue());
        });
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setMusicVolume(newVal.doubleValue());
        });

        // ⭐️ VBox ของ row sliders — จัดให้อยู่กลางแนวตั้งของ panel
        VBox rowsBox = new VBox(14,
                createRow("Volume", overallSlider),
                createRow("SFX Volume", sfxSlider),
                createRow("Music Volume", musicSlider)
        );
        rowsBox.setAlignment(Pos.CENTER);
        rowsBox.setPadding(new Insets(15, 35, 25, 35));
        StackPane.setAlignment(rowsBox, Pos.CENTER);

        // ⭐️ Close button (X) — อยู่ที่มุมขวาบนของ panel
        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #c0392b;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25;" +
                "-fx-min-width: 42;" +
                "-fx-min-height: 42;" +
                "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> {
            if (this.onClose != null) {
                this.onClose.run();
            }
        });
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(20, 25, 0, 0));

        // ⭐️ Title ติดบนของ panel — เลื่อนขึ้นไปด้านบนสุด
        StackPane.setAlignment(titleImage, Pos.TOP_CENTER);
        StackPane.setMargin(titleImage, new Insets(-30, 0, 0, 0));

        // ⭐️ rowsBox อยู่กึ่งกลางของ panel (StackPane จัด CENTER ให้อัตโนมัติ)
        rowsBox.setMaxHeight(Region.USE_PREF_SIZE);
        rowsBox.setMaxWidth(Region.USE_PREF_SIZE);
        StackPane.setAlignment(rowsBox, Pos.CENTER);

        // ใส่ทุกอย่างลงใน panel
        panel.getChildren().addAll(titleImage, rowsBox, closeButton);

        // ใส่ panel ลงใน root (centered โดยอัตโนมัติเพราะ StackPane)
        StackPane.setAlignment(panel, Pos.CENTER);
        this.getChildren().add(panel);
    }
    private HBox createRow(String labelText, Slider slider) {
        Label label = new Label(labelText);
        label.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #3a2614;"  // สีน้ำตาลเข้มให้กลืนกับโทนกรอบไม้
        );
        label.setPrefWidth(160);
        label.setMinWidth(160);

        slider.setPrefWidth(220);

        Button resetButton = createImageButton(
                "/images/settingDecoration/settingdefault.png", 160
        );
        resetButton.setOnAction(e -> slider.setValue(50));

        HBox row = new HBox(15, label, slider, resetButton);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 0, 2, 0));
        return row;
    }

    private Slider createSlider(double initialValue) {
        Slider slider = new Slider(0, 100, initialValue);
        slider.setPrefWidth(220);
        slider.setMinHeight(50);
        slider.setMaxHeight(50);
        applyPixelArtSliderSkin(slider);
        return slider;
    }

    /**
     * เปลี่ยน skin ของ Slider:
     * - track ใช้รูป slide.png
     * - thumb (หัวเลื่อน) ใช้รูป sliderNode.png
     */
    private void applyPixelArtSliderSkin(Slider slider) {
        slider.skinProperty().addListener((obs, ov, nv) -> {
            Platform.runLater(() -> {
                try {
                    String trackUrl = getClass()
                            .getResource("/images/settingDecoration/slide.png")
                            .toExternalForm();
                    String thumbUrl = getClass()
                            .getResource("/images/settingDecoration/sliderNode.png")
                            .toExternalForm();

                    Node track = slider.lookup(".track");
                    Node thumb = slider.lookup(".thumb");

                    if (track != null) {
                        track.setStyle(
                                "-fx-background-color: transparent;" +
                                "-fx-background-image: url('" + trackUrl + "');" +
                                "-fx-background-repeat: no-repeat;" +
                                "-fx-background-size: 100% 100%;" +
                                "-fx-pref-height: 28px;"
                        );
                    }
                    if (thumb != null) {
                        thumb.setStyle(
                                "-fx-background-color: transparent;" +
                                "-fx-background-image: url('" + thumbUrl + "');" +
                                "-fx-background-repeat: no-repeat;" +
                                "-fx-background-size: 100% 100%;" +
                                "-fx-pref-width: 38px;" +
                                "-fx-pref-height: 38px;" +
                                "-fx-padding: 19;"
                        );
                    }
                } catch (Exception e) {
                    System.out.println("[Settings] Slider skin failed: " + e.getMessage());
                }
            });
        });
    }

    /** สร้างปุ่มจากรูป pixel art พร้อม hover/press animation */
    private Button createImageButton(String resourcePath, double width) {
        Button btn = new Button();
        try {
            Image img = new Image(getClass().getResourceAsStream(resourcePath));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(width);
            iv.setPreserveRatio(true);
            iv.setSmooth(false);
            btn.setGraphic(iv);
        } catch (Exception e) {
            System.out.println("[Settings] Button image not found: " + resourcePath);
        }

        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
            btn.setCursor(Cursor.HAND);
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
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
