package gui;

import game.Element;
import game.bomb.Bomb;
import game.buff.*;
import game.character.*;
import game.character.Character;
import game.entity.*;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import game.util.ElementUtil;
import game.util.ScoreManager;
import game.util.SoundManager;

import java.util.ArrayList;
import java.util.List;
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
import model.GameProgress;
import model.StageData;

/**
 * GameController is responsible for:
 *   • Wiring JavaFX timers
 *   • Handling keyboard / mouse input
 *   • Rendering the grid
 *   • Delegating everything else to domain classes
 *
 * It does NOT contain movement AI, blast-zone math, spawn logic, or score math.
 */
public class GameController extends StackPane {

    // ═══════════════════════════════════════════════════════════════════════
    // FIELDS
    // ═══════════════════════════════════════════════════════════════════════

    private final StageData config;
    private final String    name;
    private final Element   element;

    // ── Domain objects ────────────────────────────────────────────────────
    private Character    player;
    private int          playerRow;
    private int          playerCol;
    private int          playerDir = 1; // 0=up 1=down 2=left 3=right

    private Tile[][]     map;
    private Seaweed[][]  seaweeds;
    private boolean[][]  hasBomb;
    private Buff[][]     buffMap;

    private final List<Enemy> enemies = new ArrayList<>();
    private EnemySpawner spawner;
    private ScoreManager scoreManager;
    private final ElementUtil elementUtil = new ElementUtil();

    // ── Game state ────────────────────────────────────────────────────────
    private int     hearts    = 5;
    private int     timeLeft  = 300;
    private int     bombsLeft;
    private int     maxBombs;
    private boolean stopCharacter = false;
    private boolean stopEnemy     = false;
    private Status  gameStatus;

    // ── Buff badge counters ───────────────────────────────────────────────
    private int maxBombCount    = 0;
    private int bombRangeCount  = 0;
    private int bombDamageCount = 0;

    // ── Timers ────────────────────────────────────────────────────────────
    private Timeline timer;
    private Timeline enemyTimer;
    private Timeline spawnTimer;
    private Timeline seaweedAnimTimer;
    private int seaweedFrame = 0;

    // ── UI ────────────────────────────────────────────────────────────────
    private Label      killLabel;
    private Label      timerLabel;
    private HBox       heartsBox;
    private Label      bombLabel;
    private Button[][] cells;
    private double     cellSize;

    private Label maxBombBadge;
    private Label bombRangeBadge;
    private Label bombDamageBadge;
    private Label shieldBadge;
    private Label healBadge;

    private Button explodeBtn;
    private Button plantBombBtn;
    private Button skillBtn;
    private Button infoBtn;

    // ⭐️ ปุ่ม transparent แบบเต็มที่ — กันการ shift ตอนกด (default :armed/:pressed ของ JavaFX Button)
    private static final String BTN_TRANSPARENT =
            "-fx-background-color: transparent;" +
            "-fx-background-insets: 0;" +
            "-fx-padding: 0;" +
            "-fx-border-insets: 0;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;";
    private final String normalStyle  = BTN_TRANSPARENT + "-fx-background-radius: 40; -fx-border-radius: 40; -fx-border-color: transparent;";
    private final String pressedStyle = BTN_TRANSPARENT + "-fx-background-radius: 40; -fx-border-color: red; -fx-border-width: 3px; -fx-border-radius: 40;";
    private String baseStyle          = "-fx-background-color: #fff9c4; -fx-border-color: #f9d77a;";

<<<<<<< Updated upstream
=======
    // ── Timers ────────────────────────────────────────────────────────────
    private Timeline timer;
    private Timeline enemyTimer;
    private Timeline spawnTimer;

    // ── Images — directional sprites: index 0=up(Back), 1=down(Front), 2=left, 3=right ──
    private Image[] spongebobImgs;
    private Image[] patrickImgs;
    private Image[] squidWardImgs;
    // Enemy images
    private Image[] npcImgs;             // Easy enemy
    private Image[] mrKrabImgs;          // Medium FIRE
    private Image[] garyImgs;            // Medium WATER
    private Image[] sandyImgs;           // Medium ELECTRIC
    private Image[] kingNeptuneImgs;     // Hard enemy
    private Image spawnImg;
    private Image rockImg;
    private Image woodEdgeImg;           // frame around the player's tile
    private Image mapBgImg;              // Map.png — gameplay background
    // Skill / button icons
    private Image teleportImg;           // Patrick (Fire)
    private Image shieldImg;             // Squidward (Water)
    private Image freezeImg;             // SpongeBob (Electric)
    private Image bombBtnImg;            // plant bomb button
    private Image boomBtnImg;            // explode button (O key)
    private Image currentSkillImg;       // resolved skill icon for current player
    private Image[] seaweedImgs;         // 2 frames for animation
    private int seaweedFrame = 0;        // 0 or 1 — toggled every 0.5 s
    private Timeline seaweedAnimTimer;
    // Player facing direction: 0=up, 1=down, 2=left, 3=right (default facing down)
    private int playerDir = 1;
    private Image bombImg;
    private Image maxBombImg;
    private Image bombRangeImg;
    private Image bombDamageImg;
    private Image bubbleShieldImg;
    private Image healImg;

    // ── Popups ────────────────────────────────────────────────────────────
>>>>>>> Stashed changes
    private Stage infoPopup;
    private Stage pausePopup;

    // ── Images ────────────────────────────────────────────────────────────
    private Image[] spongebobImgs;
    private Image[] patrickImgs;
    private Image[] squidWardImgs;
    private Image[] npcImgs;
    private Image[] mrKrabImgs;
    private Image[] garyImgs;
    private Image[] sandyImgs;
    private Image[] kingNeptuneImgs;
    private Image   spawnImg;
    private Image   rockImg;
    private Image   woodEdgeImg;
    private Image[] seaweedImgs;
    private Image   bombImg;
    private Image   maxBombImg;
    private Image   bombRangeImg;
    private Image   bombDamageImg;
    private Image   bubbleShieldImg;
    private Image   healImg;

    // ── Audio ─────────────────────────────────────────────────────────────
    private AudioClip explodeSfx;
    private AudioClip walkSfx;
    // ติดตามปุ่มเดิน W/A/S/D ที่กดค้าง — ใช้เปิด/ปิดเสียงเดิน
    private final java.util.Set<javafx.scene.input.KeyCode> heldMoveKeys = new java.util.HashSet<>();
    private boolean walkPlaying = false;

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════════

    public GameController(StageData config, String name) {
        this.config  = config;
        this.name    = name;
        this.element = switch (name) {
            case "Patrick"   -> Element.FIRE;
            case "Squidward" -> Element.WATER;
            default          -> Element.ELECTRIC;
        };

        loadImages();
        loadAudio();
        createPlayer();
        setupMap();
        setupEnemies();
        setupUI();
        renderGrid();
        startTimer();
        startEnemyTimer();
        startSpawnTimer();
        startSeaweedAnimation();

        this.setFocusTraversable(true);
        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> { if (!stopCharacter) tryMove(-1,  0); }
                case S -> { if (!stopCharacter) tryMove( 1,  0); }
                case A -> { if (!stopCharacter) tryMove( 0, -1); }
                case D -> { if (!stopCharacter) tryMove( 0,  1); }
                case O -> { explodeBtn.setStyle(pressedStyle); explodeBtn.fire(); }
                case P -> { plantBombBtn.setStyle(pressedStyle); plantBombBtn.fire(); }
                case K -> handleSkillKey();
                case U -> skillsInformation();
                case ESCAPE -> showPauseMenu();
                default -> {}
            }
        });
        this.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case O -> explodeBtn.setStyle(normalStyle);
                case P -> plantBombBtn.setStyle(normalStyle);
                case K -> skillBtn.setStyle(normalStyle);
                default -> {}
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════════════════════

