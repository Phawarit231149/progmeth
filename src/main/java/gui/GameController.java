package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.CharacterData;
import model.StageData;

public class GameController extends StackPane {

    private final StageData config;

    // game state
    private int hearts = 5;
    private int kills = 0;
    private final int goal = 30;
    private int bombsLeft = 5;
    private final int maxBombs = 5;
    private int timeLeft = 300; // 5 นาที

    // UI refs
    private Label killLabel;
    private Label timerLabel;
    private HBox heartsBox;
    private Label bombLabel;
    private Button[][] cells;
    private Timeline timer;

    public GameController(StageData config) {
        this.config = config;
        setupUI();
        startTimer();
    }

    private void setupUI() {
        BorderPane root = new BorderPane();

        // top bar
        root.setTop(buildTopBar());

        // center grid
        root.setCenter(buildGrid());

        // right panel
        root.setRight(buildRightPanel());

        // bottom hearts
        root.setBottom(buildBottomBar());

        this.getChildren().add(root);
    }

    // ── TOP BAR ──────────────────────────────────────────
    private HBox buildTopBar() {
        killLabel = new Label("0 / " + goal);
        killLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " +
                "-fx-background-color: #fff8e1; -fx-border-color: #f9a825; " +
                "-fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 18;");

        timerLabel = new Label("5:00");
        timerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; " +
                "-fx-background-color: white; -fx-border-color: #90a4ae; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 24;");

        Button exitBtn = new Button("✕");
        exitBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #e0e0e0; -fx-background-radius: 8;");
        exitBtn.setOnAction(e -> {
            timer.stop();
            this.getScene().setRoot(new HomeController());
        });

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox bar = new HBox(10, killLabel, spacer1, timerLabel, spacer2, exitBtn);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 15, 10, 15));
        return bar;
    }

    // ── GRID ─────────────────────────────────────────────
    private GridPane buildGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        cells = new Button[config.getRows()][config.getCols()];

        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                final int row = r, col = c;
                Button cell = new Button();
                cell.setPrefSize(50, 50);
                cell.setStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                cell.setOnAction(e -> onCellClick(cell, row, col));
                cells[r][c] = cell;
                grid.add(cell, c, r);
            }
        }
        return grid;
    }

    private void onCellClick(Button cell, int row, int col) {
        if (cell.getUserData() != null && cell.getUserData().equals("bomb")) {
            if (cell.getUserData() != null && cell.getUserData().equals("bomb")) {
                // เอาบอมบ์ออก
                cell.setUserData(null);
                cell.setStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                bombsLeft++;
            } else {
                if (bombsLeft <= 0) return; // ✅ วางไม่ได้ถ้าหมดแล้ว
                // วางบอมบ์
                cell.setUserData("bomb");
                cell.setStyle("-fx-background-color: #ef5350; -fx-border-color: #c62828;");
                bombsLeft--;
            }
            updateBombLabel();
        }
    }

    // ── RIGHT PANEL ───────────────────────────────────────
    private VBox buildRightPanel() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10, 15, 10, 5));

        // skill slots x5
        for (int i = 1; i <= 5; i++) {
            Button skill = new Button("S" + i);
            skill.setPrefSize(54, 54);
            skill.setStyle("-fx-background-radius: 27; -fx-border-radius: 27; " +
                    "-fx-border-color: #90a4ae; -fx-border-width: 3;");
            panel.getChildren().add(skill);
        }

        // divider
        Region div = new Region();
        div.setPrefHeight(2);
        div.setStyle("-fx-background-color: #546e7a;");
        panel.getChildren().add(div);

        // explode button
        Button explodeBtn = new Button("💥");
        explodeBtn.setPrefSize(58, 58);
        explodeBtn.setStyle("-fx-background-radius: 29; -fx-border-radius: 29; " +
                "-fx-border-color: #e53935; -fx-border-width: 3; -fx-font-size: 20px;");
        explodeBtn.setOnAction(e -> explodeBombs());

        // bomb count button
        bombLabel = new Label(bombsLeft + " / " + maxBombs);
        bombLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button bombBtn = new Button();
        bombBtn.setGraphic(bombLabel);
        bombBtn.setPrefSize(64, 64);
        bombBtn.setStyle("-fx-background-radius: 32; -fx-border-radius: 32; " +
                "-fx-border-color: #37474f; -fx-border-width: 4;");

        // info button
        Button infoBtn = new Button("i");
        infoBtn.setPrefSize(36, 36);
        infoBtn.setStyle("-fx-background-radius: 18; -fx-border-radius: 18; " +
                "-fx-border-color: #e53935; -fx-border-width: 2; " +
                "-fx-font-weight: bold; -fx-text-fill: #e53935;");

        panel.getChildren().addAll(explodeBtn, bombBtn, infoBtn);
        return panel;
    }

    // ── BOTTOM HEARTS ─────────────────────────────────────
    private HBox buildBottomBar() {
        heartsBox = new HBox(8);
        heartsBox.setAlignment(Pos.CENTER);
        heartsBox.setPadding(new Insets(10));
        updateHearts();
        return heartsBox;
    }

    private void updateHearts() {
        heartsBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            Label h = new Label(i < hearts ? "❤️" : "🖤");
            h.setStyle("-fx-font-size: 26px;");
            heartsBox.getChildren().add(h);
        }
    }

    // ── TIMER ─────────────────────────────────────────────
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLeft <= 0) { timer.stop(); return; }
            timeLeft--;
            int m = timeLeft / 60, s = timeLeft % 60;
            timerLabel.setText(m + ":" + (s < 10 ? "0" : "") + s);
            if (timeLeft <= 30)
                timerLabel.setStyle(timerLabel.getStyle() + "-fx-text-fill: #e53935;");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // ── EXPLODE ───────────────────────────────────────────
    private void explodeBombs() {
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (cells[r][c].getUserData() != null &&
                        cells[r][c].getUserData().equals("bomb")) {
                    cells[r][c].setUserData(null);
                    cells[r][c].setStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                    kills = Math.min(kills + 1, goal);
                }
            }
        }
        bombsLeft = maxBombs; // ✅ reset กลับเป็น 5/5
        updateBombLabel();
        killLabel.setText(kills + " / " + goal);
    }

    private void updateBombLabel() {
        bombLabel.setText(bombsLeft + " / " + maxBombs);
    }
    public void takeDamage() {
        if (hearts > 0) {
            hearts--;
            updateHearts();
        }
    }
}