package gui;

import game.Element;
import game.buff.*;
import game.character.*;
import game.character.Character;
import game.entity.*;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import game.util.ElementUtil;
import game.util.SoundManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

public class GameController extends StackPane {

    // ═══════════════════════════════════════════════════════════════════════
    // FIELDS
    // ═══════════════════════════════════════════════════════════════════════

    private final StageData config;
    private final String name;
    private final Element element;

    // ── Player ────────────────────────────────────────────────────────────
    private Character player;
    private int playerRow;
    private int playerCol;

    // ── Map ───────────────────────────────────────────────────────────────
    private Tile[][]    map;
    private Seaweed[][] seaweeds;
    private boolean[][] hasBomb;
    private Buff[][]    buffMap;

    // ── Game state ────────────────────────────────────────────────────────
    private int     hearts    = 5;
    private int     kills     = 0;
    private int     bombsLeft = 5;
    private int     maxBombs  = 5;
    private int     timeLeft  = 300;
    private boolean stopCharacter = false;
    private boolean stopEnemy     = false;
    private Status  gameStatus;

    // ── Buff badge counters ───────────────────────────────────────────────
    private int maxBombCount    = 0;
    private int bombRangeCount  = 0;
    private int bombDamageCount = 0;

    // ── UI references ─────────────────────────────────────────────────────
    private Label   killLabel;
    private Label   timerLabel;
    private HBox    heartsBox;
    private Label   bombLabel;
    private Button[][] cells;
    private double  cellSize;

    private Label maxBombBadge;
    private Label bombRangeBadge;
    private Label bombDamageBadge;
    private Label shieldBadge;
    private Label healBadge;

    private Button explodeBtn;
    private Button plantBombBtn;
    private Button skillBtn;
    private Button infoBtn;

    private final String normalStyle  = "-fx-background-radius: 40;";
    private final String pressedStyle = "-fx-background-radius: 40; -fx-border-color: red; -fx-border-width: 3px; -fx-border-radius: 40;";
    private String baseStyle          = "-fx-background-color: #dcedc8; -fx-border-color: #aed581;";

    // ── Timers ────────────────────────────────────────────────────────────
    private Timeline timer;
    private Timeline enemyTimer;
    private Timeline spawnTimer;

<<<<<<< HEAD
    // ── Images (โหลดจาก resources/) ────────────
    // Player & enemy directional images: index 0=up(Back), 1=down(Front/Font), 2=left, 3=right
    private Image[] spongebobImgs;
    private Image[] patrickImgs;
    private Image[] squidWardImgs;
    // enemy images
    private Image[] npcImgs;             // Easy enemy
    private Image[] mrKrabImgs;          // Medium FIRE
    private Image[] garyImgs;            // Medium WATER
    private Image[] sandyImgs;           // Medium ELECTRIC
    private Image[] kingNeptuneImgs;     // Hard enemy
    private Image spawnImg;              // marker ของจุด spawn
    private Image rockImg;
    private Image woodEdgeImg;           // กรอบไม้รอบ tile ที่ player ยืน
    private Image[] seaweedImgs;         // 2 frames สำหรับ animation
    private int seaweedFrame = 0;        // 0 หรือ 1 — สลับทุก 0.5 วิ
    private Timeline seaweedAnimTimer;
    // ทิศทางหันของ player: 0=up, 1=down, 2=left, 3=right (default หันลง)
    private int playerDir = 1;
    private Image bombImg;
    private Image maxBombImg;
    private Image bombRangeImg;
    private Image bombDamageImg;
    private Image bubbleShieldImg;
    private Image healImg;
=======
    // ── Popups ────────────────────────────────────────────────────────────
    private Stage infoPopup;
    private Stage pausePopup;
>>>>>>> 93ac0c839b173038d6ea823533acfa4edaf0816f

    // ── Enemies ───────────────────────────────────────────────────────────
    private List<Enemy> enemies      = new ArrayList<>();
    private int  totalSpawned        = 0;
    private int  stagePhase          = 1;
    private boolean phase2Started    = false;

    private static final int[] DR = {-1, 1,  0, 0};
    private static final int[] DC = { 0, 0, -1, 1};

    // ── Images ────────────────────────────────────────────────────────────
    private Image spongebobImg, patrickImg, squidWardImg;
    private Image npcImg, mrKrabImg, garyImg, sandyImg, kingNeptuneImg, spawnImg;
    private Image rockImg, seaweedImg, bombImg;
    private Image maxBombImg, bombRangeImg, bombDamageImg, bubbleShieldImg, healImg;

    // ── Audio ─────────────────────────────────────────────────────────────
    private AudioClip explodeSfx;

    // ── Utilities ─────────────────────────────────────────────────────────
    private final ElementUtil elementUtil = new ElementUtil();

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════════

