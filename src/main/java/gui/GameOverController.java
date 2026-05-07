package gui;

import game.util.SoundManager;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import model.StageData;

public class GameOverController extends BorderPane {

    public GameOverController(Status gameResult, StageData config, String name) {

        Label title = new Label("");
        title.setFont(pixelFont(40));
        title.setPrefSize(500,200);
        title.setAlignment(Pos.CENTER);

        Button nextStage = new Button("Next Stage");
        Button retry = new Button("Retry");
        Button back = new Button("Back to stage");

        // ⭐️ ใช้ pixel font กับปุ่มทั้ง 3
        Font btnFont = pixelFont(20);
        nextStage.setFont(btnFont);
        retry.setFont(btnFont);
        back.setFont(btnFont);

        nextStage.setPrefSize(220,55);
        retry.setPrefSize(220,55);
        back.setPrefSize(220,55);

        // ⭐️ UI sounds
        SoundManager.attachUiSfx(nextStage);
        SoundManager.attachUiSfx(retry);
        SoundManager.attachUiSfx(back);

        nextStage.setOnAction(e -> {
            GameController gameController = new GameController(StageData.ALL_STAGES[config.getLevel()], name);
            this.getScene().setRoot(gameController);
        });

        retry.setOnAction(e -> {
            GameController gameController = new GameController(config, name);
            this.getScene().setRoot(gameController);
        });

        back.setOnAction(e -> {
            StageSelectController stageSelectController = new StageSelectController();
            this.getScene().setRoot(stageSelectController);
        });

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(50));

        if(gameResult.equals(Status.WIN)){
            setupBackground("StageClear.png");
            vbox.getChildren().addAll(nextStage, retry, back);
            this.setCenter(vbox);
            // ⭐️ เสียงชนะ
            SoundManager.stopBGM();
            SoundManager.playOneShot("win.mp3");
        }
        if(gameResult.equals(Status.LOSE)){
            setupBackground("MissionFail.png");
            title.setText("Mission Fail!");
            vbox.getChildren().addAll(retry,back);
            this.setCenter(vbox);
            // ⭐️ เสียงแพ้
            SoundManager.stopBGM();
            SoundManager.playOneShot("lose.mp3");
        }
        if(gameResult.equals(Status.CLEAR)){
            setupBackground("Congratulations.png");
            title.setText("Congratulations!");

            HBox hbox = new HBox(20);
            hbox.setAlignment(Pos.CENTER);
            hbox.setPadding(new Insets(50));
            hbox.getChildren().addAll(retry,back);
            this.setTop(vbox);
            this.setCenter(hbox);
            // ⭐️ เสียงผ่าน stage สุดท้าย (ใช้ win.mp3 เหมือนกัน)
            SoundManager.stopBGM();
            SoundManager.playOneShot("win.mp3");
        }

    }

    /** โหลด pixel font จาก /Font/pixelFont.ttf */
    private Font pixelFont(double size) {
        Font f = Font.loadFont(
                getClass().getResourceAsStream("/Font/pixelFont.ttf"), size
        );
        return f != null ? f : Font.font(size);
    }

    private void setupBackground(String image) {
        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream("/images/homeDecoration/"+image)
            );
            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            1.0, 1.0,
                            true, true,    // ใช้ percentage
                            false, false
            ));
            this.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.out.println("[Home] Background load failed: " + e.getMessage());
        }
    }
}
