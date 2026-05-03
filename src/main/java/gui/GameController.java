package gui;

import game.entity.EasyEnemy;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.StageData;

import java.util.Random;

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
    private Status gameStatus;
    private Stage infoPopup;
    private Stage pausePopup;

    // Button
    private Button explodeBtn;
    private Button plantBombBtn;
    private Button infoBtn;

    //Button Style
    private final String normalStyle = "-fx-background-radius: 40;";
    private final String pressedStyle = "-fx-background-radius: 40; -fx-border-color: red; -fx-border-width: 3px; -fx-border-radius: 40;";

    public GameController(StageData config) {
        this.config = config;
        setupUI();
        startTimer();

        this.setFocusTraversable(true);

        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case O:
                    explodeBtn.setStyle(pressedStyle);
                    explodeBtn.fire();
                    break;
                case U:
                    skillsInformation();
                    break;
                case P:
                    plantBombBtn.setStyle(pressedStyle);
                    plantBombBtn.fire();
                    break;
                case ESCAPE:
                    showPauseMenu();
                    break;
                default:
                    break;
            }
        });

        this.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case O:
                    explodeBtn.setStyle(normalStyle);
                    break;
                case P:
                    plantBombBtn.setStyle(normalStyle);
                    break;
                default: break;
            }
        });

    }

    private void setupUI() {
        BorderPane root = new BorderPane();

        root.setTop(buildTopBar());
        root.setRight(buildRightPanel());
        root.setBottom(buildBottomBar());

        root.setPrefSize(900, 600);
        this.getChildren().add(root);

        // ✅ เพิ่ม padding เผื่อให้มากขึ้น
        double rightW = 210;  // เพิ่มจาก 189
        double topH   = 80;   // เพิ่มจาก 62
        double botH   = 60;   // เพิ่มจาก 51

        double availableW = 900 - rightW - 10;
        double availableH = 600 - topH - botH - 10;

        double cellSize = Math.floor(Math.min(
                availableW / config.getCols(),
                availableH / config.getRows()
        ));
        cellSize = Math.max(cellSize, 8);

        StackPane centerHolder = new StackPane(buildGrid(cellSize));
        centerHolder.setAlignment(Pos.CENTER);
        root.setCenter(centerHolder);
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

        Button pauseBtn = new Button("| |");
        /*
        pauseBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #e0e0e0; -fx-background-radius: 8;");
        */
        pauseBtn.setOnAction(e -> {
            timer.stop();
            showPauseMenu();
        });

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox bar = new HBox(10, killLabel, spacer1, timerLabel, spacer2, pauseBtn);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 15, 10, 15));
        return bar;
    }

    private void skillsInformation() {
        if (infoPopup != null && infoPopup.isShowing()) {
            infoPopup.close();
            timer.play();
            return;
        }

        this.infoPopup = new Stage();
        infoPopup.initModality(Modality.APPLICATION_MODAL);
        infoPopup.setTitle("Skills info");

        VBox info = new VBox(20);
        info.setAlignment(Pos.CENTER);
        Label skillsInfo = new Label("Skills Information\nPress 'U' to close");
        info.getChildren().add(skillsInfo);

        Scene infoScene = new Scene(info, 300, 250);

        infoScene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.U) {
                infoPopup.close();
                timer.play();
                this.requestFocus();
            }
        });

        infoPopup.setScene(infoScene);

        timer.stop();

        infoPopup.show();
    }

    private void showPauseMenu() {
        // 1. ถ้าเปิดอยู่แล้ว ให้ปิดและเล่นต่อ (Toggle Off)
        if (pausePopup != null && pausePopup.isShowing()) {
            pausePopup.close();
            timer.play();
            return;
        }

        timer.stop();

        this.pausePopup = new Stage();
        pausePopup.initModality(Modality.APPLICATION_MODAL);
        pausePopup.setTitle("Paused");

        Label pauseLabel = new Label("GAME PAUSED");
        pauseLabel.setFont(Font.font(24));

        Button resumeBtn = new Button("Resume");
        resumeBtn.setPrefWidth(100);
        resumeBtn.setOnAction(e -> {
            pausePopup.close();
            timer.play();
        });

        Button quitBtn = new Button("Quit to home");
        quitBtn.setPrefWidth(100);
        quitBtn.setOnAction(e -> {
            pausePopup.close();
            this.getScene().setRoot(new HomeController());
        });

        VBox pauseLayout = new VBox(20, pauseLabel, resumeBtn, quitBtn);
        pauseLayout.setAlignment(Pos.CENTER);
        pauseLayout.setPadding(new Insets(30));
        pauseLayout.setStyle("-fx-background-color: white; -fx-border-color: black;");

        Scene pauseScene = new Scene(pauseLayout, 300, 250);

        pauseScene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                pausePopup.close();
                timer.play();
                this.requestFocus();
            }
        });

        pausePopup.setScene(pauseScene);

        pausePopup.setOnCloseRequest(e -> timer.play());

        pausePopup.show();
    }


    // ── GRID ─────────────────────────────────────────────
    private GridPane buildGrid(double cellSize) {
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
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setPrefSize(cellSize, cellSize);
                cell.setStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                cell.setOnAction(e -> onCellClick(cell, row, col));
                cells[r][c] = cell;
                grid.add(cell, c, r);
            }
        }

        for (int c = 0; c < config.getCols(); c++) {
            ColumnConstraints cc = new ColumnConstraints(cellSize);
            cc.setHgrow(Priority.NEVER);
            grid.getColumnConstraints().add(cc);
        }
        for (int r = 0; r < config.getRows(); r++) {
            RowConstraints rc = new RowConstraints(cellSize);
            rc.setVgrow(Priority.NEVER);
            grid.getRowConstraints().add(rc);
        }

        return grid;
    }
    private void onCellClick(Button cell, int row, int col) {
        // ถ้าช่องนั้นว่าง และเรายังมีระเบิดเหลือ
        if (cell.getUserData() == null) {
            plantBomb(cell);
        }
        // ถ้าช่องนั้นมีระเบิดอยู่แล้ว ให้เก็บระเบิดคืน (Unplant)
        else if (cell.getUserData().equals("bomb")) {
            removeBomb(cell);
        }
    }

    // ── RIGHT PANEL ───────────────────────────────────────
    private HBox buildRightPanel() {
        HBox mainRightContainer = new HBox(40); // เว้นระยะห่างระหว่างคอลัมน์
        mainRightContainer.setAlignment(Pos.BOTTOM_RIGHT);
        mainRightContainer.setPadding(new Insets(10, 15, 20, 0));

        // คอลัมน์ซ้าย: สำหรับปุ่มระเบิด (Bomb Control)
        VBox leftCol = new VBox(15);
        leftCol.setAlignment(Pos.BOTTOM_CENTER);
        leftCol.getChildren().add(createBombControls()); // นำปุ่มระเบิดมาใส่ที่นี่

        // คอลัมน์ขวา: สำหรับ Skill S1-S5 และปุ่ม Info
        VBox rightCol = new VBox(10);
        rightCol.setAlignment(Pos.TOP_CENTER);

        // ใส่ Skill S1-S5
        for (int i = 1; i <= 5; i++) {
            Button skill = new Button("S" + i);
            skill.setPrefSize(54, 54);
            skill.setStyle("-fx-background-radius: 27; -fx-border-radius: 27; -fx-border-color: #90a4ae; -fx-border-width: 3;");
            rightCol.getChildren().add(skill);
        }

        // ใส่ Spacer ดันปุ่ม Info ลงไปข้างล่าง
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.infoBtn = new Button("i");
        infoBtn.setPrefSize(36, 36);
        infoBtn.setStyle("-fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e53935; -fx-border-width: 2;");
        infoBtn.setOnAction(e -> {
            timer.stop();
            skillsInformation();
        });

        Label info = new Label("[U]");
        info.setFont(Font.font(15));

        rightCol.getChildren().addAll(spacer, infoBtn, info);

        // รวม 2 คอลัมน์เข้าด้วยกัน
        mainRightContainer.getChildren().addAll(leftCol, rightCol);

        return mainRightContainer;
    }

    private VBox createBombControls() {
        VBox bombContainer = new VBox(10);
        bombContainer.setAlignment(Pos.BOTTOM_CENTER);

        // ปุ่มกดระเบิด (Bomb Button)
        this.explodeBtn = new Button("explode");
        explodeBtn.setPrefSize(80, 80);
        explodeBtn.setStyle(normalStyle);

        Label explodeKey = new Label("[O]");
        explodeKey.setFont(Font.font(15));

        explodeBtn.setOnAction(e -> explodeBombs());

        this.plantBombBtn = new Button("Planter");
        plantBombBtn.setPrefSize(80,80);
        plantBombBtn.setStyle(normalStyle);

        // ตัวเลขจำนวนระเบิด (5/5)
        Label plantBombLabel = new Label(bombsLeft + " / " + maxBombs);
        plantBombLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label plantKey = new Label("[P]");
        plantKey.setFont(Font.font(15));

        plantBombBtn.setOnAction(e ->{
            return;
        });


        bombContainer.getChildren().addAll(explodeBtn, explodeKey, plantBombBtn, plantBombLabel, plantKey);
        return bombContainer;
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
            if (timeLeft <= 0) {
                timer.stop();
                setGameStatus(Status.LOSE);
                gameOver();
            }
            timeLeft--;
            int m = timeLeft / 60, s = timeLeft % 60;
            timerLabel.setText(m + ":" + (s < 10 ? "0" : "") + s);
            if(timeLeft > 0 && kills > goal){
                if (config.getLevel() == 5) {setGameStatus(Status.CLEAR);}
                else{setGameStatus(Status.WIN);}
                gameOver();
                }

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

    private void plantBomb(Button cell) {
        if (bombsLeft > 0) {
            // 1. ตั้งค่าสถานะว่าเป็นระเบิด
            cell.setUserData("bomb");

            // 2. เปลี่ยนสีปุ่มเป็นสีแดงตามที่คุณออกแบบไว้
            cell.setStyle("-fx-background-color: #ef5350; -fx-border-color: #c62828;");

            // 3. ลดจำนวนระเบิดในคลัง
            bombsLeft--;

            // 4. อัปเดตตัวเลขบนหน้าจอ (5/5 -> 4/5)
            updateBombLabel();

            System.out.println("Bomb planted! Remaining: " + bombsLeft);
        } else {
            System.out.println("No bombs left!");
            // อาจจะเพิ่ม Effect สั่นหรือเสียงเตือนว่าระเบิดหมดที่นี่
        }
    }

    private void removeBomb(Button cell) {
        // 1. ล้างสถานะระเบิดออก
        cell.setUserData(null);

        // 2. เปลี่ยนสีกลับเป็นสีพื้นหญ้าเดิม
        cell.setStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");

        // 3. เพิ่มจำนวนระเบิดคืนเข้าคลัง (แต่ไม่เกินค่า Max)
        if (bombsLeft < maxBombs) {
            bombsLeft++;
        }

        // 4. อัปเดตตัวเลขบนหน้าจอ
        updateBombLabel();
    }

    private void updateBombLabel() {
        bombLabel.setText(bombsLeft + " / " + maxBombs);
    }

    public void takeDamage() {
        if (hearts > 0) {
            hearts--;
            updateHearts();
        }
        if (hearts <= 0){
            setGameStatus(Status.LOSE);
            gameOver();
        }
    }

    private void gameOver(){
        GameOverController gameOverController = new GameOverController(gameStatus,config);
        this.getScene().setRoot(gameOverController);
    }

    private void spawnEnemy() {
        Random random = new Random();

        int randomX = random.nextInt(config.getCols());
        int randomY = random.nextInt(config.getRows());
        boolean isShielded = random.nextBoolean();

        EasyEnemy slime = new EasyEnemy(1,randomX, randomY,isShielded);

    }

    public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }
}