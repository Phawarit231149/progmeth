package gui;

import game.Element;
import game.character.Character;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import game.util.SoundManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.StageData;

public class GameController extends StackPane {

    private final StageData config;
    private final String name;
    private Element element;

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
    private int maxBombs = 5;
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
    private Image mrKrabsImg;
    private Image squidWardImg;
    private Image rockImg;
    private Image seaweedImg;
    private Image bombImg;
    private double cellSize;       // เก็บไว้ใช้กับ ImageView

    private AudioClip explodeSfx;

    public GameController(StageData config, String name) {
        this.config = config;
        this.name = name;
        if(name.equals("Mr.Krabs")){this.element = Element.FIRE;}
        if(name.equals("Squidward")){this.element = Element.WATER;}
        if(name.equals("SpongeBob")){this.element = Element.ELECTRIC;}

        loadImages();
        loadAudio();
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
        spongebobImg = tryLoadImage("/images/gamePlay/spongebob.png");   // ใช้ชื่อไฟล์ตามที่ใส่ใน resources
        mrKrabsImg    = tryLoadImage("/images/gamePlay/mrKrab.png");
        squidWardImg = tryLoadImage("/images/gamePlay/squidward.png");
        rockImg      = tryLoadImage("/images/gamePlay/rock.png");
        seaweedImg   = tryLoadImage("/images/gamePlay/seaweed.png");
        bombImg      = tryLoadImage("/images/gamePlay/bomb.png");
    }

    private void loadAudio(){
        explodeSfx = tryLoadAudio("/sounds/explosion.mp3");
    }

    private Image tryLoadImage(String resourcePath) {
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

    private AudioClip tryLoadAudio(String resourcePath) {
        try {
            var url = getClass().getResource(resourcePath);
            if (url == null) {
                System.out.println("[Audio] NOT FOUND: " + resourcePath);
                return null;
            }
            System.out.println("[Audio] Loaded: " + resourcePath);
            // ใช้ toExternalForm() เพื่อเปลี่ยน URL เป็น String path ที่ AudioClip เข้าใจ
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("[Audio] ERROR: " + resourcePath + " → " + e.getMessage());
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
            case Element.FIRE:
                player = new FireCharacter(5, 1, 1, 1, 5);
                break;
            case Element.WATER:
                player = new WaterCharacter(5, 1, 1, 1, 5);
                break;
            case Element.ELECTRIC:
                player = new ElectricCharacter(5, 1, 1, 1, 5);
                break;
            default:
                break;
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

        Image playerImg = null;
        if(name.equals("Mr.Krabs")){playerImg  = mrKrabsImg  ;}
        if(name.equals("Squidward")){playerImg = squidWardImg;}
        if(name.equals("SpongeBob")){playerImg = spongebobImg;}

        if (r == playerRow && c == playerCol) {
            // Player — ใช้ SpongeBob ถ้ามีรูป (electric), MrKrab ถ้ามี (เผื่อ character อื่น)
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
                "• Bomb Capacity Up: Increase max\nbombs you can drop.\n\n" +
                "• Blast Radius: Expand the explosion\nrange.\n\n" +
                "• High Explosive: Increase bomb\ndamage dealt to enemies.\n\n" +
                "• Bubble Shield: Protects you from one\ninstance of damage.\n\n" +
                "• Quick Heal: Restore your HP\nfor 1 immediately.");
        skillsInfo.setFont(Font.font(18));

        info.getChildren().add(skillsInfo);

        Scene infoScene = new Scene(info, 400, 380);

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
        ImageView s1 = createSkillImage("buffIcon/increaseMaximumBomb.png");
        ImageView s2 = createSkillImage("buffIcon/increaseBombRange.png");
        ImageView s3 = createSkillImage("buffIcon/increaseBombDamage.png");
        ImageView s4 = createSkillImage("buffIcon/bubbleShield.png");
        ImageView s5 = createSkillImage("buffIcon/heal.png");

        rightCol.getChildren().addAll(s1,s2,s3,s4,s5);

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

    public ImageView createSkillImage(String skillName) {
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

        return skill;
    }

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

            if (hearts == 0){
                setGameStatus(Status.LOSE);
                gameOver();}
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
        boolean takeDamage = false;

        if((bombsLeft < maxBombs) && (explodeSfx != null)){
            SoundManager.playSFX(explodeSfx);
        }

        // 1. คำนวณขอบเขตการระเบิด (โค้ดเดิมของคุณ)
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (!hasBomb[r][c]) continue;
                toExplode[r][c] = true;
                int[][] dirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };
                for (int[] d : dirs) {
                    for (int step = 1; step <= range; step++) {
                        int rr = r + d[0]*step;
                        int cc = c + d[1]*step;
                        if (rr < 0 || rr >= config.getRows() || cc < 0 || cc >= config.getCols()) break;
                        if (map[rr][cc] instanceof Rock) break;
                        toExplode[rr][cc] = true;
                        if (seaweeds[rr][cc] != null && !seaweeds[rr][cc].isDestroyed()) {
                            seaweeds[rr][cc].destroy();
                            break;
                        }
                        if((rr == playerRow && cc == playerCol) || (r == playerRow && c == playerCol)){
                            if(!takeDamage){
                                hearts --;
                                takeDamage = true;
                                updateHearts();
                            }
                        }
                    }
                }
            }
        }

        // 2. เคลียร์สถานะระเบิด
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (hasBomb[r][c]) hasBomb[r][c] = false;
            }
        }

        // อัปเดต UI เบื้องต้น
        bombsLeft = maxBombs;
        updateBombLabel();
        renderGrid();

        // แสดง Effect กะพริบสีแดง
        applyExplosionEffect(toExplode);
    }

    // --- ฟังก์ชันเสริมสำหรับทำ Effect ---
    private void applyExplosionEffect(boolean[][] toExplode) {
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (toExplode[r][c]) {
                    // ✅ เปลี่ยนเป็น cells[r][c] ให้ตรงกับที่ประกาศไว้ด้านบน
                    Button cell = cells[r][c];

                    // เก็บสไตล์ปัจจุบันไว้
                    String originalStyle = cell.getStyle();

                    // ✅ แสดงสีแดงกะพริบ (ใช้สีแดงสดเพื่อให้เห็นชัดบนพื้นเขียว)
                    cell.setStyle("-fx-background-color: rgba(255, 0, 0, 0.8); -fx-border-color: #ff1744;");

                    // หน่วงเวลา 300ms แล้วเปลี่ยนกลับ
                    Timeline flash = new Timeline(new KeyFrame(
                            Duration.millis(300),
                            ae -> cell.setStyle(originalStyle)
                    ));
                    flash.play();
                }
            }
        }
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
        GameOverController gameOverController = new GameOverController(gameStatus, config, name);
        this.getScene().setRoot(gameOverController);
    }

    public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getHearts() {
        return hearts;
    }

    public void setHearts(int hearts) {
        this.hearts = hearts;
    }
}