<<<<<<< Updated upstream
=======
    private void loadImages() {
        // Players — directional sprites
        spongebobImgs   = loadDirectional("spongebob",   "Sponge",    "Front");
        patrickImgs     = loadDirectional("patrick",     "Patrick",   "Front");
        squidWardImgs   = loadDirectional("squidword",   "SquidWord", "Font");

        // Enemies — directional sprites
        npcImgs         = loadDirectional("npc",         "Npc",       "Font");
        mrKrabImgs      = loadDirectional("mrkrab",      "MrKrab",    "Font");
        garyImgs        = loadDirectional("gary",        "Gary",      "Font");
        sandyImgs       = loadDirectional("sandy",       "Sandy",     "Font");
        kingNeptuneImgs = loadDirectional("kingneptune", "KingNep",   "Front");

        spawnImg     = tryLoadImage("/images/gamePlay/spawn/spawn.png");
        rockImg      = tryLoadImage("/images/gamePlay/rock/Rock.png");
        woodEdgeImg  = tryLoadImage("/images/gamePlay/WoodEdge/WoodEdge.png");

        // Background ของหน้า gameplay
        mapBgImg     = tryLoadImage("/images/gamePlay/map/Map.png");

        // Skill / button icons
        teleportImg  = tryLoadImage("/images/gamePlay/skill/Teleport.png");
        shieldImg    = tryLoadImage("/images/gamePlay/skill/Shield.png");
        freezeImg    = tryLoadImage("/images/gamePlay/skill/Freeze.png");
        bombBtnImg   = tryLoadImage("/images/gamePlay/skill/Bomb.png");
        boomBtnImg   = tryLoadImage("/images/gamePlay/skill/Boom.png");

        // เลือก skill icon ตามชื่อ character
        currentSkillImg = switch (name) {
            case "Patrick"   -> teleportImg;
            case "Squidward" -> shieldImg;
            case "SpongeBob" -> freezeImg;
            default -> null;
        };

        // Seaweed: 2 frames for animation
        seaweedImgs = new Image[]{
                tryLoadImage("/images/gamePlay/seaweed/seaweed_0.png"),
                tryLoadImage("/images/gamePlay/seaweed/seaweed_1.png")
        };

        bombImg        = tryLoadImage("/images/gamePlay/skill/Bomb.png");
        maxBombImg     = tryLoadImage("/images/buffIcon/increaseMaximumBomb.png");
        bombRangeImg   = tryLoadImage("/images/buffIcon/increaseBombRange.png");
        bombDamageImg  = tryLoadImage("/images/buffIcon/increaseBombDamage.png");
        bubbleShieldImg= tryLoadImage("/images/buffIcon/bubbleShield.png");
        healImg        = tryLoadImage("/images/buffIcon/heal.png");
    }

    /**
     * Loads 4-directional sprites for one character.
     * index: 0=up(Back), 1=down(Front/Font), 2=left, 3=right
     */
    private Image[] loadDirectional(String folder, String prefix, String downSuffix) {
        Image[] arr = new Image[4];
        arr[0] = tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + "Back.png");
        arr[1] = tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + downSuffix + ".png");
        arr[2] = tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + "Left.png");
        arr[3] = tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + "Right.png");
        return arr;
    }

    /** Starts the timer that alternates seaweed frames every 0.5 seconds. */
    private void startSeaweedAnimation() {
        seaweedAnimTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            seaweedFrame = 1 - seaweedFrame;
            for (int r = 0; r < config.getRows(); r++) {
                for (int c = 0; c < config.getCols(); c++) {
                    if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) {
                        styleCell(r, c);
                    }
                }
            }
        }));
        seaweedAnimTimer.setCycleCount(Timeline.INDEFINITE);
        seaweedAnimTimer.play();
    }

    private void loadAudio() {
        explodeSfx = tryLoadAudio("/sounds/explosion.mp3");
        walkSfx    = tryLoadAudio("/sounds/walk.mp3");
        if (walkSfx != null) walkSfx.setCycleCount(AudioClip.INDEFINITE);
        // เปลี่ยน BGM เป็นเพลงในเกม (สลับจาก home.mp3)
        SoundManager.playBGM("spongebobBGM.mp3");
    }

    /** เริ่มเล่นเสียงเดิน loop (ถ้ายังไม่เล่นอยู่) */
    private void startWalkSound() {
        if (walkSfx == null || walkPlaying) return;
        walkSfx.setVolume(SoundManager.getSfxVolume() / 100.0);
        walkSfx.play();
        walkPlaying = true;
    }

    /** หยุดเสียงเดิน */
    private void stopWalkSound() {
        if (walkSfx == null || !walkPlaying) return;
        walkSfx.stop();
        walkPlaying = false;
    }

    private Image tryLoadImage(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) { System.out.println("[Image] NOT FOUND: " + path); return null; }
            return new Image(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("[Image] ERROR: " + path + " → " + e.getMessage());
            return null;
        }
    }

    private AudioClip tryLoadAudio(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) { System.out.println("[Audio] NOT FOUND: " + path); return null; }
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("[Audio] ERROR: " + path + " → " + e.getMessage());
            return null;
        }
    }

    /** Creates an ImageView sized to fit inside a cell. */
    private ImageView makeCellImage(Image img) {
        ImageView iv = new ImageView(img);
        double size = Math.max(cellSize - 4, 8);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        return iv;
    }

    /**
     * Creates the graphic for the player's tile:
     * WoodEdge.png as an outer frame with the player sprite centred on top.
     */
    private StackPane makePlayerCellGraphic(Image playerImg) {
        StackPane stack = new StackPane();

        if (woodEdgeImg != null) {
            ImageView edge = new ImageView(woodEdgeImg);
            double frameSize = Math.max(cellSize - 2, 8);
            edge.setFitWidth(frameSize);
            edge.setFitHeight(frameSize);
            edge.setPreserveRatio(false);
            edge.setSmooth(false);
            stack.getChildren().add(edge);
        }

        if (playerImg != null) {
            ImageView pv = new ImageView(playerImg);
            double playerSize = Math.max(cellSize - 14, 8);
            pv.setFitWidth(playerSize);
            pv.setFitHeight(playerSize);
            pv.setPreserveRatio(true);
            pv.setSmooth(false);
            stack.getChildren().add(pv);
        }

        return stack;
    }

>>>>>>> Stashed changes
    private void createPlayer() {
        player = switch (element) {
            case FIRE     -> new FireCharacter(5, 1, 1, 5);
            case WATER    -> new WaterCharacter(5, 1, 1, 5);
            case ELECTRIC -> new ElectricCharacter(5, 1, 1, 5);
            default -> null;
        };
        int[] spawn = config.getPlayerSpawn();
        playerRow = spawn[0];
        playerCol = spawn[1];
        player.setPos(playerCol, playerRow);
        maxBombs  = player.getMaxBombs();
        bombsLeft = maxBombs;
    }

    private void setupMap() {
        int rows = config.getRows();
        int cols = config.getCols();
        map      = new Tile[rows][cols];
        seaweeds = new Seaweed[rows][cols];
        hasBomb  = new boolean[rows][cols];
        buffMap  = new Buff[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                map[r][c] = new Tile(r, c);
                char ch   = config.tileAt(r, c);
                if (ch == 'R') {
                    map[r][c] = new Rock(r, c);
                } else if (ch == 'S') {
                    seaweeds[r][c] = new Seaweed(r, c);
                    double roll = Math.random();
                    if      (roll < 0.1) buffMap[r][c] = new MaxBombBuff(r, c);
                    else if (roll < 0.2) buffMap[r][c] = new BombRangeBuff(r, c);
                    else if (roll < 0.3) buffMap[r][c] = new BombDamageBuff(r, c);
                    else if (roll < 0.4) buffMap[r][c] = new ShieldBuff(r, c);
                    else if (roll < 0.5) buffMap[r][c] = new HealBuff(r, c);
                }
            }
        }

        // Guarantee at least one of each buff type
        Class<?>[] guaranteed = { MaxBombBuff.class, BombRangeBuff.class,
                BombDamageBuff.class, HealBuff.class, ShieldBuff.class };
        for (Class<?> type : guaranteed) {
            int[] pos = randomFreeBuffTile(rows, cols);
            if (pos == null) continue;
            int r = pos[0], c = pos[1];
            if      (type == MaxBombBuff.class)   buffMap[r][c] = new MaxBombBuff(r, c);
            else if (type == BombRangeBuff.class)  buffMap[r][c] = new BombRangeBuff(r, c);
            else if (type == BombDamageBuff.class) buffMap[r][c] = new BombDamageBuff(r, c);
            else if (type == HealBuff.class)       buffMap[r][c] = new HealBuff(r, c);
            else if (type == ShieldBuff.class)     buffMap[r][c] = new ShieldBuff(r, c);
        }
    }

    private int[] randomFreeBuffTile(int rows, int cols) {
        for (int i = 0; i < 500; i++) {
            int r = (int)(Math.random() * rows);
            int c = (int)(Math.random() * cols);
            if (config.tileAt(r, c) != '.') continue;
            if (map[r][c] instanceof Rock)  continue;
            if (seaweeds[r][c] != null)     continue;
            if (buffMap[r][c]  != null)     continue;
            if (r == playerRow && c == playerCol) continue;
            return new int[]{r, c};
        }
        return null;
    }

    private void setupEnemies() {
        scoreManager = new ScoreManager(config.getGoal());
        spawner = new EnemySpawner(config, enemies, map, seaweeds, hasBomb);
        spawner.setPlayerPos(playerRow, playerCol);
        spawner.setupInitial();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TIMERS
    // ═══════════════════════════════════════════════════════════════════════

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLeft <= 0) { pauseAll(); setGameStatus(Status.LOSE); gameOver(); return; }
            timeLeft--;
            int m = timeLeft / 60, s = timeLeft % 60;
            timerLabel.setText(m + ":" + (s < 10 ? "0" : "") + s);
            if (timeLeft <= 30)
                timerLabel.setStyle(timerLabel.getStyle() + "-fx-text-fill: #e53935;");
            if (scoreManager.hasReachedGoal()) {
                setGameStatus(config.getLevel() == 5 ? Status.CLEAR : Status.WIN);
                gameOver();
            }
            updateSkillButtonUI();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        resumeAll();
    }

    private void startEnemyTimer() {
        enemyTimer = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            spawner.setPlayerPos(playerRow, playerCol);
            for (Enemy en : enemies)
                en.move(map, seaweeds, hasBomb, enemies, playerRow, playerCol,
                        config.getRows(), config.getCols());
            renderGrid();
            checkPlayerEnemyCollision();
        }));
        enemyTimer.setCycleCount(Timeline.INDEFINITE);
        enemyTimer.play();
    }

    private void startSpawnTimer() {
        spawnTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            spawner.setPlayerPos(playerRow, playerCol);
            if (spawner.tick()) renderGrid();
        }));
        spawnTimer.setCycleCount(Timeline.INDEFINITE);
        spawnTimer.play();
    }

    private void startSeaweedAnimation() {
        seaweedAnimTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            seaweedFrame = 1 - seaweedFrame;
            for (int r = 0; r < config.getRows(); r++)
                for (int c = 0; c < config.getCols(); c++)
                    if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed())
                        styleCell(r, c);
        }));
        seaweedAnimTimer.setCycleCount(Timeline.INDEFINITE);
        seaweedAnimTimer.play();
    }