    public GameController(StageData config, String name) {
        this.config = config;
        this.name   = name;
        this.element = switch (name) {
            case "Patrick"  -> Element.FIRE;
            case "Squidward"-> Element.WATER;
            default         -> Element.ELECTRIC; // SpongeBob
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
        startSeaweedAnimation();   // ⭐️ เริ่ม animation seaweed (สลับเฟรมทุก 0.5 วิ)

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

<<<<<<< HEAD
    // ── Load images safely (null ถ้าไม่เจอ) ─────────────
    private void loadImages() {
        // Players — direc  tional sprites (subfolder ตามชื่อ)
        spongebobImgs   = loadDirectional("spongebob",   "Sponge",    "Front");
        patrickImgs     = loadDirectional("patrick",     "Patrick",   "Front");
        squidWardImgs   = loadDirectional("squidword",   "SquidWord", "Font");

        // Enemies — directional sprites
        npcImgs         = loadDirectional("npc",         "Npc",       "Font");  // Easy
        mrKrabImgs      = loadDirectional("mrkrab",      "MrKrab",    "Font");  // Medium FIRE
        garyImgs        = loadDirectional("gary",        "Gary",      "Font");  // Medium WATER
        sandyImgs       = loadDirectional("sandy",       "Sandy",     "Font");  // Medium ELECTRIC
        kingNeptuneImgs = loadDirectional("kingneptune", "KingNep",   "Front"); // Hard

        spawnImg     = tryLoadImage("/images/gamePlay/spawn/spawn.png");
        rockImg      = tryLoadImage("/images/gamePlay/rock/Rock.png");
        woodEdgeImg  = tryLoadImage("/images/gamePlay/WoodEdge/WoodEdge.png");

        // Seaweed: 2 frames for animation
        seaweedImgs = new Image[]{
                tryLoadImage("/images/gamePlay/seaweed/seaweed_0.png"),
                tryLoadImage("/images/gamePlay/seaweed/seaweed_1.png")
        };

        bombImg      = tryLoadImage("/images/gamePlay/bomb.png");
=======
    // ═══════════════════════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════════════════════
>>>>>>> 93ac0c839b173038d6ea823533acfa4edaf0816f

    private void loadImages() {
        spongebobImg   = tryLoadImage("/images/gamePlay/spongebob.png");
        patrickImg     = tryLoadImage("/images/gamePlay/patrick.png");
        squidWardImg   = tryLoadImage("/images/gamePlay/squidward.png");
        npcImg         = tryLoadImage("/images/gamePlay/npc.png");
        mrKrabImg      = tryLoadImage("/images/gamePlay/mrKrab.png");
        garyImg        = tryLoadImage("/images/gamePlay/gary.png");
        sandyImg       = tryLoadImage("/images/gamePlay/sandy.png");
        kingNeptuneImg = tryLoadImage("/images/gamePlay/kingneptune.png");
        spawnImg       = tryLoadImage("/images/gamePlay/spawn.png");
        rockImg        = tryLoadImage("/images/gamePlay/rock.png");
        seaweedImg     = tryLoadImage("/images/gamePlay/seaweed.png");
        bombImg        = tryLoadImage("/images/gamePlay/bomb.png");
        maxBombImg     = tryLoadImage("/images/buffIcon/increaseMaximumBomb.png");
        bombRangeImg   = tryLoadImage("/images/buffIcon/increaseBombRange.png");
        bombDamageImg  = tryLoadImage("/images/buffIcon/increaseBombDamage.png");
        bubbleShieldImg= tryLoadImage("/images/buffIcon/bubbleShield.png");
        healImg        = tryLoadImage("/images/buffIcon/heal.png");
    }

<<<<<<< HEAD
    /**
     * โหลดรูป 4 ทิศทางของตัวละคร 1 ตัว
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

    /** เริ่ม timer สลับเฟรม seaweed ทุก 0.5 วินาที */
    private void startSeaweedAnimation() {
        seaweedAnimTimer = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            seaweedFrame = 1 - seaweedFrame;
            // re-render เฉพาะช่องที่มี seaweed ยังไม่ทำลาย
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

    private void loadAudio(){
=======
    private void loadAudio() {
>>>>>>> 93ac0c839b173038d6ea823533acfa4edaf0816f
        explodeSfx = tryLoadAudio("/sounds/explosion.mp3");
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

<<<<<<< HEAD
    // ── สร้าง ImageView ขนาดพอดี cell ─────────────────
    private ImageView makeCellImage(Image img) {
        ImageView iv = new ImageView(img);
        double size = Math.max(cellSize - 4, 8);  // เผื่อขอบนิดหน่อย
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        return iv;
    }

    /**
     * สร้าง graphic ของช่องที่ player ยืน:
     * ใช้ WoodEdge.png เป็นกรอบรอบนอก แล้วซ้อนรูป player อยู่ตรงกลาง
     * ทำให้ผู้เล่นเห็นชัดว่าตัวเองอยู่ช่องไหน
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
            double playerSize = Math.max(cellSize - 14, 8);  // เล็กกว่ากรอบนิดหน่อย
            pv.setFitWidth(playerSize);
            pv.setFitHeight(playerSize);
            pv.setPreserveRatio(true);
            pv.setSmooth(false);
            stack.getChildren().add(pv);
        }

        return stack;
    }

    // ── Create the player character ─────────────────────
=======
>>>>>>> 93ac0c839b173038d6ea823533acfa4edaf0816f
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
                char ch = config.tileAt(r, c);
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

        // Guarantee at least one of each buff type somewhere on the map
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

    // ═══════════════════════════════════════════════════════════════════════
    // SKILL HANDLING  (extracted from the giant case-K block)
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
        stopEnemy     = true;
        stopCharacter = true;
        timer.stop();
        fire.useSkill();
        setGridStyle("-fx-background-color: #cfd8dc; -fx-border-color: #b0bec5;");

        Timeline disarm = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            if (fire.isTeleportArmed()) {
                fire.cancelTeleport();
                resetGridStyle();
                timer.play();
                stopEnemy     = false;
                stopCharacter = false;
            }
        }));
        disarm.play();
    }

    private void activateWaterShield(WaterCharacter water) {
        water.useSkill();
        styleCell(playerRow, playerCol);
        skillBtn.setDisable(true);
        activateShieldBadge();
    }

    private void activateElectricStun(ElectricCharacter electric) {
        stopEnemy = true;
        setGridStyle("-fx-background-color: #cfd8dc; -fx-border-color: #b0bec5;");

        Timeline unstun = new Timeline(new KeyFrame(
                Duration.millis(ElectricCharacter.getStunDurationMs()),
                e -> {
                    stopEnemy = false;
                    resetGridStyle();
                }
        ));
        unstun.play();
        electric.useSkill();
    }

    /** Apply a uniform style to every cell (e.g. stun/teleport visual). */
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
    // SHIELD BADGE  (replaces 5 duplicated inline-style blocks)
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
    // MOVEMENT & BUFF PICKUP
    // ═══════════════════════════════════════════════════════════════════════

    private void tryMove(int dr, int dc) {
        int nr = playerRow + dr;
        int nc = playerCol + dc;
        if (nr < 0 || nr >= config.getRows()) return;
        if (nc < 0 || nc >= config.getCols()) return;
        if (!map[nr][nc].isPassable()) return;
        if (seaweeds[nr][nc] != null && !seaweeds[nr][nc].isDestroyed()) return;
        if (hasBomb[nr][nc]) return;

        playerRow = nr;
        playerCol = nc;
        player.setPos(playerCol, playerRow);

        if (buffMap[playerRow][playerCol] != null) {
            applyBuffPickup(buffMap[playerRow][playerCol]);
            buffMap[playerRow][playerCol] = null;
        }

        checkPlayerEnemyCollision();
        renderGrid();
    }

    /** Separated from tryMove so the pickup logic is easy to read and extend. */
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
    // BOMB PLANTING & EXPLODING
    // ═══════════════════════════════════════════════════════════════════════

    private void plantBombAtPlayer() {
        if (bombsLeft <= 0 || hasBomb[playerRow][playerCol]) return;
        hasBomb[playerRow][playerCol] = true;
        bombsLeft--;
        updateBombLabel();
        renderGrid();
    }

    private void explodeBombs() {
        int rows  = config.getRows();
        int cols  = config.getCols();
        int range = player.getBombRange();
        boolean[][] blastZone = new boolean[rows][cols];
        boolean playerHit = false;

        if (bombsLeft < maxBombs && explodeSfx != null) SoundManager.playSFX(explodeSfx);

        // Build blast zone
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!hasBomb[r][c]) continue;
                blastZone[r][c] = true;

                int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
                for (int[] d : dirs) {
                    for (int step = 1; step <= range; step++) {
                        int rr = r + d[0] * step;
                        int cc = c + d[1] * step;
                        if (rr < 0 || rr >= rows || cc < 0 || cc >= cols) break;
                        if (map[rr][cc] instanceof Rock) break;
                        blastZone[rr][cc] = true;
                        if (seaweeds[rr][cc] != null && !seaweeds[rr][cc].isDestroyed()) {
                            seaweeds[rr][cc].destroy();
                            break;
                        }
                        if (!playerHit && rr == playerRow && cc == playerCol) {
                            playerHit = true;
                            applyBlastDamageToPlayer();
                        }
                    }
                }
                // Bomb is on player's tile
                if (!playerHit && r == playerRow && c == playerCol) {
                    playerHit = true;
                    applyBlastDamageToPlayer();
                }
            }
        }

        applyBlastDamageToEnemies(blastZone);

        // Reset bombs
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                hasBomb[r][c] = false;

        bombsLeft = maxBombs;
        updateBombLabel();
        renderGrid();
        applyExplosionEffect(blastZone);
    }

    private void applyBlastDamageToPlayer() {
        if (player.hasShield()) {
            player.setShield(false);
            deactivateShieldBadge();
        } else {
            hearts -= player.getDamage();
            updateHearts();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ENEMY DAMAGE & COLLISION
    // ═══════════════════════════════════════════════════════════════════════

    private void applyBlastDamageToEnemies(boolean[][] blastZone) {
        int rows = config.getRows();
        int cols = config.getCols();
        Iterator<Enemy> it = enemies.iterator();

        while (it.hasNext()) {
            Enemy en  = it.next();
            boolean hit = isEnemyInBlastZone(en, blastZone, rows, cols);
            if (!hit) continue;

            if (en.isShielded()) { en.setShielded(false); continue; }

            int dmg = elementUtil.calculateCharacterDamage(player, en);
            en.setHealth(en.getHealth() - dmg);

            if (en.getHealth() <= 0) {
                it.remove();
                kills++;
                if (killLabel != null) killLabel.setText(kills + " / " + config.getGoal());
                if (kills >= config.getGoal()) {
                    setGameStatus(config.getLevel() == 5 ? Status.CLEAR : Status.WIN);
                    gameOver();
                    return;
                }
            }
        }

        if (killLabel != null) killLabel.setText(kills + " / " + config.getGoal());

        if (config.getLevel() == 5 && !phase2Started && kills >= 25) {
            triggerStage5Phase2();
        }
    }

    private boolean isEnemyInBlastZone(Enemy en, boolean[][] zone, int rows, int cols) {
        if (en.getLevel() == Level.HARD) {
            for (int dr = 0; dr < 2; dr++)
                for (int dc = 0; dc < 2; dc++) {
                    int rr = en.getPosY() + dr, cc = en.getPosX() + dc;
                    if (rr >= 0 && rr < rows && cc >= 0 && cc < cols && zone[rr][cc]) return true;
                }
            return false;
        }
        int r = en.getPosY(), c = en.getPosX();
        return r >= 0 && r < rows && c >= 0 && c < cols && zone[r][c];
    }

    private void damageFromEnemy(Enemy e) {
        if (player.hasShield()) {
            player.setShield(false);
            deactivateShieldBadge();
            return;
        }
        int dmg = elementUtil.calculateEnemyDamage(e, player);
        hearts = Math.max(0, hearts - dmg);
        updateHearts();
    }

    private void checkPlayerEnemyCollision() {
        for (Enemy e : enemies) {
            if (enemyOccupiesTile(e, playerRow, playerCol)) {
                damageFromEnemy(e);
                return;
            }
        }
    }

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
            enemies.add(new MediumEnemy(1, pos[1], pos[0], randomElement(), someShielded && Math.random() < 0.4));
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
        enemies.add(new MediumEnemy(1, col, row, randomElement(), canShield && Math.random() < 0.4));
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
            enemies.add(new HardEnemy(2, c, r, randomElement(), false));
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
            }
        }
        spawnHardRandom();
        spawnHardRandom();

        spawnTimer.stop();
        spawnTimer.getKeyFrames().setAll(new KeyFrame(Duration.seconds(30), e -> tickSpawn()));
        spawnTimer.play();
        renderGrid();
    }

    private void startEnemyTimer() {
        enemyTimer = new Timeline(new KeyFrame(Duration.millis(600), e -> {
            for (Enemy en : enemies) moveEnemy(en);
            renderGrid();
            checkPlayerEnemyCollision();
        }));
        enemyTimer.setCycleCount(Timeline.INDEFINITE);
        enemyTimer.play();
    }

<<<<<<< HEAD
    // คืน Enemy ที่อยู่ตำแหน่ง (r,c) — ไม่มี → null
    private Enemy enemyAt(int r, int c) {
        for (Enemy e : enemies) {
            if (enemyOccupiesTile(e, r, c)) return e;
        }
        return null;
    }

    // เลือกรูปของ enemy ตาม Level + Element + ทิศทาง (ใช้ currentDir)
    private Image enemyImage(Enemy e) {
        int dir = clampDir(e.getCurrentDir());
        Image[] set = npcImgs;  // default
        Level lvl = e.getLevel();
        if (lvl != null) {
            switch (lvl) {
                case EASY:
                    set = npcImgs;
                    break;
                case MEDIUM:
                    if (e.getElement() == null) {
                        set = mrKrabImgs;
                    } else {
                        switch (e.getElement()) {
                            case FIRE:     set = mrKrabImgs; break;
                            case WATER:    set = garyImgs;   break;
                            case ELECTRIC: set = sandyImgs;  break;
                            default:       set = mrKrabImgs; break;
                        }
                    }
                    break;
                case HARD:
                    set = kingNeptuneImgs;
                    break;
                default:
                    set = npcImgs;
            }
        }
        return pickDirImage(set, dir);
    }

    /** เลือกรูปจาก array 4 ทิศ — ถ้ารูปทิศนั้นไม่มี ให้หาตัวอื่นเป็น fallback */
    private Image pickDirImage(Image[] set, int dir) {
        if (set == null) return null;
        if (dir < 0 || dir > 3) dir = 1;
        if (set[dir] != null) return set[dir];
        // fallback: ลอง down ก่อน (ปกติเป็นหน้าเริ่มต้น) แล้วไล่ดูรูปอื่น
        for (int d : new int[]{1, 0, 2, 3}) {
            if (set[d] != null) return set[d];
        }
        return null;
    }

    private int clampDir(int dir) {
        if (dir < 0 || dir > 3) return 1;
        // Enemy dir mapping: 0=up, 1=down, 2=left, 3=right (ตรงกับ player)
        return dir;
    }

    // เช็กว่า enemy เดินไปช่อง (r,c) ได้มั้ย
    //   - ต้องอยู่ในแผนที่
    //   - ห้ามเป็น Rock / Seaweed (ที่ยังไม่ทำลาย) / ระเบิด / ตัว enemy อื่น
    private boolean canEnemyWalk(int r, int c, Enemy self) {
        if (r < 0 || r >= config.getRows()) return false;
        if (c < 0 || c >= config.getCols()) return false;
        if (map[r][c] instanceof Rock) return false;
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) return false;
        if (hasBomb[r][c]) return false;
        for (Enemy other : enemies) {
            if (other == self) continue;
            if (enemyOccupiesTile(other, r, c)) return false;
        }
        return true;
    }

    // เช็กว่า Hard เดินไป "anchor" (r,c) ได้มั้ย — ต้องเช็ก 2×2 area
    private boolean canHardWalk(int newR, int newC, Enemy self) {
        int rows = config.getRows();
        int cols = config.getCols();
        if (newR < 0 || newR + 1 >= rows) return false;
        if (newC < 0 || newC + 1 >= cols) return false;
        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int rr = newR + dr;
                int cc = newC + dc;
                if (map[rr][cc] instanceof Rock) return false;
                if (seaweeds[rr][cc] != null && !seaweeds[rr][cc].isDestroyed()) return false;
                if (hasBomb[rr][cc]) return false;
                for (Enemy other : enemies) {
                    if (other == self) continue;
                    if (enemyOccupiesTile(other, rr, cc)) return false;
                }
            }
        }
        return true;
    }

    // ── Logic การเดินของ enemy (dispatcher) ──
    //   Easy / Medium → เดินสุ่ม (logic เดิม: ตรงไปก่อน → เลี้ยว → ถอยหลัง)
    //   Hard         → ไล่ตาม player (เลือกทิศที่ใกล้ player ที่สุด)
