package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class RuleController extends BorderPane {

    public RuleController() {
        setupBackground();

        // ⭐️ Title

        // ⭐️ 8 rules — สั้นกระชับ
        String[] rules = {
                "        1  Pick a character  Patrick Squidward or spongeBob",
                "        2  Move with W A S D ",
                "        3  Press P to plant bombs  O to detonate",
                "        4  Press K to use your unique skill",
                "        5  Element triangle  FIRE   WATER   ELECTRIC   FIRE",
                "        6  Bombs destroy rocks and seaweed (buffs may drop)",
                "        7  Pick up buffs  heal  shield  range  damage  max bombs",
                "        8  Reach the kill goal before time runs out"
        };

        VBox rulesBox = new VBox(10);
        rulesBox.setAlignment(Pos.CENTER_LEFT);
        for (String r : rules) {
            Label l = new Label(r);
            l.setFont(pixelFont(30));
            l.setStyle("-fx-text-fill: #3b2009");
            rulesBox.getChildren().add(l);
        }

        // กล่องครอบ title + rules — จัดอยู่กลางจอ
        VBox content = new VBox(22, rulesBox);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(100, 30, 30, 30));

        // Back button (X) มุมขวาบน
        Button back = new Button("X");
        back.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #c0392b;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25;" +
                "-fx-min-width: 42;" +
                "-fx-min-height: 42;" +
                "-fx-cursor: hand;"
        );
        back.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        StackPane topPane = new StackPane(back);
        topPane.setPadding(new Insets(15, 15, 0, 0));
        StackPane.setAlignment(back, Pos.TOP_RIGHT);

        this.setTop(topPane);
        this.setCenter(content);
    }

    /** โหลด pixel font จาก /Font/pixelFont.ttf */
    private Font pixelFont(double size) {
        Font f = Font.loadFont(
                getClass().getResourceAsStream("/Font/pixelFont.ttf"), size
        );
        return f != null ? f : Font.font(size);
    }

    private void setupBackground() {
        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream("/images/homeDecoration/Rule.png")
            );
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            1.0, 1.0,
                            true, true,    // ใช้ percentage
                            false, false   // stretch ให้เต็ม window ไม่เหลือขอบ
                    )
            );
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("[Rule] Background load failed: " + e.getMessage());
        }
    }
}