<<<<<<< Updated upstream
    private void pauseAll() {
        if (timer          != null) timer.stop();
        if (enemyTimer     != null) enemyTimer.stop();
        if (spawnTimer     != null) spawnTimer.stop();
    }

    private void resumeAll() {
        if (timer          != null) timer.play();
        if (enemyTimer     != null) enemyTimer.play();
        if (spawnTimer     != null) spawnTimer.play();
=======
    private void resetGridStyle() {
        setBaseStyle("-fx-background-color: #fff9c4; -fx-border-color: #f9d77a;");
        for (int r = 0; r < config.getRows(); r++)
            for (int c = 0; c < config.getCols(); c++)
                cells[r][c].setStyle(baseStyle);
>>>>>>> Stashed changes
    }

    // ═══════════════════════════════════════════════════════════════════════
    // INPUT — MOVEMENT
    // ═══════════════════════════════════════════════════════════════════════

    private void tryMove(int dr, int dc) {
        // Always update facing direction, even when blocked
        if      (dr == -1) playerDir = 0;
        else if (dr ==  1) playerDir = 1;
        else if (dc == -1) playerDir = 2;
        else if (dc ==  1) playerDir = 3;

        int nr = playerRow + dr;
        int nc = playerCol + dc;
        if (nr < 0 || nr >= config.getRows()) { renderGrid(); return; }
        if (nc < 0 || nc >= config.getCols()) { renderGrid(); return; }
        if (!map[nr][nc].isPassable())         { renderGrid(); return; }
        if (seaweeds[nr][nc] != null && !seaweeds[nr][nc].isDestroyed()) { renderGrid(); return; }
        //if (hasBomb[nr][nc])                   { renderGrid(); return; }

        playerRow = nr;
        playerCol = nc;
        player.setPos(playerCol, playerRow);
        spawner.setPlayerPos(playerRow, playerCol);

        // ⭐️ เล่นเสียงเดินทุกครั้งที่ขยับสำเร็จ
        if (walkSfx != null) SoundManager.playSFX(walkSfx);

        if (buffMap[playerRow][playerCol] != null) {
            applyBuffPickup(buffMap[playerRow][playerCol]);
            buffMap[playerRow][playerCol] = null;
        }

        checkPlayerEnemyCollision();
        renderGrid();
    }

    private void applyBuffPickup(Buff buff) {
        buff.apply(player);

        if (buff instanceof MaxBombBuff) {
            maxBombs++;
            bombsLeft++;
            maxBombCount++;
            updateBombLabel();
            updateBadge(maxBombBadge, maxBombCount);
        } else if (buff instanceof BombRangeBuff) {
            bombRangeCount++;
            updateBadge(bombRangeBadge, bombRangeCount);
        } else if (buff instanceof BombDamageBuff) {
            bombDamageCount++;
            updateBadge(bombDamageBadge, bombDamageCount);
        } else if (buff instanceof ShieldBuff) {
            activateShieldBadge();
        } else if (buff instanceof HealBuff) {
            if (hearts < 5) { hearts++; updateHearts(); }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // INPUT — BOMBS
    // ═══════════════════════════════════════════════════════════════════════

    private void plantBombAtPlayer() {
        if (bombsLeft <= 0 || hasBomb[playerRow][playerCol]) return;
        hasBomb[playerRow][playerCol] = true;
        bombsLeft--;
        updateBombLabel();
        renderGrid();
    }

    private void explodeBombs() {
        int rows = config.getRows(), cols = config.getCols();
        boolean playerHit    = false;
        boolean anyBombPlaced = bombsLeft < maxBombs;

        if (anyBombPlaced && explodeSfx != null) SoundManager.playSFX(explodeSfx);

        // Merge blast zones from all placed bombs
        boolean[][] totalZone = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!hasBomb[r][c]) continue;
                Bomb bomb = new Bomb(r, c, player.getBombRange(), player.getDamage(), player);
                boolean[][] zone = bomb.computeBlastZone(map, seaweeds, rows, cols);
                for (int rr = 0; rr < rows; rr++)
                    for (int cc = 0; cc < cols; cc++)
                        if (zone[rr][cc]) totalZone[rr][cc] = true;
            }
        }

        // Player hit?
        if (totalZone[playerRow][playerCol] && !playerHit) {
            playerHit = true;
            applyBlastDamageToPlayer();
        }

        // Enemy hits
        applyBlastDamageToEnemies(totalZone);

        // Reset bomb state
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                hasBomb[r][c] = false;

        bombsLeft = maxBombs;
        updateBombLabel();
        renderGrid();
        applyExplosionEffect(totalZone);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DAMAGE
    // ═══════════════════════════════════════════════════════════════════════

    private void applyBlastDamageToPlayer() {
        if (! player.isImmortal()){
            if (player.hasShield()) {
                player.setShield(false);
                deactivateShieldBadge();
            } else {
                hearts = Math.max(0, hearts - 1);
                updateHearts();
            }
            player.setImmortal(true);
            new Timeline(new KeyFrame(
                    Duration.millis(player.getImmortalDuration()),
                    e -> {player.setImmortal(false);}
            )).play();
        }
    }

    private void applyBlastDamageToEnemies(boolean[][] zone) {
        int rows = config.getRows(), cols = config.getCols();
        var it = enemies.iterator();
        while (it.hasNext()) {
            Enemy en = it.next();
            if (!en.isInBlastZone(zone, rows, cols)) continue;
            if (en.isShielded()) { en.setShielded(false); continue; }

            int dmg = elementUtil.calculateCharacterDamage(player, en);
            en.setHealth(en.getHealth() - dmg);

            if (en.getHealth() <= 0) {
                it.remove();
                scoreManager.addKill();
                updateKillLabel();
                if (scoreManager.hasReachedGoal()) {
                    setGameStatus(config.getLevel() == 5 ? Status.CLEAR : Status.WIN);
                    gameOver();
                    return;
                }
                // Stage 5 phase-2 trigger
                if (config.getLevel() == 5 && !spawner.isPhase2Started()
                        && scoreManager.getScore() >= 25) {
                    triggerPhase2();
                }
            }
        }
        updateKillLabel();
    }

    private void checkPlayerEnemyCollision() {
        for (Enemy e : enemies) {
            if (e.occupiesTile(playerRow, playerCol)) {
                damageFromEnemy(e);
                return;
            }
        }
    }

<<<<<<< Updated upstream
    private void damageFromEnemy(Enemy en) {
        if(! player.isImmortal()){
            if (player.hasShield()) {
                player.setShield(false);
                deactivateShieldBadge();
                return;
=======
    // ═══════════════════════════════════════════════════════════════════════
    // ENEMY SETUP & AI
    // ═══════════════════════════════════════════════════════════════════════

    private void setupEnemies() {
        enemies       = new ArrayList<>();
        totalSpawned  = 0;
        stagePhase    = 1;
        phase2Started = false;

        switch (config.getLevel()) {
            case 1 -> spawnInitialEasy(5);
            case 2 -> spawnInitialMedium(7, false);
            case 3 -> spawnInitialMedium(5, true);
            case 4 -> spawnInitialMedium(7, true);
            case 5 -> spawnInitialMedium(10, false);
        }
    }

    private void spawnInitialEasy(int count) {
        for (int i = 0; i < count; i++) {
            int[] pos = randomWalkableNotNearPlayer(3);
            if (pos == null) break;
            enemies.add(new EasyEnemy(1, pos[1], pos[0], false));
            totalSpawned++;
        }
    }

    private void spawnInitialMedium(int count, boolean someShielded) {
        for (int i = 0; i < count; i++) {
            int[] pos = randomWalkableNotNearPlayer(3);
            if (pos == null) break;
            enemies.add(new MediumEnemy(1, pos[1], pos[0], randomMediumElement(), someShielded && Math.random() < 0.4));
            totalSpawned++;
        }
    }

    private void startSpawnTimer() {
        spawnTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> tickSpawn()));
        spawnTimer.setCycleCount(Timeline.INDEFINITE);
        spawnTimer.play();
    }

    private void tickSpawn() {
        int level = config.getLevel();
        if (level == 5 && stagePhase == 2) {
            if (totalSpawned < 30) spawnHardRandom();
            return;
        }
        if (totalSpawned < phase1Cap(level)) {
            spawnMediumAtSpawnPoint(level == 3 || level == 4);
        }
    }

    private int phase1Cap(int level) {
        return switch (level) { case 1 -> 10; case 2 -> 15; case 3 -> 20; case 4 -> 25; default -> 25; };
    }

    private void spawnMediumAtSpawnPoint(boolean canShield) {
        List<int[]> spawns = new ArrayList<>(config.getEnemySpawns());
        if (spawns.isEmpty()) {
            int[] pos = randomWalkableNotNearPlayer(3);
            if (pos != null) addMedium(pos[0], pos[1], canShield);
            return;
        }
        Collections.shuffle(spawns);
        for (int[] s : spawns) {
            if (isTileFreeForSpawn(s[0], s[1])) { addMedium(s[0], s[1], canShield); return; }
        }
    }

    private void addMedium(int row, int col, boolean canShield) {
        enemies.add(new MediumEnemy(1, col, row, randomMediumElement(), canShield && Math.random() < 0.4));
        totalSpawned++;
        renderGrid();
    }

    private void spawnHardRandom() {
        int rows = config.getRows(), cols = config.getCols();
        for (int i = 0; i < 300; i++) {
            int r = (int)(Math.random() * (rows - 1));
            int c = (int)(Math.random() * (cols - 1));
            if (!canHardOccupy(r, c)) continue;
            if (Math.abs(r - playerRow) + Math.abs(c - playerCol) < 3) continue;
            enemies.add(new HardEnemy(2, c, r, randomHardElement(), false));
            totalSpawned++;
            renderGrid();
            return;
        }
    }

    private void triggerStage5Phase2() {
        if (config.getLevel() != 5 || phase2Started) return;
        phase2Started = true;
        stagePhase    = 2;

        int rows = config.getRows(), cols = config.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] instanceof Rock) map[r][c] = new Tile(r, c);
                if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) seaweeds[r][c].destroy();