=======
>>>>>>> 93ac0c839b173038d6ea823533acfa4edaf0816f
    private void moveEnemy(Enemy e) {
        if (stopEnemy) return;
        if (e.getLevel() == Level.HARD) moveHardFollowPlayer(e);
        else moveRandom(e);
    }

    private void moveRandom(Enemy e) {
        int dir = e.getCurrentDir();
        int r = e.getPosY(), c = e.getPosX();

        int nr = r + DR[dir], nc = c + DC[dir];
        if (canEnemyWalk(nr, nc, e)) { e.setPosY(nr); e.setPosX(nc); return; }

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
        for (int i = 3; i > 0; i--) { // Fisher-Yates shuffle
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
        if (e.getLevel() == Level.HARD) return (r==er||r==er+1) && (c==ec||c==ec+1);
        return r == er && c == ec;
    }

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
        if (nr < 0 || nr+1 >= rows || nc < 0 || nc+1 >= cols) return false;
        for (int dr = 0; dr < 2; dr++)
            for (int dc = 0; dc < 2; dc++)
                if (!canEnemyWalk(nr+dr, nc+dc, self)) return false;
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
                if (!isTileFreeForSpawn(r+dr, c+dc)) return false;
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

    private Image enemyImage(Enemy e) {
        if (e.getLevel() == null)         return npcImg;
        return switch (e.getLevel()) {
            case EASY -> npcImg;
            case HARD -> kingNeptuneImg;
            case MEDIUM -> {
                if (e.getElement() == null) yield mrKrabImg;
                yield switch (e.getElement()) {
                    case WATER    -> garyImg;
                    case ELECTRIC -> sandyImg;
                    default       -> mrKrabImg;
                };
            }
        };
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TIMER
    // ═══════════════════════════════════════════════════════════════════════

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLeft <= 0) { pauseAll(); setGameStatus(Status.LOSE); gameOver(); return; }
            timeLeft--;
            int m = timeLeft / 60, s = timeLeft % 60;
            timerLabel.setText(m + ":" + (s < 10 ? "0" : "") + s);
            if (timeLeft <= 30) timerLabel.setStyle(timerLabel.getStyle() + "-fx-text-fill: #e53935;");
            if (kills > config.getGoal()) {
                setGameStatus(config.getLevel() == 5 ? Status.CLEAR : Status.WIN);
                gameOver();
            }
            updateSkillButtonUI();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        resumeAll();
    }

    private void pauseAll()  { if (timer != null) timer.stop();  if (enemyTimer != null) enemyTimer.stop();  if (spawnTimer != null) spawnTimer.stop(); }
    private void resumeAll() { if (timer != null) timer.play();  if (enemyTimer != null) enemyTimer.play();  if (spawnTimer != null) spawnTimer.play(); }

    // ═══════════════════════════════════════════════════════════════════════
    // UI SETUP
    // ═══════════════════════════════════════════════════════════════════════

