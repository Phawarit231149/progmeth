package gui;

import game.character.Character;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.StageData;

import java.util.Random;

public class GameController extends StackPane {

    private final StageData config;
    private final String element;

    // Game model
    private Character player;
    private int playerRow;
    private int playerCol;
    private Tile[][] map;          // พื้น (Tile, Rock)
    private Seaweed[][] seaweeds;  // seaweed objects (วางทับ tile)
    private boolean[][] hasBomb;   // ช่องไหนมีระเบิด

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

    // ── Images (โหลดจาก resources/) ────────────
    private Image spongebobImg;
    private Image mrKrabImg;
    private Image rockImg;
    private Image seaweedImg;
    private Image bombImg;
    private double cellSize;       // เก็บไว้ใช้กับ ImageView

    public GameController(StageData config, String element) {
        this.config = config;
        this.element = element;

        loadImages();
        createPlayer();
        setupMap();
        setupUI();
        renderGrid();
        startTimer();

        this.setFocusTraversable(true);

        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W: tryMove(-1, 0); break;
                case S: tryMove( 1, 0); break;
                case A: tryMove( 0,-1); break;
                case D: tryMove( 0, 1); break;
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
                case O: explodeBtn.setStyle(normalStyle); break;
                case P: plantBombBtn.setStyle(normalStyle); break;
                default: break;
            }
        });
    }

    // ── Load images safely (null ถ้าไม่เจอ) ─────────────
    private void loadImages() {
        spongebobImg = tryLoad("/sponngebob.png");   // ใช้ชื่อไฟล์ตามที่ใส่ใน resources
        mrKrabImg    = tryLoad("/mrCrab.png");
        rockImg      = tryLoad("/rock.png");
        seaweedImg   = tryLoad("/seaweed.png");
        bombImg      = tryLoad("/bomb.png");
    }

    private Image tryLoad(String resourcePath) {
        try {
            var url = getClass().getResource(resourcePath);
            if (url == null) {
                System.out.println("[Image] NOT FOUND: " + resourcePath);
                return null;
            }
            System.out.println("[Image] Loaded: " + resourcePath);
            return new Image(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("[Image] ERROR: " + resourcePath + " → " + e.getMessage());
            return null;
        }
    }

    // ── สร้าง ImageView ขนาดพอดี cell ─────────────────
    private ImageView makeCellImage(Image img) {
        ImageView iv = new ImageView(img);
        double size = Math.max(cellSize - 4, 8);  // เผื่อขอบนิดหน่อย
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        return iv;
    }

    // ── Create the player character ─────────────────────
    private void createPlayer() {
        // health=5, damage=1, damageBomb=1, bombRange=1, maxBombs=5
        switch (element) {
            case "Fire":     player = new FireCharacter(5, 1, 1, 1, 5); break;
            case "Water":    player = new WaterCharacter(5, 1, 1, 1, 5); break;
            case "Electric":
            default:         player = new ElectricCharacter(5, 1, 1, 1, 5); break;
        }
        playerRow = 0;
        playerCol = 0;
        player.setPos(playerCol, playerRow);
    }

    // ── Setup map with rocks and seaweeds ───────────────
    private void setupMap() {
        int rows = config.getRows();
        int cols = config.getCols();

        map      = new Tile[rows][cols];
        seaweeds = new Seaweed[rows][cols];
        hasBomb  = new boolean[rows][cols];

        // initialize every tile as plain Tile
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                map[r][c] = new Tile(r, c);
            }
        }

        // place rocks (hardcoded for demo)
        int[][] rockPositions = { {2,3}, {2,4}, {3,3}, {5,5}, {5,6}, {7,2}, {7,8} };
        for (int[] p : rockPositions) {
            int r = p[0], c = p[1];
            if (r > 0 && r < rows && c > 0 && c < cols) {
                map[r][c] = new Rock(r, c);
            }
        }

        // place seaweeds (hardcoded for demo)
        int[][] seaweedPositions = { {1,5}, {2,7}, {4,2}, {4,7}, {6,4}, {8,5}, {8,9} };
        for (int[] p : seaweedPositions) {
            int r = p[0], c = p[1];
            if (r > 0 && r < rows && c > 0 && c < cols) {
                seaweeds[r][c] = new Seaweed(r, c);
            }
        }
    }

    // ── Movement ────────────────────────────────────────
    private void tryMove(int dr, int dc) {
        int nr = playerRow + dr;
        int nc = playerCol + dc;

        if (nr < 0 || nr >= config.getRows()) return;
        if (nc < 0 || nc >= config.getCols()) return;
        if (!map[nr][nc].isPassable()) return;          // ติด Rock
        if (seaweeds[nr][nc] != null && !seaweeds[nr][nc].isDestroyed()) return; // ติด Seaweed
        if (hasBomb[nr][nc]) return;                    // ติด Bomb

        playerRow = nr;
        playerCol = nc;
        player.setPos(playerCol, playerRow);
        renderGrid();
    }

    // ── Render the whole grid ───────────────────────────
    private void renderGrid() {
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                styleCell(r, c);
            }
        }
    }

    private void styleCell(int r, int c) {
        Button cell = cells[r][c];
        cell.setText("");
        cell.setGraphic(null);

        // พื้นหลังพื้นปกติทุกครั้ง
        String baseStyle = "-fx-background-color: #dcedc8; -fx-border-color: #aed581;";

        if (r == playerRow && c == playerCol) {
            // Player — ใช้ SpongeBob ถ้ามีรูป (electric), MrKrab ถ้ามี (เผื่อ character อื่น)
            Image playerImg = spongebobImg;
            if (playerImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(playerImg));
            } else {
                cell.setStyle("-fx-background-color: #fff176; -fx-border-color: #f57f17; -fx-font-weight: bold;");
                cell.setText("S");
            }
            return;
        }
        if (hasBomb[r][c]) {
            if (bombImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(bombImg));
            } else {
                cell.setStyle("-fx-background-color: #ef5350; -fx-border-color: #c62828; -fx-font-weight: bold;");
                cell.setText("B");
            }
            return;
        }
        if (map[r][c] instanceof Rock) {
            if (rockImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(rockImg));
            } else {
                cell.setStyle("-fx-background-color: #9e9e9e; -fx-border-color: #424242; -fx-font-weight: bold;");
                cell.setText("R");
            }
            return;
        }
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) {
            if (seaweedImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(seaweedImg));
            } else {
                cell.setStyle("-fx-background-color: #66bb6a; -fx-border-color: #2e7d32; -fx-font-weight: bold;");
                cell.setText("W");
            }
            return;
        }
        // plain tile (or destroyed seaweed → ผ่านได้)
        cell.setStyle(baseStyle);
    }

    // ── Setup UI (top bar / grid / right panel / bottom) ─
    private void setupUI() {
        BorderPane root = new BorderPane();

        root.setTop(buildTopBar());
        root.setRight(buildRightPanel());
        root.setBottom(buildBottomBar());

        root.setPrefSize(900, 600);
        this.getChildren().add(root);

        double rightW = 210;
        double topH   = 80;
        double botH   = 60;

        double availableW = 900 - rightW - 10;
        double availableH = 600 - topH - botH - 10;

        double cs = Math.floor(Math.min(
                availableW / config.getCols(),
                availableH / config.getRows()
        ));
        cs = Math.max(cs, 8);
        this.cellSize = cs;   // เก็บไว้ให้ ImageView ใช้

        StackPane centerHolder = new StackPane(buildGrid(cs));
        centerHolder.setAlignment(Pos.CENTER);
        root.setCenter(centerHolder);
    }

    // ── TOP BAR ─────────────────────────────────────────
    private HBox buildTopBar() {
        killLabel = new Label("0 / " + config.getGoal());
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
        Label skillsInfo = new Label(
                "• Bomb Capacity Up: Increase max \n bombs you can drop.\n" +
                "• Blast Radius: Expand the explosion \n range.\n" +
                "• High Explosive: Increase bomb \n damage dealt to enemies.\n" +
                "• Bubble Shield: Protects you from one instance of damage.\n" +
                "• Quick Heal: Restore your HP \n immediately.");
        skillsInfo.setFont(Font.font(18));

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
                cell.setFocusTraversable(false);  // ไม่ให้ปุ่มดูดโฟกัสจาก WASD
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

    // ── RIGHT PANEL ─────────────────────────────────────
    private HBox buildRightPanel() {
        HBox mainRightContainer = new HBox(40);
        mainRightContainer.setAlignment(Pos.BOTTOM_RIGHT);
        mainRightContainer.setPadding(new Insets(10, 15, 20, 0));

        // คอลัมน์ซ้าย: สำหรับปุ่มระเบิด (Bomb Control)
        VBox leftCol = new VBox(15);
        leftCol.setAlignment(Pos.BOTTOM_CENTER);
        leftCol.getChildren().add(createBombControls());

        VBox rightCol = new VBox(10);
        rightCol.setAlignment(Pos.TOP_CENTER);

        // ใส่ Skills buff
        StackPane s1 = createSkillImage("s1.jpeg");
        StackPane s2 = createSkillImage("s2.jpeg");
        StackPane s3 = createSkillImage("s3.jpeg");
        StackPane s4 = createSkillImage("s4.jpeg");
        StackPane s5 = createSkillImage("s5.jpeg");

        rightCol.getChildren().addAll(s1,s2,s3,s4,s5);
        for (int i = 1; i <= 5; i++) {
            Button skill = new Button("S" + i);
            skill.setPrefSize(54, 54);
            skill.setFocusTraversable(false);
            skill.setStyle("-fx-background-radius: 27; -fx-border-radius: 27; -fx-border-color: #90a4ae; -fx-border-width: 3;");
            rightCol.getChildren().add(skill);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.infoBtn = new Button("i");
        infoBtn.setPrefSize(36, 36);
        infoBtn.setFocusTraversable(false);
        infoBtn.setStyle("-fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e53935; -fx-border-width: 2;");
        infoBtn.setOnAction(e -> { timer.stop(); skillsInformation(); });

        Label info = new Label("[U]");
        info.setFont(Font.font(15));

        rightCol.getChildren().addAll(spacer, infoBtn, info);

        mainRightContainer.getChildren().addAll(leftCol, rightCol);
        return mainRightContainer;
    }

    private VBox createBombControls() {
        VBox bombContainer = new VBox(10);
        bombContainer.setAlignment(Pos.BOTTOM_CENTER);

        this.explodeBtn = new Button("Bomb");
        explodeBtn.setPrefSize(80, 80);
        explodeBtn.setFocusTraversable(false);
        explodeBtn.setStyle(normalStyle);
        explodeBtn.setOnAction(e -> explodeBombs());

        Label explodeKey = new Label("[O]");
        explodeKey.setFont(Font.font(15));

        this.plantBombBtn = new Button("Plant");
        plantBombBtn.setPrefSize(80, 80);
        plantBombBtn.setFocusTraversable(false);
        plantBombBtn.setStyle(normalStyle);
        plantBombBtn.setOnAction(e -> plantBombAtPlayer());

        this.bombLabel = new Label(bombsLeft + " / " + maxBombs);
        bombLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label plantKey = new Label("[P]");
        plantKey.setFont(Font.font(15));

        bombContainer.getChildren().addAll(explodeBtn, explodeKey, plantBombBtn, bombLabel, plantKey);
        return bombContainer;
    }

    public StackPane createSkillImage(String skillName) {
        // 1. โหลดรูปภาพ
        Image img = new Image(getClass().getResourceAsStream("/images/" + skillName));
        ImageView skill = new ImageView(img);

        // 2. ปรับขนาดรูปให้ "เต็ม" พื้นที่ 54x54
        double size = 54.0;
        skill.setFitWidth(size);
        skill.setFitHeight(size);

        // ⭐️ สำคัญ: เปลี่ยนเป็น false เพื่อให้ภาพยืด/หดจนเต็มวงกลมพอดี
        skill.setPreserveRatio(false);

        // 3. ตัดรูปให้เป็นวงกลม (เช็คจุดศูนย์กลางให้แม่น)
        double radius = size / 2;
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(radius, radius, radius);
        skill.setClip(clip);

        // 4. สร้างกรอบ StackPane
        StackPane container = new StackPane(skill);
        container.setPrefSize(size, size);
        container.setMinSize(size, size); // ป้องกัน Layout อื่นมาบีบให้เล็กลง
        container.setMaxSize(size, size);

        // ⭐️ ปรับสไตล์ขอบ
        container.setStyle(
                "-fx-background-radius: " + radius + "; " +
                        "-fx-border-radius: " + radius + "; " +
                        "-fx-border-color: #90a4ae; " +
                        "-fx-border-width: 3;"
        );

        return container;
    }

    // ── BOTTOM HEARTS ─────────────────────────────────────
    // ── BOTTOM HEARTS ───────────────────────────────────
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
            Label h = new Label(i < hearts ? "♥" : "♡");
            h.setStyle("-fx-font-size: 26px; -fx-text-fill: " + (i < hearts ? "#e53935" : "#9e9e9e") + ";");
            heartsBox.getChildren().add(h);
        }
    }

    // ── TIMER ───────────────────────────────────────────
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
            if (timeLeft > 0 && kills > config.getGoal()) {
                if (config.getLevel() == 5) setGameStatus(Status.CLEAR);
                else                        setGameStatus(Status.WIN);
                gameOver();
            }
            if (timeLeft <= 30)
                timerLabel.setStyle(timerLabel.getStyle() + "-fx-text-fill: #e53935;");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // ── PLANT BOMB AT PLAYER POSITION ───────────────────
    private void plantBombAtPlayer() {
        if (bombsLeft <= 0) {
            System.out.println("No bombs left!");
            return;
        }
        if (hasBomb[playerRow][playerCol]) return;   // ช่องนี้มีระเบิดอยู่แล้ว

        hasBomb[playerRow][playerCol] = true;
        bombsLeft--;
        updateBombLabel();
        renderGrid();
        System.out.println("Bomb planted at (" + playerRow + "," + playerCol + ")");
    }

    // ── EXPLODE — bomb 4 directions, range = player.getBombRange() ─
    private void explodeBombs() {
        int range = player.getBombRange();
        boolean[][] toExplode = new boolean[config.getRows()][config.getCols()];

        // ทุกระเบิดบนแผนที่ → ขยายระเบิด 4 ทิศ
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (!hasBomb[r][c]) continue;
                toExplode[r][c] = true;
                int[][] dirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };
                for (int[] d : dirs) {
                    for (int step = 1; step <= range; step++) {
                        int rr = r + d[0]*step;
                        int cc = c + d[1]*step;
                        if (rr < 0 || rr >= config.getRows()) break;
                        if (cc < 0 || cc >= config.getCols()) break;
                        if (map[rr][cc] instanceof Rock) break;       // เจอ Rock → หยุด
                        toExplode[rr][cc] = true;
                        if (seaweeds[rr][cc] != null && !seaweeds[rr][cc].isDestroyed()) {
                            seaweeds[rr][cc].destroy();               // ทำลาย Seaweed → หยุด
                            break;
                        }
                    }
                }
            }
        }

        // ระเบิดทำลายทุกอย่างใน toExplode (ตอนนี้แค่เคลียร์ระเบิดออก)
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (hasBomb[r][c]) hasBomb[r][c] = false;
            }
        }

        bombsLeft = maxBombs;
        updateBombLabel();
        renderGrid();
    }

    private void updateBombLabel() {
        bombLabel.setText(bombsLeft + " / " + maxBombs);
    }

    public void takeDamage() {
        if (hearts > 0) {
            hearts--;
            updateHearts();
        }
        if (hearts <= 0) {
            setGameStatus(Status.LOSE);
            gameOver();
        }
    }

    private void gameOver() {
        GameOverController gameOverController = new GameOverController(gameStatus, config, element);
        this.getScene().setRoot(gameOverController);
    }

    public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }
}