>>>>>>> Stashed changes
            }
            int dmg = elementUtil.calculateEnemyDamage(en, player);
            hearts = Math.max(0, hearts - dmg);
            updateHearts();

            player.setImmortal(true);
            new Timeline(new KeyFrame(
                    Duration.millis(player.getImmortalDuration()),
                    e -> {player.setImmortal(false);}
            )).play();
        }
    }

    private void triggerPhase2() {
        if (!spawner.triggerPhase2()) return;

        // Re-wire spawn timer to 30-second interval for phase 2
        spawnTimer.stop();
        spawnTimer.getKeyFrames().setAll(new KeyFrame(Duration.seconds(30), e -> {
            spawner.setPlayerPos(playerRow, playerCol);
            if (spawner.tick()) renderGrid();
        }));
        spawnTimer.play();
        renderGrid();
    }

<<<<<<< Updated upstream
=======
    private void startEnemyTimer() {
        enemyTimer = new Timeline(new KeyFrame(Duration.millis(600), e -> {
            for (Enemy en : enemies) moveEnemy(en);
            renderGrid();
            checkPlayerEnemyCollision();
        }));
        enemyTimer.setCycleCount(Timeline.INDEFINITE);
        enemyTimer.play();
    }

    private void moveEnemy(Enemy e) {
        if (stopEnemy) return;
        if (e.getLevel() == Level.HARD) moveHardFollowPlayer(e);
        else moveRandom(e);
    }

    private void moveRandom(Enemy e) {
        int dir = e.getCurrentDir();
        int r = e.getPosY(), c = e.getPosX();

        int nr = r + DR[dir], nc = c + DC[dir];
        if (canEnemyWalk(nr, nc, e)) {
            e.setCurrentDir(dir);
            e.setPosY(nr);
            e.setPosX(nc);
            return;
        }

        int reverse = dir ^ 1;
        List<Integer> options = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            if (d == dir || d == reverse) continue;
            if (canEnemyWalk(r + DR[d], c + DC[d], e)) options.add(d);
        }

        int newDir;
        if (!options.isEmpty()) {
            newDir = options.get((int)(Math.random() * options.size()));
        } else if (canEnemyWalk(r + DR[reverse], c + DC[reverse], e)) {
            newDir = reverse;
        } else {
            return;
        }
        e.setCurrentDir(newDir);
        e.setPosY(r + DR[newDir]);
        e.setPosX(c + DC[newDir]);
    }

    private void moveHardFollowPlayer(Enemy e) {
        int r = e.getPosY(), c = e.getPosX();
        int bestDir = -1, bestDist = Integer.MAX_VALUE;

        int[] order = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int t = order[i]; order[i] = order[j]; order[j] = t;
        }

        for (int d : order) {
            int nr = r + DR[d], nc = c + DC[d];
            if (!canHardWalk(nr, nc, e)) continue;
            int dist = Math.abs(nr - playerRow) + Math.abs(nc - playerCol);
            if (dist < bestDist) { bestDist = dist; bestDir = d; }
        }

        if (bestDir >= 0) {
            e.setCurrentDir(bestDir);
            e.setPosY(r + DR[bestDir]);
            e.setPosX(c + DC[bestDir]);
        }
    }

    // ── Enemy position helpers ─────────────────────────────────────────────

    private boolean enemyOccupiesTile(Enemy e, int r, int c) {
        int er = e.getPosY(), ec = e.getPosX();
        if (e.getLevel() == Level.HARD) return (r == er || r == er + 1) && (c == ec || c == ec + 1);
        return r == er && c == ec;
    }

    /** Returns the enemy occupying tile (r,c), or null if none. */
    private Enemy enemyAt(int r, int c) {
        for (Enemy e : enemies) if (enemyOccupiesTile(e, r, c)) return e;
        return null;
    }

    private boolean canEnemyWalk(int r, int c, Enemy self) {
        if (r < 0 || r >= config.getRows() || c < 0 || c >= config.getCols()) return false;
        if (map[r][c] instanceof Rock) return false;
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) return false;
        if (hasBomb[r][c]) return false;
        for (Enemy o : enemies) if (o != self && enemyOccupiesTile(o, r, c)) return false;
        return true;
    }

    private boolean canHardWalk(int nr, int nc, Enemy self) {
        int rows = config.getRows(), cols = config.getCols();
        if (nr < 0 || nr + 1 >= rows || nc < 0 || nc + 1 >= cols) return false;
        for (int dr = 0; dr < 2; dr++)
            for (int dc = 0; dc < 2; dc++)
                if (!canEnemyWalk(nr + dr, nc + dc, self)) return false;
        return true;
    }

    private boolean isTileFreeForSpawn(int r, int c) {
        if (r < 0 || r >= config.getRows() || c < 0 || c >= config.getCols()) return false;
        if (map[r][c] instanceof Rock) return false;
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) return false;
        if (hasBomb[r][c]) return false;
        if (r == playerRow && c == playerCol) return false;
        for (Enemy e : enemies) if (enemyOccupiesTile(e, r, c)) return false;
        return true;
    }

    private boolean canHardOccupy(int r, int c) {
        for (int dr = 0; dr < 2; dr++)
            for (int dc = 0; dc < 2; dc++)
                if (!isTileFreeForSpawn(r + dr, c + dc)) return false;
        return true;
    }

    private int[] randomWalkableNotNearPlayer(int minDist) {
        int rows = config.getRows(), cols = config.getCols();
        for (int i = 0; i < 200; i++) {
            int r = (int)(Math.random() * rows);
            int c = (int)(Math.random() * cols);
            if (!isTileFreeForSpawn(r, c)) continue;
            if (Math.abs(r - playerRow) + Math.abs(c - playerCol) < minDist) continue;
            return new int[]{r, c};
        }
        return null;
    }

    private Element randomElement() {
        Element[] e = {Element.FIRE, Element.WATER, Element.ELECTRIC};
        return e[(int)(Math.random() * e.length)];
    }

    /**
     * เลือก element ของ MEDIUM enemy ตาม stage ปัจจุบัน:
     *  - Stage 1: WATER 100%
     *  - Stage 2: ELECTRIC 70% / FIRE 30%
     *  - Stage 3: FIRE 70% / WATER 30%
     *  - Stage 4: WATER 70% / ELECTRIC 30%
     *  - Stage 5: เท่ากันทุก element (33/33/33)
     * และพยายามไม่ให้ element ซ้ำกับตัวที่ spawn ก่อนหน้า (จะลองสุ่มใหม่ 1 ครั้ง)
     */
    private Element lastSpawnedElement = null;

    private Element randomMediumElement() {
        int stage = config.getLevel();
        Element[] choices;
        double[] weights;

        switch (stage) {
            case 1:
                // ทุกตัวเป็น WATER → ไม่ต้องสุ่ม
                lastSpawnedElement = Element.WATER;
                return Element.WATER;
            case 2:
                choices = new Element[]{Element.ELECTRIC, Element.FIRE};
                weights = new double[]{0.7, 0.3};
                break;
            case 3:
                choices = new Element[]{Element.FIRE, Element.WATER};
                weights = new double[]{0.7, 0.3};
                break;
            case 4:
                choices = new Element[]{Element.WATER, Element.ELECTRIC};
                weights = new double[]{0.7, 0.3};
                break;
            case 5:
            default:
                // เท่าๆ กันทุก element
                choices = new Element[]{Element.FIRE, Element.WATER, Element.ELECTRIC};
                weights = new double[]{1.0/3, 1.0/3, 1.0/3};
                break;
        }

        Element picked = pickWeighted(choices, weights);
        // หลีกเลี่ยง element ซ้ำกับตัวก่อน (ลองสุ่มใหม่ 1 ครั้ง)
        if (picked == lastSpawnedElement && choices.length > 1) {
            Element retry = pickWeighted(choices, weights);
            if (retry != lastSpawnedElement) picked = retry;
        }
        lastSpawnedElement = picked;
        return picked;
    }

    /**
     * Hard enemy (Stage 5 phase 2) ยัง random ทุก element แต่กันการซ้ำติดกัน
     */
    private Element randomHardElement() {
        Element[] choices = {Element.FIRE, Element.WATER, Element.ELECTRIC};
        Element picked;
        int attempts = 0;
        do {
            picked = choices[(int)(Math.random() * choices.length)];
            attempts++;
        } while (picked == lastSpawnedElement && attempts < 3);
        lastSpawnedElement = picked;
        return picked;
    }

    /** สุ่ม element ตาม weight (probabilities) */
    private Element pickWeighted(Element[] choices, double[] weights) {
        double r = Math.random();
        double sum = 0;
        for (int i = 0; i < choices.length; i++) {
            sum += weights[i];
            if (r < sum) return choices[i];
        }
        return choices[choices.length - 1];
    }

    /** Returns the correct directional image for an enemy based on its level, element and current direction. */
    private Image enemyImage(Enemy e) {
        int dir = clampDir(e.getCurrentDir());
        Image[] set;
        Level lvl = e.getLevel();
        if (lvl == null) {
            set = npcImgs;
        } else {
            set = switch (lvl) {
                case EASY -> npcImgs;
                case HARD -> kingNeptuneImgs;
                case MEDIUM -> {
                    if (e.getElement() == null) yield mrKrabImgs;
                    yield switch (e.getElement()) {
                        case WATER    -> garyImgs;
                        case ELECTRIC -> sandyImgs;
                        default       -> mrKrabImgs;
                    };
                }
            };
        }
        return pickDirImage(set, dir);
    }

    /**
     * Picks from a 4-directional image array.
     * Falls back to the first non-null image if the requested direction is missing.
     */
    private Image pickDirImage(Image[] set, int dir) {
        if (set == null) return null;
        if (dir < 0 || dir > 3) dir = 1;
        if (set[dir] != null) return set[dir];
        for (int d : new int[]{1, 0, 2, 3}) {
            if (set[d] != null) return set[d];
        }
        return null;
    }

    private int clampDir(int dir) {
        return (dir < 0 || dir > 3) ? 1 : dir;
    }