<<<<<<< HEAD
    // ── Movement ────────────────────────────────────────
    private void tryMove(int dr, int dc) {
        // อัปเดตทิศที่หันก่อน — ถึงเดินไม่ได้ก็ยังหันหน้าเปลี่ยนได้
        if (dr == -1) playerDir = 0;          // up (W)
        else if (dr == 1) playerDir = 1;      // down (S)
        else if (dc == -1) playerDir = 2;     // left (A)
        else if (dc == 1) playerDir = 3;      // right (D)

        int nr = playerRow + dr;
        int nc = playerCol + dc;

        if (nr < 0 || nr >= config.getRows()) { renderGrid(); return; }
        if (nc < 0 || nc >= config.getCols()) { renderGrid(); return; }
        if (!map[nr][nc].isPassable()) { renderGrid(); return; }          // ติด Rock
        if (seaweeds[nr][nc] != null && !seaweeds[nr][nc].isDestroyed()) { renderGrid(); return; } // ติด Seaweed
        if (hasBomb[nr][nc]) { renderGrid(); return; }                    // ติด Bomb

        playerRow = nr;
        playerCol = nc;
        player.setPos(playerCol, playerRow);

        if (buffMap[playerRow][playerCol] != null) {
            Buff currentBuff = buffMap[playerRow][playerCol];
            int level = config.getLevel();
            boolean shieldDisabled = (level == 3 || level == 4);   // Stage 3-4 ห้ามเก็บ shield

            // 1. ส่งผลกับตัวละคร
            currentBuff.apply(player);

            // 2. ถ้าเป็นบัฟที่ส่งผลต่อจำนวนระเบิดใน UI (ต้องอัปเดต Label ด้วย)
            if (currentBuff instanceof MaxBombBuff) {
                maxBombs++;
                bombsLeft++;
                maxBombCount++;
                updateBombLabel();
                updateBadge(maxBombBadge, maxBombCount);
            } else if (currentBuff instanceof BombRangeBuff) {
                bombRangeCount++;
                updateBadge(bombRangeBadge, bombRangeCount);
            } else if (currentBuff instanceof BombDamageBuff) {
                bombDamageCount++;
                updateBadge(bombDamageBadge, bombDamageCount);
            } else if (currentBuff instanceof ShieldBuff) {
            // Show green ✓ when shield is active
            shieldBadge.setText("✓");
            shieldBadge.setStyle(
                    "-fx-background-color: #43a047; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 11px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-min-width: 18px; " +
                            "-fx-min-height: 18px; " +
                            "-fx-background-radius: 9; " +
                            "-fx-alignment: center; " +
                            "-fx-padding: 0 3 0 3;"
            );
            shieldBadge.setVisible(true);
        } else if (currentBuff instanceof HealBuff) {
            if (hearts < 5) hearts++;
            updateHearts();
            // Show green ✓ briefly then hide (heal is instant)
        }

            // 3. ลบบัฟออกจากแผนที่หลังจากกินแล้ว
            buffMap[playerRow][playerCol] = null;

            // 4. (Optional) เล่นเสียงเก็บไอเทม
            // SoundManager.playSFX(pickupSfx);
        }
        checkPlayerEnemyCollision();
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

        if (r == playerRow && c == playerCol) {
            Image playerImg = null;
            Image[] set = null;
            if(name.equals("Patrick"))   { set = patrickImgs;   }
            if(name.equals("Squidward")) { set = squidWardImgs; }
            if(name.equals("SpongeBob")) { set = spongebobImgs; }
            playerImg = pickDirImage(set, playerDir);

            // ⭐️ เช็ค Shield เพื่อกำหนด Style เส้นขอบ
            String playerStyle = baseStyle;
            if (player.hasShield()) {
                playerStyle += "-fx-border-color: #00E5FF; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-radius: 100; " +
                        "-fx-background-radius: 100; " +
                        "-fx-background-color: #dcedc8;";
            }

            if (playerImg != null) {
                cell.setStyle(playerStyle);
                cell.setGraphic(makeCellImage(playerImg));
            } else {
                // กรณีไม่มีรูป ให้เปลี่ยนสีพื้นหลังตัว S แทน
                cell.setStyle(playerStyle + "-fx-background-color: #fff176; -fx-font-weight: bold;");
                cell.setText("S");
            }
            return;
        }
        // Enemy (วาดทับ tile แต่ player จะวาดทับ enemy อีกที — ตรวจไปแล้วด้านบน)
        Enemy enemyHere = enemyAt(r, c);
        if (enemyHere != null) {
            Image img = enemyImage(enemyHere);
            String enemyStyle = enemyHere.isShielded()
                    ? "-fx-border-color: #00E5FF; " +
                    "-fx-border-width: 4px; " +
                    "-fx-border-radius: 100; " +
                    "-fx-background-radius: 100; " +
                    "-fx-background-color: #dcedc8;"
                    : baseStyle;
            if (img != null) {
                cell.setStyle(enemyStyle);
                cell.setGraphic(makeCellImage(img));
            } else {
                cell.setStyle(enemyStyle + "-fx-background-color: #ff7043; -fx-font-weight: bold;");
                cell.setText("E");
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
            // ใช้เฟรมที่กำลังแสดงอยู่ (สลับทุก 0.5 วิด้วย seaweedAnimTimer)
            Image swImg = (seaweedImgs != null) ? seaweedImgs[seaweedFrame] : null;
            if (swImg == null && seaweedImgs != null) {
                // fallback: ลองอีกเฟรม
                swImg = seaweedImgs[1 - seaweedFrame];
            }
            if (swImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(swImg));
            } else {
                cell.setStyle("-fx-background-color: #66bb6a; -fx-border-color: #2e7d32; -fx-font-weight: bold;");
                cell.setText("W");
            }
            return;
        }

        if (buffMap[r][c] != null) {
            Image bImg = null;
            Buff b = buffMap[r][c];

            if (b instanceof MaxBombBuff) bImg = maxBombImg;
            else if (b instanceof BombRangeBuff) bImg = bombRangeImg;
            else if (b instanceof BombDamageBuff) bImg = bombDamageImg;
            else if (b instanceof ShieldBuff) bImg = bubbleShieldImg;
            else if (b instanceof HealBuff) bImg = healImg;

            if (bImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(bImg));
                return;
            }
        }

        // Spawn-point marker — แสดงรูป spawn.png บนช่องที่มาร์ค 'P' ใน layout
        // (เดินทับได้ตามปกติ — ไม่ใช่สิ่งกีดขวาง)
        if (config.tileAt(r, c) == 'P') {
            if (spawnImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(spawnImg));
            } else {
                cell.setStyle("-fx-background-color: #f8bbd0; -fx-border-color: #ec407a; -fx-font-weight: bold;");
                cell.setText("P");
            }
            return;
        }

        // plain tile (or destroyed seaweed → ผ่านได้)
        cell.setStyle(baseStyle);
    }

    // ── Setup UI (top bar / grid / right panel / bottom) ─
=======
>>>>>>> 93ac0c839b173038d6ea823533acfa4edaf0816f
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
        this.getChildren().add(root);
    }

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
                cell.setMinSize(cs, cs);
                cell.setMaxSize(cs, cs);
                cell.setPrefSize(cs, cs);
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

    /** Handles FireCharacter teleport clicks on the grid. */
    private void handleCellClick(int row, int col) {
        if (!(player instanceof FireCharacter fire) || !fire.isTeleportArmed()) return;

        Tile targetTile = map[row][col];
        if (seaweeds[row][col] != null && !seaweeds[row][col].isDestroyed())
            targetTile = seaweeds[row][col];

        if (fire.teleportTo(col, row, targetTile, false)) {
            playerRow = row;
            playerCol = col;
            renderGrid();
            resetGridStyle();
        }
        stopEnemy     = false;
        stopCharacter = false;
        timer.play();
    }

    private HBox buildRightPanel() {
        maxBombBadge    = createBadgeLabel();
        bombRangeBadge  = createBadgeLabel();
        bombDamageBadge = createBadgeLabel();
        shieldBadge     = createBadgeLabel();
        healBadge       = createBadgeLabel();
        deactivateShieldBadge(); // show ✗ by default

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
        infoBtn.setStyle("-fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e53935; -fx-border-width: 2;");
        infoBtn.setOnAction(e -> { pauseAll(); skillsInformation(); });

        iconCol.getChildren().addAll(spacer, infoBtn, new Label("[U]"));

        HBox panel = new HBox(40, createBombControls(), iconCol);
        panel.setAlignment(Pos.BOTTOM_RIGHT);
        panel.setPadding(new Insets(10, 15, 20, 0));
        return panel;
    }

    private VBox createBombControls() {
        this.skillBtn = new Button("Skill");
        skillBtn.setPrefSize(80, 80);
        skillBtn.setFocusTraversable(false);
        skillBtn.setStyle(normalStyle);
        skillBtn.setOnAction(e -> player.useSkill());

        String skillText = switch (name) {
            case "Patrick"      -> "Teleport\n[K]";
            case "Squidward"    -> "Generate\nshield [K]";
            case "SpongeBob"    -> "Freeze all\nenemies [K]";
            default -> null;
        };
        Label skillLabel = new Label(skillText);
        skillLabel.setFont(Font.font(15));

        this.explodeBtn = new Button("Bomb");
        explodeBtn.setPrefSize(80, 80);
        explodeBtn.setFocusTraversable(false);
        explodeBtn.setStyle(normalStyle);
        explodeBtn.setOnAction(e -> explodeBombs());

        this.plantBombBtn = new Button("Plant");
        plantBombBtn.setPrefSize(80, 80);
        plantBombBtn.setFocusTraversable(false);
        plantBombBtn.setStyle(normalStyle);
        plantBombBtn.setOnAction(e -> plantBombAtPlayer());

        this.bombLabel = new Label(bombsLeft + " / " + maxBombs);
        bombLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox box = new VBox(15, skillBtn, skillLabel, explodeBtn, new Label("[O]"),
                plantBombBtn, bombLabel, new Label("[P]"));
        box.setAlignment(Pos.BOTTOM_CENTER);
        return box;
    }

    private HBox buildBottomBar() {
        heartsBox = new HBox(8);
        heartsBox.setAlignment(Pos.CENTER);
        heartsBox.setPadding(new Insets(10));
        updateHearts();
        return heartsBox;
    }

    // ── Popups ─────────────────────────────────────────────────────────────

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
        scene.setOnKeyPressed(ev -> { if (ev.getCode() == javafx.scene.input.KeyCode.U) { infoPopup.close(); resumeAll(); this.requestFocus(); }});
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

        Button resumeBtn = new Button("Resume");
        resumeBtn.setPrefWidth(100);
        resumeBtn.setOnAction(e -> { pausePopup.close(); resumeAll(); });

        Button quitBtn = new Button("Quit to home");
        quitBtn.setPrefWidth(100);
        quitBtn.setOnAction(e -> { pausePopup.close(); this.getScene().setRoot(new HomeController()); });

        VBox layout = new VBox(20, new Label("GAME PAUSED"), resumeBtn, quitBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: white; -fx-border-color: black;");

        Scene scene = new Scene(layout, 300, 250);
        scene.setOnKeyPressed(ev -> { if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) { pausePopup.close(); resumeAll(); this.requestFocus(); }});
        pausePopup.setScene(scene);
        pausePopup.setOnCloseRequest(ev -> timer.play());
        pausePopup.show();
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
        cell.setText("");
        cell.setGraphic(null);

        // Player
        if (r == playerRow && c == playerCol) {
            Image img = switch (name) { case "Patrick" -> patrickImg; case "Squidward" -> squidWardImg; default -> spongebobImg; };
            String style = player.hasShield()
                    ? baseStyle + "-fx-border-color: #00E5FF; -fx-border-width: 4px; -fx-border-radius: 100; -fx-background-radius: 100;"
                    : baseStyle;
            if (img != null) { cell.setStyle(style); cell.setGraphic(makeCellImage(img)); }
            else             { cell.setStyle(style + "-fx-background-color: #fff176; -fx-font-weight: bold;"); cell.setText("S"); }
            return;
        }

        // Enemy
        Enemy en = enemyAt(r, c);
        if (en != null) {
            Image img = enemyImage(en);
            String style = en.isShielded()
                    ? "-fx-border-color: #00E5FF; -fx-border-width: 4px; -fx-border-radius: 100; -fx-background-radius: 100; -fx-background-color: #dcedc8;"
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

        // Seaweed
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) {
            if (seaweedImg != null) { cell.setStyle(baseStyle); cell.setGraphic(makeCellImage(seaweedImg)); }
            else { cell.setStyle("-fx-background-color: #66bb6a; -fx-border-color: #2e7d32; -fx-font-weight: bold;"); cell.setText("W"); }
            return;
        }

        // Buff
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

    private Image buffImage(Buff b) {
        if (b instanceof MaxBombBuff)   return maxBombImg;
        if (b instanceof BombRangeBuff) return bombRangeImg;
        if (b instanceof BombDamageBuff)return bombDamageImg;
        if (b instanceof ShieldBuff)    return bubbleShieldImg;
        if (b instanceof HealBuff)      return healImg;
        return null;
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

    // ═══════════════════════════════════════════════════════════════════════
    // UI HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private ImageView makeCellImage(Image img) {
        ImageView iv = new ImageView(img);
        double size = Math.max(cellSize - 4, 8);
        iv.setFitWidth(size);
        iv.setFitHeight(size);
        iv.setPreserveRatio(true);
        return iv;
    }

    public ImageView createSkillImage(String skillName) {
        Image img = new Image(getClass().getResourceAsStream("/images/" + skillName));
        ImageView iv = new ImageView(img);
        double size = 54.0;
        iv.setFitWidth(size); iv.setFitHeight(size); iv.setPreserveRatio(false);
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(size/2, size/2, size/2);
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

    private void updateBadge(Label badge, int count) {
        badge.setText(String.valueOf(count));
        badge.setVisible(count > 0);
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
            long remaining = Math.max(0, (s.getLastSkillUseTime() + (long)s.getCooldown()*1000L
                    - System.currentTimeMillis()) / 1000);
            skillBtn.setText(remaining + "s");
            skillBtn.setDisable(true);
            skillBtn.setOpacity(0.7);
        } else {
            skillBtn.setText("Skill");
            skillBtn.setDisable(false);
            skillBtn.setOpacity(1.0);
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
    // PUBLIC API (called by other controllers)
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