>>>>>>> Stashed changes
    // ═══════════════════════════════════════════════════════════════════════
    // INPUT — SKILLS
    // ═══════════════════════════════════════════════════════════════════════

    private void handleSkillKey() {
        if (player.isSkillReady()) skillBtn.setStyle(pressedStyle);

        if (player instanceof FireCharacter fire && fire.isSkillReady()) {
            activateFireTeleport(fire);
        } else if (player instanceof WaterCharacter water && water.isSkillReady()) {
            activateWaterShield(water);
        } else if (player instanceof ElectricCharacter electric && electric.isSkillReady()) {
            activateElectricStun(electric);
        }
    }

    private void activateFireTeleport(FireCharacter fire) {
        for(Enemy enemy:enemies){enemy.setFreezed(true);}
        stopCharacter = true;
        timer.stop();
        fire.useSkill();
        setGridStyle("-fx-background-color: #cfd8dc; -fx-border-color: #b0bec5;");

        new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            if (fire.isTeleportArmed()) {
                fire.cancelTeleport();
                for(Enemy enemy:enemies){enemy.setFreezed(false);}
                stopCharacter = false;
                timer.play();
                resetGridStyle();
            }
        })).play();
    }

    private void activateWaterShield(WaterCharacter water) {
        water.useSkill();
        styleCell(playerRow, playerCol);
        skillBtn.setDisable(true);
        activateShieldBadge();
    }

    private void activateElectricStun(ElectricCharacter electric) {
        for(Enemy enemy:enemies){enemy.setFreezed(true);}
        setGridStyle("-fx-background-color: #cfd8dc; -fx-border-color: #b0bec5;");
        new Timeline(new KeyFrame(
                Duration.millis(ElectricCharacter.getStunDurationMs()),
                e -> {
                    for(Enemy enemy:enemies) {enemy.setFreezed(false);}
                    resetGridStyle();
                }
        )).play();
        electric.useSkill();
    }

    private void handleCellClick(int row, int col) {
        if (!(player instanceof FireCharacter fire) || !fire.isTeleportArmed()) return;
        Tile target = map[row][col];
        if (seaweeds[row][col] != null && !seaweeds[row][col].isDestroyed())
            target = seaweeds[row][col];

        if (fire.teleportTo(col, row, target, false)) {
            playerRow = row; playerCol = col;
            spawner.setPlayerPos(playerRow, playerCol);
            renderGrid();
            resetGridStyle();
        }
        for(Enemy enemy:enemies){enemy.setFreezed(false);}
        stopCharacter = false;
        timer.play();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SHIELD BADGE
    // ═══════════════════════════════════════════════════════════════════════

    private void activateShieldBadge() {
        shieldBadge.setText("✓");
        shieldBadge.setStyle(ShieldBadgeStyle.ACTIVE);
        shieldBadge.setVisible(true);
    }

    private void deactivateShieldBadge() {
        shieldBadge.setText("✗");
        shieldBadge.setStyle(ShieldBadgeStyle.INACTIVE);
        shieldBadge.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UI SETUP
    // ═══════════════════════════════════════════════════════════════════════

    private void setupUI() {
        double rightW = 210, topH = 80, botH = 60;
        double cs = Math.max(Math.floor(Math.min(
                (900 - rightW - 10) / config.getCols(),
                (600 - topH - botH - 10) / config.getRows())), 8);
        this.cellSize = cs;

        BorderPane root = new BorderPane();
        root.setTop(buildTopBar());
        root.setRight(buildRightPanel());
        root.setBottom(buildBottomBar());
        root.setCenter(new StackPane(buildGrid(cs)));
        root.setPrefSize(900, 600);

        // ⭐️ Map.png เป็น background ของ gameplay area
        if (mapBgImg != null) {
            BackgroundImage bgImage = new BackgroundImage(
                    mapBgImg,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO, BackgroundSize.AUTO,
                            false, false, true, true   // cover เต็ม
                    )
            );
            this.setBackground(new Background(bgImage));
        }

        this.getChildren().add(root);
    }

    private HBox buildTopBar() {
        killLabel = new Label(scoreManager.formatProgress());
        killLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " +
                "-fx-background-color: #fff8e1; -fx-border-color: #f9a825; " +
                "-fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 6 18;");

        timerLabel = new Label("5:00");
        timerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; " +
                "-fx-background-color: white; -fx-border-color: #90a4ae; " +
                "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 6 24;");

        Button pauseBtn = new Button("| |");
        pauseBtn.setOnAction(e -> { pauseAll(); showPauseMenu(); });

        Region sp1 = new Region(), sp2 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        HBox.setHgrow(sp2, Priority.ALWAYS);

        HBox bar = new HBox(10, killLabel, sp1, timerLabel, sp2, pauseBtn);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 15, 10, 15));
        return bar;
    }

    private GridPane buildGrid(double cs) {
        GridPane grid = new GridPane();
        grid.setHgap(2); grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        cells = new Button[config.getRows()][config.getCols()];
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                final int row = r, col = c;
                Button cell = new Button();
                cell.setMinSize(cs, cs); cell.setMaxSize(cs, cs); cell.setPrefSize(cs, cs);
                cell.setFocusTraversable(false);
                cell.setOnAction(e -> handleCellClick(row, col));
                cells[r][c] = cell;
                grid.add(cell, c, r);
            }
        }
        for (int c = 0; c < config.getCols(); c++) {
            ColumnConstraints cc = new ColumnConstraints(cs); cc.setHgrow(Priority.NEVER);
            grid.getColumnConstraints().add(cc);
        }
        for (int r = 0; r < config.getRows(); r++) {
            RowConstraints rc = new RowConstraints(cs); rc.setVgrow(Priority.NEVER);
            grid.getRowConstraints().add(rc);
        }
        return grid;
    }

    private HBox buildRightPanel() {
        maxBombBadge    = createBadgeLabel();
        bombRangeBadge  = createBadgeLabel();
        bombDamageBadge = createBadgeLabel();
        shieldBadge     = createBadgeLabel();
        healBadge       = createBadgeLabel();
        deactivateShieldBadge();

        VBox iconCol = new VBox(10);
        iconCol.setAlignment(Pos.TOP_CENTER);
        iconCol.getChildren().addAll(
                createSkillWithBadge("buffIcon/increaseMaximumBomb.png", maxBombBadge),
                createSkillWithBadge("buffIcon/increaseBombRange.png",   bombRangeBadge),
                createSkillWithBadge("buffIcon/increaseBombDamage.png",  bombDamageBadge),
                createSkillWithBadge("buffIcon/bubbleShield.png",        shieldBadge),
                createSkillWithBadge("buffIcon/heal.png",                healBadge)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.infoBtn = new Button("i");
        infoBtn.setPrefSize(36, 36);
        infoBtn.setFocusTraversable(false);
        infoBtn.setStyle("-fx-background-radius: 18; -fx-border-radius: 18; " +
                "-fx-border-color: #e53935; -fx-border-width: 2;");
        infoBtn.setOnAction(e -> { pauseAll(); skillsInformation(); });

        iconCol.getChildren().addAll(spacer, infoBtn, new Label("[U]"));

        HBox panel = new HBox(40, createBombControls(), iconCol);
        panel.setAlignment(Pos.BOTTOM_RIGHT);
        panel.setPadding(new Insets(10, 15, 20, 0));
        return panel;
    }

    /**
     * ใส่รูป icon ลงในปุ่ม — รูปจะ zoom เต็มวงกลมโดยใช้ circular clip
     * (corners ของรูปสี่เหลี่ยมจะถูกตัดออก เหลือเฉพาะวงกลม → ไม่เห็นพื้นหลังขาวรอบรูปแล้ว)
     */
    private void applyImageGraphic(Button btn, Image img, double size) {
        if (img == null) return;
        ImageView iv = new ImageView(img);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(false);   // ยืดให้เต็ม 80×80
        iv.setSmooth(false);

        // ตัดให้เป็นวงกลม — มุมสี่เหลี่ยมของรูปจะหายไป
        double r = size / 2.0;
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(r, r, r);
        iv.setClip(clip);

        btn.setGraphic(iv);
        btn.setText("");
        btn.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        // ใช้ normalStyle (transparent + radius 40) เพื่อกันการ shift ตอนกด
        btn.setStyle(normalStyle);
    }

    private VBox createBombControls() {
        this.skillBtn = new Button();
        skillBtn.setPrefSize(80, 80);
        skillBtn.setFocusTraversable(false);
        skillBtn.setStyle(normalStyle);
        skillBtn.setOnAction(e -> handleSkillKey());
        applyImageGraphic(skillBtn, currentSkillImg, 80);

        String skillText = switch (name) {
<<<<<<< Updated upstream
            case "Patrick"   -> "Teleport\n[K]";
            case "Squidward" -> "Generate\nshield [K]";
            default          -> "Freeze all\nenemies [K]";
=======
            case "Patrick"   -> "Teleport [K]";
            case "Squidward" -> "Shield [K]";
            case "SpongeBob" -> "Freeze all [K]";
            default -> null;
>>>>>>> Stashed changes
        };
        Label skillLabel = makeHintLabel(skillText, 13);

        this.explodeBtn = new Button();
        explodeBtn.setPrefSize(80, 80);
        explodeBtn.setFocusTraversable(false);
        explodeBtn.setStyle(normalStyle);
        explodeBtn.setOnAction(e -> explodeBombs());
        applyImageGraphic(explodeBtn, boomBtnImg, 80);

        this.plantBombBtn = new Button();
        plantBombBtn.setPrefSize(80, 80);
        plantBombBtn.setFocusTraversable(false);
        plantBombBtn.setStyle(normalStyle);
        plantBombBtn.setOnAction(e -> plantBombAtPlayer());
        applyImageGraphic(plantBombBtn, bombBtnImg, 80);

        this.bombLabel = makeHintLabel(bombsLeft + " / " + maxBombs, 16);
        Label oLabel = makeHintLabel("[O]", 14);
        Label pLabel = makeHintLabel("[P]", 14);

        VBox box = new VBox(10, skillBtn, skillLabel, explodeBtn, oLabel,
                plantBombBtn, bombLabel, pLabel);
        box.setAlignment(Pos.BOTTOM_CENTER);
        return box;
    }

    /**
     * สร้าง Label ที่มีพื้นหลังขาวเป็น chip — ตัวอักษรอ่านง่ายแม้อยู่บน background สีฉูดฉาด
     */
    private Label makeHintLabel(String text, double fontSize) {
        Label l = new Label(text);
        l.setFont(Font.font(fontSize));
        l.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #1a1a1a;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 3 10 3 10;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #b0b0b0;" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;"
        );
        return l;
    }

    private HBox buildBottomBar() {
        heartsBox = new HBox(8);
        heartsBox.setAlignment(Pos.CENTER);
        heartsBox.setPadding(new Insets(10));
        updateHearts();
        return heartsBox;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // POPUPS
    // ═══════════════════════════════════════════════════════════════════════

    private void skillsInformation() {
        if (infoPopup != null && infoPopup.isShowing()) { infoPopup.close(); resumeAll(); return; }
        infoPopup = new Stage();
        infoPopup.initModality(Modality.APPLICATION_MODAL);
        infoPopup.setTitle("Skills info");
        Label text = new Label(
                "• Bomb Capacity Up: Increase max\nbombs you can drop.\n\n" +
                        "• Blast Radius: Expand the explosion\nrange.\n\n" +
                        "• High Explosive: Increase bomb\ndamage dealt to enemies.\n\n" +
                        "• Bubble Shield: Protects you from one\ninstance of damage.\n\n" +
                        "• Quick Heal: Restore your HP\nfor 1 immediately.");
        text.setFont(Font.font(18));
        VBox layout = new VBox(20, text);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 400, 380);
        scene.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.U) {
                infoPopup.close(); resumeAll(); this.requestFocus();
            }
        });
        infoPopup.setScene(scene);
        pauseAll();
        infoPopup.show();
    }

    private void showPauseMenu() {
        if (pausePopup != null && pausePopup.isShowing()) { pausePopup.close(); resumeAll(); return; }
        pauseAll();
        pausePopup = new Stage();
        pausePopup.initModality(Modality.APPLICATION_MODAL);
        pausePopup.setTitle("Paused");

        // ⭐️ ปุ่ม Resume — text button with pixel font (เหมือน GameOver)
        Button resumeBtn = new Button("Resume");
        resumeBtn.setFont(pauseMenuFont(20));
        resumeBtn.setPrefSize(220, 55);
        resumeBtn.setOnAction(e -> { pausePopup.close(); resumeAll(); });

        // ⭐️ ปุ่ม Quit to Home — text button with pixel font
        Button quitBtn = new Button("Quit to Home");
        quitBtn.setFont(pauseMenuFont(20));
        quitBtn.setPrefSize(220, 55);
        quitBtn.setOnAction(e -> { pausePopup.close(); this.getScene().setRoot(new HomeController()); });

        // ขนาด popup
        final double POPUP_W = 600;
        final double POPUP_H = 400;

        VBox btnBox = new VBox(20, resumeBtn, quitBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(75, 0, 0, 0));

        // ⭐️ StackPane root — ใช้ ImageView stretch เต็ม popup เป็นพื้นหลัง
        StackPane root = new StackPane();
        Image pauseBg = tryLoadImage("/images/gamePlay/PauseGame/GamePause.png");
        if (pauseBg != null) {
            ImageView bgView = new ImageView(pauseBg);
            bgView.setFitWidth(POPUP_W);
            bgView.setFitHeight(POPUP_H);
            bgView.setPreserveRatio(false);  // stretch ให้เต็มไม่เหลือขอบขาว
            bgView.setSmooth(false);
            root.getChildren().add(bgView);
        } else {
            root.setStyle("-fx-background-color: white; -fx-border-color: black;");
        }
        root.getChildren().add(btnBox);
        StackPane.setAlignment(btnBox, Pos.CENTER);

        Scene scene = new Scene(root, POPUP_W, POPUP_H);
        scene.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                pausePopup.close(); resumeAll(); this.requestFocus();
            }
        });
        pausePopup.setScene(scene);
        pausePopup.setOnCloseRequest(ev -> timer.play());
        pausePopup.show();
    }

    /** โหลด pixel font จาก /Font/pixelFont.ttf */
    private Font pauseMenuFont(double size) {
        Font f = Font.loadFont(
                getClass().getResourceAsStream("/Font/pixelFont.ttf"), size
        );
        return f != null ? f : Font.font(size);
    }

    /** สร้างปุ่ม image-only สำหรับ pause menu พร้อม hover/press effect */
    private Button createPauseImageButton(String resourcePath) {
        Button btn = new Button();
        try {
            Image img = new Image(getClass().getResourceAsStream(resourcePath));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(200);
            iv.setPreserveRatio(true);
            iv.setSmooth(false);
            btn.setGraphic(iv);
        } catch (Exception e) {
            btn.setText("?");
        }
        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;" +
                "-fx-background-insets: 0;" +
                "-fx-border-color: transparent;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
            btn.setCursor(javafx.scene.Cursor.HAND);
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

    // ═══════════════════════════════════════════════════════════════════════
    // RENDERING
    // ═══════════════════════════════════════════════════════════════════════

    private void renderGrid() {
        for (int r = 0; r < config.getRows(); r++)
            for (int c = 0; c < config.getCols(); c++)
                styleCell(r, c);
    }

    private void styleCell(int r, int c) {
        Button cell = cells[r][c];
        cell.setText(""); cell.setGraphic(null);

        // Player
        if (r == playerRow && c == playerCol) {
            Image[] set = switch (name) {
                case "Patrick"   -> patrickImgs;
                case "Squidward" -> squidWardImgs;
                default          -> spongebobImgs;
            };
            Image img = pickDirImage(set, playerDir);
            String style = player.hasShield()
                    ? baseStyle + "-fx-border-color: #00E5FF; -fx-border-width: 4px; " +
                    "-fx-border-radius: 100; -fx-background-radius: 100;"
                    : baseStyle;
            if (img != null) { cell.setStyle(style); cell.setGraphic(makePlayerCellGraphic(img)); }
            else             { cell.setStyle(style + "-fx-background-color: #fff176; -fx-font-weight: bold;"); cell.setText("S"); }
            return;
        }

        // Enemy
        Enemy en = enemyAt(r, c);
        if (en != null) {
            Image img = enemyImage(en);
            String style = en.isShielded()
<<<<<<< Updated upstream
                    ? "-fx-border-color: #00E5FF; -fx-border-width: 4px; -fx-border-radius: 100; " +
                    "-fx-background-radius: 100; -fx-background-color: #dcedc8;"
=======
                    ? "-fx-border-color: #00E5FF; -fx-border-width: 4px; -fx-border-radius: 100; -fx-background-radius: 100; -fx-background-color: #fff9c4;"
>>>>>>> Stashed changes
                    : baseStyle;
            if (img != null) { cell.setStyle(style); cell.setGraphic(makeCellImage(img)); }
            else             { cell.setStyle(style + "-fx-background-color: #ff7043; -fx-font-weight: bold;"); cell.setText("E"); }
            return;
        }

        // Bomb
        if (hasBomb[r][c]) {
            if (bombImg != null) { cell.setStyle(baseStyle); cell.setGraphic(makeCellImage(bombImg)); }
            else { cell.setStyle("-fx-background-color: #ef5350; -fx-border-color: #c62828; -fx-font-weight: bold;"); cell.setText("B"); }
            return;
        }

        // Rock
        if (map[r][c] instanceof Rock) {
            if (rockImg != null) { cell.setStyle(baseStyle); cell.setGraphic(makeCellImage(rockImg)); }
            else { cell.setStyle("-fx-background-color: #9e9e9e; -fx-border-color: #424242; -fx-font-weight: bold;"); cell.setText("R"); }
            return;
        }

        // Seaweed (animated)
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) {
            Image sw = (seaweedImgs != null) ? seaweedImgs[seaweedFrame] : null;
            if (sw == null && seaweedImgs != null) sw = seaweedImgs[1 - seaweedFrame];
            if (sw != null) { cell.setStyle(baseStyle); cell.setGraphic(makeCellImage(sw)); }
            else { cell.setStyle("-fx-background-color: #66bb6a; -fx-border-color: #2e7d32; -fx-font-weight: bold;"); cell.setText("W"); }
            return;
        }

        // Buff pickup
        if (buffMap[r][c] != null) {
            Image bImg = buffImage(buffMap[r][c]);
            if (bImg != null) { cell.setStyle(baseStyle); cell.setGraphic(makeCellImage(bImg)); return; }
        }

        // Spawn marker
        if (config.tileAt(r, c) == 'P') {
            if (spawnImg != null) { cell.setStyle(baseStyle); cell.setGraphic(makeCellImage(spawnImg)); }
            else { cell.setStyle("-fx-background-color: #f8bbd0; -fx-border-color: #ec407a; -fx-font-weight: bold;"); cell.setText("P"); }
            return;
        }

        cell.setStyle(baseStyle);
    }

    private void applyExplosionEffect(boolean[][] zone) {
        for (int r = 0; r < config.getRows(); r++) {
            for (int c = 0; c < config.getCols(); c++) {
                if (!zone[r][c]) continue;
                Button cell = cells[r][c];
                String saved = cell.getStyle();
                cell.setStyle("-fx-background-color: rgba(255,0,0,0.8); -fx-border-color: #ff1744;");
                new Timeline(new KeyFrame(Duration.millis(300), e -> cell.setStyle(saved))).play();
            }
        }
    }

    private void setGridStyle(String style) {
        setBaseStyle(style);
        for (int r = 0; r < config.getRows(); r++)
            for (int c = 0; c < config.getCols(); c++)
                cells[r][c].setStyle(style);
    }

    private void resetGridStyle() {
        setBaseStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
        for (int r = 0; r < config.getRows(); r++)
            for (int c = 0; c < config.getCols(); c++)
                cells[r][c].setStyle(baseStyle);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RENDERING HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private Enemy enemyAt(int r, int c) {
        for (Enemy e : enemies) if (e.occupiesTile(r, c)) return e;
        return null;
    }

    private Image enemyImage(Enemy e) {
        int dir = (e.getCurrentDir() < 0 || e.getCurrentDir() > 3) ? 1 : e.getCurrentDir();
        Image[] set;
        Level lvl = e.getLevel();
        if (lvl == null) { set = npcImgs; }
        else set = switch (lvl) {
            case EASY -> npcImgs;
            case HARD -> kingNeptuneImgs;
            case MEDIUM -> {
                if (e.getElement() == null) yield mrKrabImgs;
                yield switch (e.getElement()) {
                    case WATER    -> garyImgs;
                    case ELECTRIC -> sandyImgs;
                    default       -> mrKrabImgs;
                };
            }
        };
        return pickDirImage(set, dir);
    }

    private Image pickDirImage(Image[] set, int dir) {
        if (set == null) return null;
        if (dir < 0 || dir > 3) dir = 1;
        if (set[dir] != null) return set[dir];
        for (int d : new int[]{1, 0, 2, 3}) if (set[d] != null) return set[d];
        return null;
    }

    private Image buffImage(Buff b) {
        if (b instanceof MaxBombBuff)    return maxBombImg;
        if (b instanceof BombRangeBuff)  return bombRangeImg;
        if (b instanceof BombDamageBuff) return bombDamageImg;
        if (b instanceof ShieldBuff)     return bubbleShieldImg;
        if (b instanceof HealBuff)       return healImg;
        return null;
    }

    private ImageView makeCellImage(Image img) {
        ImageView iv = new ImageView(img);
        double size = Math.max(cellSize - 4, 8);
        iv.setFitWidth(size); iv.setFitHeight(size); iv.setPreserveRatio(true);
        return iv;
    }

    private StackPane makePlayerCellGraphic(Image playerImg) {
        StackPane stack = new StackPane();
        if (woodEdgeImg != null) {
            ImageView edge = new ImageView(woodEdgeImg);
            double fs = Math.max(cellSize - 2, 8);
            edge.setFitWidth(fs); edge.setFitHeight(fs);
            edge.setPreserveRatio(false); edge.setSmooth(false);
            stack.getChildren().add(edge);
        }
        if (playerImg != null) {
            ImageView pv = new ImageView(playerImg);
            double ps = Math.max(cellSize - 14, 8);
            pv.setFitWidth(ps); pv.setFitHeight(ps);
            pv.setPreserveRatio(true); pv.setSmooth(false);
            stack.getChildren().add(pv);
        }
        return stack;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UI HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private void updateKillLabel() {
        if (killLabel != null) killLabel.setText(scoreManager.formatProgress());
    }

    private void updateBombLabel() { bombLabel.setText(bombsLeft + " / " + maxBombs); }

    private void updateHearts() {
        heartsBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            Label h = new Label(i < hearts ? "♥" : "♡");
            h.setStyle("-fx-font-size: 26px; -fx-text-fill: " + (i < hearts ? "#e53935" : "#9e9e9e") + ";");
            heartsBox.getChildren().add(h);
        }
        if (hearts <= 0) { setGameStatus(Status.LOSE); gameOver(); }
    }

    private void updateSkillButtonUI() {
        if (!(player instanceof Skillable s)) return;
        if (!s.isSkillReady()) {
            long rem = Math.max(0, (s.getLastSkillUseTime() + (long) s.getCooldown() * 1000L
                    - System.currentTimeMillis()) / 1000);
            skillBtn.setText(rem + "s");
            skillBtn.setDisable(true);
            skillBtn.setOpacity(0.7);
        } else {
            skillBtn.setText("Skill");
            skillBtn.setDisable(false);
            skillBtn.setOpacity(1.0);
        }
    }

    private void updateBadge(Label badge, int count) {
        badge.setText(String.valueOf(count));
        badge.setVisible(count > 0);
    }

    public ImageView createSkillImage(String skillName) {
        Image img = new Image(getClass().getResourceAsStream("/images/" + skillName));
        ImageView iv = new ImageView(img);
        double size = 54.0;
        iv.setFitWidth(size); iv.setFitHeight(size); iv.setPreserveRatio(false);
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(size / 2, size / 2, size / 2);
        iv.setClip(clip);
        return iv;
    }

    private Label createBadgeLabel() {
        Label l = new Label("0");
        l.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-size: 11px; " +
                "-fx-font-weight: bold; -fx-min-width: 18px; -fx-min-height: 18px; " +
                "-fx-background-radius: 9; -fx-alignment: center; -fx-padding: 0 3 0 3;");
        l.setVisible(false);
        return l;
    }

    private StackPane createSkillWithBadge(String skillName, Label badge) {
        ImageView icon = createSkillImage(skillName);
        StackPane stack = new StackPane(icon, badge);
        stack.setPrefSize(54, 54);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(-4, -4, 0, 0));
        return stack;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // IMAGE / AUDIO LOADING
    // ═══════════════════════════════════════════════════════════════════════

    private void loadImages() {
        spongebobImgs   = loadDirectional("spongebob",   "Sponge",    "Front");
        patrickImgs     = loadDirectional("patrick",     "Patrick",   "Front");
        squidWardImgs   = loadDirectional("squidword",   "SquidWord", "Font");
        npcImgs         = loadDirectional("npc",         "Npc",       "Font");
        mrKrabImgs      = loadDirectional("mrkrab",      "MrKrab",    "Font");
        garyImgs        = loadDirectional("gary",        "Gary",      "Font");
        sandyImgs       = loadDirectional("sandy",       "Sandy",     "Font");
        kingNeptuneImgs = loadDirectional("kingneptune", "KingNep",   "Front");
        spawnImg     = tryLoadImage("/images/gamePlay/spawn/spawn.png");
        rockImg      = tryLoadImage("/images/gamePlay/rock/Rock.png");
        woodEdgeImg  = tryLoadImage("/images/gamePlay/WoodEdge/WoodEdge.png");
        seaweedImgs  = new Image[]{
                tryLoadImage("/images/gamePlay/seaweed/seaweed_0.png"),
                tryLoadImage("/images/gamePlay/seaweed/seaweed_1.png")
        };
        bombImg        = tryLoadImage("/images/gamePlay/bomb.png");
        maxBombImg     = tryLoadImage("/images/buffIcon/increaseMaximumBomb.png");
        bombRangeImg   = tryLoadImage("/images/buffIcon/increaseBombRange.png");
        bombDamageImg  = tryLoadImage("/images/buffIcon/increaseBombDamage.png");
        bubbleShieldImg= tryLoadImage("/images/buffIcon/bubbleShield.png");
        healImg        = tryLoadImage("/images/buffIcon/heal.png");
    }

    private Image[] loadDirectional(String folder, String prefix, String downSuffix) {
        return new Image[]{
                tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + "Back.png"),
                tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + downSuffix + ".png"),
                tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + "Left.png"),
                tryLoadImage("/images/gamePlay/" + folder + "/" + prefix + "Right.png")
        };
    }

    private void loadAudio() { explodeSfx = tryLoadAudio("/sounds/explosion.mp3"); }

    private Image tryLoadImage(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) { System.out.println("[Image] NOT FOUND: " + path); return null; }
            return new Image(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("[Image] ERROR: " + path + " → " + e.getMessage()); return null;
        }
    }

<<<<<<< Updated upstream
    private AudioClip tryLoadAudio(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) { System.out.println("[Audio] NOT FOUND: " + path); return null; }
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            System.out.println("[Audio] ERROR: " + path + " → " + e.getMessage()); return null;
=======
    private void updateSkillButtonUI() {
        if (!(player instanceof Skillable s)) return;
        if (!s.isSkillReady()) {
            long remaining = Math.max(0, (s.getLastSkillUseTime() + (long) s.getCooldown() * 1000L
                    - System.currentTimeMillis()) / 1000);
            // แสดงเวลาเหลือเป็นข้อความบน icon (ใช้ ContentDisplay.CENTER ไม่ได้กับ JavaFX)
            // ทางแก้: ลบ icon ชั่วคราว แสดงเลขจนกว่า skill จะพร้อม
            skillBtn.setGraphic(null);
            skillBtn.setText(remaining + "s");
            skillBtn.setDisable(true);
            skillBtn.setOpacity(0.7);
        } else {
            // กลับมาแสดงรูป icon เมื่อ skill พร้อมใช้
            skillBtn.setText("");
            applyImageGraphic(skillBtn, currentSkillImg, 80);
            skillBtn.setDisable(false);
            skillBtn.setOpacity(1.0);
>>>>>>> Stashed changes
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // GAME OVER
    // ═══════════════════════════════════════════════════════════════════════

    private void gameOver() {
        pauseAll();
        if (gameStatus == Status.WIN || gameStatus == Status.CLEAR)
            GameProgress.markCleared(config.getLevel() - 1);
        this.getScene().setRoot(new GameOverController(gameStatus, config, name));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PUBLIC API
    // ═══════════════════════════════════════════════════════════════════════

    public void setGameStatus(Status s) { this.gameStatus = s; }
    public int  getHearts()             { return hearts; }
    public void setHearts(int h)        { this.hearts = Math.max(0, Math.min(5, h)); }
    public void setBaseStyle(String s)  { this.baseStyle = s; }

    public void takeDamage() {
        if (hearts > 0) hearts--;
        updateHearts();
        if (hearts <= 0) { setGameStatus(Status.LOSE); gameOver(); }
    }
}