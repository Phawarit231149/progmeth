package gui;

import game.Element;
import game.buff.*;
import game.character.*;
import game.character.Character;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.entity.EasyEnemy;
import game.entity.Enemy;
import game.entity.HardEnemy;
import game.entity.Level;
import game.entity.MediumEnemy;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import game.util.ElementUtil;
import game.util.SoundManager;

import java.util.ArrayList;
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

import java.util.Timer;

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
    private boolean[][] hasBomb;
    private Buff[][] buffMap;
    private boolean stopCharacter = false;

    // game state
    private int hearts = 5;
    private int kills = 0;
    //private final int goal = 30;
    private int bombsLeft = 5;
    private int maxBombs = 5;
    private int timeLeft = 300; // 5 นาที

    // Badge counters for buff icons (right panel)
    private int maxBombCount = 0;
    private int bombRangeCount = 0;
    private int bombDamageCount = 0;
    private int shieldCount = 0;
    private int healCount = 0;

    // Badge labels (so we can update them)
    private Label maxBombBadge;
    private Label bombRangeBadge;
    private Label bombDamageBadge;
    private Label shieldBadge;
    private Label healBadge;

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
    private Button skillBtn;
    private Button infoBtn;

    //Button Style
    private final String normalStyle = "-fx-background-radius: 40;";
    private final String pressedStyle = "-fx-background-radius: 40; -fx-border-color: red; -fx-border-width: 3px; -fx-border-radius: 40;";

    //Tile Style
    private String baseStyle = "-fx-background-color: #dcedc8; -fx-border-color: #aed581;";
    // ── Enemies ──────────────────────────────
    private List<Enemy> enemies = new ArrayList<>();
    private Timeline enemyTimer;
    private Timeline spawnTimer;            // ใช้สำหรับ spawn enemy ทีละตัวตามช่วงเวลา
    private int totalSpawned = 0;           // นับ enemy ทั้งหมดที่เคย spawn ใน stage นี้
    private int stagePhase   = 1;           // ใช้กับ Stage 5 (1 = medium phase, 2 = hard phase)
    private boolean phase2Started = false;  // กัน trigger ซ้ำ
    private final ElementUtil elementUtil = new ElementUtil();   // ใช้คำนวณดาเมจตามธาตุ
    // dr/dc สำหรับ 4 ทิศ: 0=up, 1=down, 2=left, 3=right
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = { 0, 0,-1, 1};
    private boolean stopEnemy = false;

    // ── Images (โหลดจาก resources/) ────────────
    private Image spongebobImg;
    private Image patrickImg;
    private Image squidWardImg;
    // enemy images
    private Image npcImg;             // Easy enemy
    private Image mrKrabImg;          // Medium FIRE
    private Image garyImg;            // Medium WATER
    private Image sandyImg;           // Medium ELECTRIC
    private Image kingNeptuneImg;     // Hard enemy
    private Image spawnImg;           // marker ของจุด spawn
    private Image rockImg;
    private Image seaweedImg;
    private Image bombImg;
    private Image maxBombImg;
    private Image bombRangeImg;
    private Image bombDamageImg;
    private Image bubbleShieldImg;
    private Image healImg;

    private double cellSize;       // เก็บไว้ใช้กับ ImageView

    private AudioClip explodeSfx;

    public GameController(StageData config, String name) {
        this.config = config;
        this.name = name;
        if(name.equals("Patrick")){this.element = Element.FIRE;}
        if(name.equals("Squidward")){this.element = Element.WATER;}
        if(name.equals("SpongeBob")){this.element = Element.ELECTRIC;}

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

        this.setFocusTraversable(true);

        this.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    if(!stopCharacter){tryMove(-1, 0); break;}

                case S:
                    if(!stopCharacter){tryMove(1, 0); break;}
                case A:
                    if(!stopCharacter){tryMove(0, -1); break;}
                case D:
                    if(!stopCharacter){tryMove(0, 1); break;}
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
                case K: // Example: Press K to use skill
                    if(player.isSkillReady()){
                        skillBtn.setStyle(pressedStyle);
                    }
                    if (player instanceof FireCharacter fire) {
                        if (fire.isSkillReady()) {
                            this.stopEnemy = true;
                            this.stopCharacter = true;
                            timer.stop();
                            fire.useSkill();
                            setBaseStyle("-fx-background-color: #cfd8dc; -fx-border-color: #b0bec5;");
                            for(int i = 0; i < config.getRows(); i++){
                                for(int j = 0; j < config.getCols(); j++){
                                    cells[i][j].setStyle(baseStyle);
                                }
                            }
                            // Start a 2-second countdown to disarm if no click happens
                            Timeline disarmTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                                if (fire.isTeleportArmed()) {
                                    fire.cancelTeleport();
                                    setBaseStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                                    for(int i = 0; i < config.getRows(); i++){
                                        for(int j = 0; j < config.getCols(); j++){
                                            cells[i][j].setStyle(baseStyle);
                                        }
                                    }
                                    timer.play();
                                    this.stopEnemy = false;
                                    this.stopCharacter = false;
                                }
                            }));
                            disarmTimer.play();
                        }
                    }

                    if(player instanceof WaterCharacter water){
                        if(water.isSkillReady()){
                            water.useSkill();
                            styleCell(playerRow, playerCol);
                            skillBtn.setDisable(true);

                            // Show green ✓ on shield badge when skill activates shield
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
                        }
                    }

                    if(player instanceof ElectricCharacter electric){
                        if(electric.isSkillReady()){
                            this.stopEnemy = true;

                            setBaseStyle("-fx-background-color: #cfd8dc; -fx-border-color: #b0bec5;");
                            for(int i = 0; i < config.getRows(); i++){
                                for(int j = 0; j < config.getCols(); j++){
                                    cells[i][j].setStyle(baseStyle);
                                }
                            }

                            Timeline stunTimer = new Timeline(new KeyFrame(
                                    Duration.millis(ElectricCharacter.getStunDurationMs()),
                                    e -> {
                                        this.stopEnemy = false;
                                        setBaseStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                                        for(int i = 0; i < config.getRows(); i++){
                                            for(int j = 0; j < config.getCols(); j++){
                                                cells[i][j].setStyle(baseStyle);
                                            }
                                        }
                                    }
                            ));
                            stunTimer.play();
                            electric.useSkill();
                        }
                    }
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
                case K: skillBtn.setStyle(normalStyle); break;
                default: break;
            }
        });
    }

    // ── Load images safely (null ถ้าไม่เจอ) ─────────────
    private void loadImages() {
        spongebobImg = tryLoadImage("/images/gamePlay/spongebob.png");   // ใช้ชื่อไฟล์ตามที่ใส่ใน resources
        patrickImg    = tryLoadImage("/images/gamePlay/patrick.png");
        squidWardImg = tryLoadImage("/images/gamePlay/squidward.png");
        // enemies
        npcImg          = tryLoadImage("/images/gamePlay/npc.png");           // Easy
        mrKrabImg       = tryLoadImage("/images/gamePlay/mrKrab.png");        // Medium FIRE
        garyImg         = tryLoadImage("/images/gamePlay/gary.png");          // Medium WATER
        sandyImg        = tryLoadImage("/images/gamePlay/sandy.png");         // Medium ELECTRIC
        kingNeptuneImg  = tryLoadImage("/images/gamePlay/kingneptune.png");   // Hard
        spawnImg        = tryLoadImage("/images/gamePlay/spawn.png");         // spawn marker
        rockImg      = tryLoadImage("/images/gamePlay/rock.png");
        seaweedImg   = tryLoadImage("/images/gamePlay/seaweed.png");
        bombImg      = tryLoadImage("/images/gamePlay/bomb.png");

        maxBombImg = tryLoadImage("/images/buffIcon/increaseMaximumBomb.png");
        bombRangeImg = tryLoadImage("/images/buffIcon/increaseBombRange.png");
        bombDamageImg = tryLoadImage("/images/buffIcon/increaseBombDamage.png");
        bubbleShieldImg = tryLoadImage("/images/buffIcon/bubbleShield.png");
        healImg       = tryLoadImage("/images/buffIcon/heal.png");
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
        // อ่าน spawn point จาก layout (ตัว 'C' ใน StageData)
        int[] spawn = config.getPlayerSpawn();
        playerRow = spawn[0];
        playerCol = spawn[1];
        player.setPos(playerCol, playerRow);
    }

    // ── Setup map with rocks and seaweeds ───────────────
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

                    // Random buff hidden under seaweed (revealed when destroyed)
                    double chance = Math.random();
                    if      (chance < 0.1) buffMap[r][c] = new MaxBombBuff(r, c);
                    else if (chance < 0.2) buffMap[r][c] = new BombRangeBuff(r, c);
                    else if (chance < 0.3) buffMap[r][c] = new BombDamageBuff(r, c);
                    else if (chance < 0.4) buffMap[r][c] = new ShieldBuff(r, c);
                    else if (chance < 0.5) buffMap[r][c] = new HealBuff(r, c);
                }
            }
        }

        // Spawn exactly one of each buff type on a random free empty tile
        Class<?>[] buffTypes = {
                MaxBombBuff.class,
                BombRangeBuff.class,
                BombDamageBuff.class,
                HealBuff.class,
                ShieldBuff.class
        };

        for (Class<?> buffType : buffTypes) {
            int[] pos = randomFreeBuffTile(rows, cols);
            if (pos == null) continue;
            int r = pos[0], c = pos[1];

            if      (buffType == MaxBombBuff.class)   buffMap[r][c] = new MaxBombBuff(r, c);
            else if (buffType == BombRangeBuff.class)  buffMap[r][c] = new BombRangeBuff(r, c);
            else if (buffType == BombDamageBuff.class) buffMap[r][c] = new BombDamageBuff(r, c);
            else if (buffType == HealBuff.class)       buffMap[r][c] = new HealBuff(r, c);
            else if (buffType == ShieldBuff.class)     buffMap[r][c] = new ShieldBuff(r, c);
        }
    }

    /** Returns a random tile that is: empty (not Rock, not Seaweed, not player/enemy spawn, not already has a buff) */
    private int[] randomFreeBuffTile(int rows, int cols) {
        for (int attempt = 0; attempt < 500; attempt++) {
            int r = (int)(Math.random() * rows);
            int c = (int)(Math.random() * cols);

            char ch = config.tileAt(r, c);
            if (ch != '.') continue;                          // must be plain empty tile
            if (map[r][c] instanceof Rock) continue;          // no rocks
            if (seaweeds[r][c] != null) continue;             // no seaweed tiles
            if (buffMap[r][c] != null) continue;              // no existing buff
            if (r == playerRow && c == playerCol) continue;   // not on player spawn

            return new int[]{r, c};
        }
        return null; // no free tile found after 500 attempts
    }

    // ── Setup enemies ตาม stage ──────────────────────
    //   Stage 1: 5 Easy initial → ทุก 10 วิ medium 1 ตัวจนรวม 10
    //   Stage 2: 7 Medium initial → ทุก 10 วิ medium 1 ตัวจนรวม 15
    //   Stage 3: 5 Medium (บางตัวมี shield) → ทุก 10 วิ จนรวม 20
    //   Stage 4: 7 Medium (บางตัวมี shield) → ทุก 10 วิ จนรวม 25
    //   Stage 5: 10 Medium → ทุก 10 วิ จน kill 25 → reset map + 2 Hard + ทุก 30 วิ จนรวม 30
    private void setupEnemies() {
        enemies        = new ArrayList<>();
        totalSpawned   = 0;
        stagePhase     = 1;
        phase2Started  = false;

        switch (config.getLevel()) {
            case 1:
                spawnInitialEasy(5);
                break;
            case 2:
                spawnInitialMedium(7, false);
                break;
            case 3:
                spawnInitialMedium(5, true);   // some shielded
                break;
            case 4:
                spawnInitialMedium(7, true);   // some shielded
                break;
            case 5:
                spawnInitialMedium(10, false);
                break;
        }
    }

    // ── Initial spawn helpers (random, ห่างจาก player) ──
    private void spawnInitialEasy(int count) {
        for (int i = 0; i < count; i++) {
            int[] pos = randomWalkableNotNearPlayer(3);
            if (pos == null) break;
            EasyEnemy e = new EasyEnemy(1, pos[1], pos[0], false);
            enemies.add(e);
            totalSpawned++;
        }
    }

    private void spawnInitialMedium(int count, boolean someShielded) {
        for (int i = 0; i < count; i++) {
            int[] pos = randomWalkableNotNearPlayer(3);
            if (pos == null) break;
            Element elem  = randomElement();
            boolean shield = someShielded && Math.random() < 0.4;   // ~40% มี shield
            MediumEnemy e = new MediumEnemy(1, pos[1], pos[0], elem, shield);
            enemies.add(e);
            totalSpawned++;
        }
    }

    private Element randomElement() {
        Element[] elems = { Element.FIRE, Element.WATER, Element.ELECTRIC };
        return elems[(int)(Math.random() * elems.length)];
    }

    // หาช่องสุ่มที่เดินได้ + ห่างจาก player อย่างน้อย minDist (Manhattan)
    private int[] randomWalkableNotNearPlayer(int minDist) {
        int rows = config.getRows();
        int cols = config.getCols();
        for (int attempt = 0; attempt < 200; attempt++) {
            int r = (int)(Math.random() * rows);
            int c = (int)(Math.random() * cols);
            if (!isTileFreeForSpawn(r, c)) continue;
            int dist = Math.abs(r - playerRow) + Math.abs(c - playerCol);
            if (dist < minDist) continue;
            return new int[]{r, c};
        }
        return null;
    }

    // ช่องที่ "ว่างพอจะ spawn enemy ได้"
    private boolean isTileFreeForSpawn(int r, int c) {
        if (r < 0 || r >= config.getRows() || c < 0 || c >= config.getCols()) return false;
        if (map[r][c] instanceof Rock) return false;
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) return false;
        if (hasBomb[r][c]) return false;
        if (r == playerRow && c == playerCol) return false;
        for (Enemy en : enemies) {
            if (enemyOccupiesTile(en, r, c)) return false;
        }
        return true;
    }

    // ตรวจว่า Hard ลง 2×2 ตรง anchor (r,c) ได้มั้ย
    private boolean canHardOccupy(int r, int c) {
        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                if (!isTileFreeForSpawn(r + dr, c + dc)) return false;
            }
        }
        return true;
    }

    // ── Spawn timer (ทุก 10 วิ — Stage 5 phase 2 จะเปลี่ยนเป็น 30 วิ) ──
    private void startSpawnTimer() {
        spawnTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> tickSpawn()));
        spawnTimer.setCycleCount(Timeline.INDEFINITE);
        spawnTimer.play();
    }

    private void tickSpawn() {
        int level = config.getLevel();
        int cap = phase1Cap(level);

        // Stage 5 phase 2 → spawn Hard random
        if (level == 5 && stagePhase == 2) {
            if (totalSpawned >= 30) return;
            spawnHardRandom();
            return;
        }

        // ทุก stage phase 1 → spawn Medium ที่จุด P
        if (totalSpawned >= cap) return;
        boolean withShield = (level == 3 || level == 4);
        spawnMediumAtSpawnPoint(withShield);
    }

    // จำนวนสูงสุดที่ phase 1 จะ spawn (ก่อนเข้า phase 2 ของ stage 5)
    private int phase1Cap(int level) {
        switch (level) {
            case 1: return 10;
            case 2: return 15;
            case 3: return 20;
            case 4: return 25;
            case 5: return 25;   // phase 1 ของ stage 5 หยุดที่ 25 รอเข้า phase 2
        }
        return 0;
    }

    // ── Spawn 1 ตัวจากจุด P ──
    private void spawnMediumAtSpawnPoint(boolean canShield) {
        List<int[]> spawns = new ArrayList<>(config.getEnemySpawns());
        if (spawns.isEmpty()) {
            // ไม่มี P เลย — fallback random
            int[] pos = randomWalkableNotNearPlayer(3);
            if (pos == null) return;
            addMedium(pos[0], pos[1], canShield);
            return;
        }
        java.util.Collections.shuffle(spawns);
        for (int[] s : spawns) {
            if (isTileFreeForSpawn(s[0], s[1])) {
                addMedium(s[0], s[1], canShield);
                return;
            }
        }
    }

    private void addMedium(int row, int col, boolean canShield) {
        Element elem  = randomElement();
        boolean shield = canShield && Math.random() < 0.4;
        MediumEnemy e = new MediumEnemy(1, col, row, elem, shield);
        enemies.add(e);
        totalSpawned++;
        renderGrid();
    }

    // Spawn Hard random — Stage 5 phase 2 (ใช้ 2×2 บนแผนที่)
    private void spawnHardRandom() {
        int rows = config.getRows();
        int cols = config.getCols();
        for (int attempt = 0; attempt < 300; attempt++) {
            int r = (int)(Math.random() * (rows - 1));   // เผื่อ r+1 อยู่ในแผนที่
            int c = (int)(Math.random() * (cols - 1));
            if (!canHardOccupy(r, c)) continue;
            int dist = Math.abs(r - playerRow) + Math.abs(c - playerCol);
            if (dist < 3) continue;                      // ห่างจาก player พอสมควร
            Element elem = randomElement();
            HardEnemy e = new HardEnemy(2, c, r, elem, false);   // size=2, posX=col, posY=row (anchor = top-left)
            enemies.add(e);
            totalSpawned++;
            renderGrid();
            return;
        }
    }

    // ── Stage 5 — เข้า phase 2: เคลียร์ rock/seaweed + spawn 2 Hard + เปลี่ยน timer 30 วิ ──
    private void triggerStage5Phase2() {
        if (config.getLevel() != 5 || phase2Started) return;
        phase2Started = true;
        stagePhase = 2;

        int rows = config.getRows();
        int cols = config.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] instanceof Rock) {
                    map[r][c] = new Tile(r, c);     // เคลียร์ rock
                }
                if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) {
                    seaweeds[r][c].destroy();        // เคลียร์ seaweed
                }
                // (ไม่เคลียร์ buff — buff คงอยู่บนแผนที่เหมือนเดิม)
            }
        }

        // spawn 2 Hard ทันที
        spawnHardRandom();
        spawnHardRandom();

        // เปลี่ยน interval ของ spawnTimer เป็น 30 วินาที
        if (spawnTimer != null) {
            spawnTimer.stop();
            spawnTimer.getKeyFrames().clear();
            spawnTimer.getKeyFrames().add(new KeyFrame(Duration.seconds(30), e -> tickSpawn()));
            spawnTimer.play();
        }

        renderGrid();
    }

    // เช็กว่า enemy ตัวนี้ครอบคลุมช่อง (r,c) หรือไม่ — Hard ใช้ 2×2
    private boolean enemyOccupiesTile(Enemy e, int r, int c) {
        int er = e.getPosY();
        int ec = e.getPosX();
        if (e.getLevel() == Level.HARD) {
            return (r == er || r == er + 1) && (c == ec || c == ec + 1);
        }
        return r == er && c == ec;
    }

    // คืน Enemy ที่อยู่ตำแหน่ง (r,c) — ไม่มี → null
    private Enemy enemyAt(int r, int c) {
        for (Enemy e : enemies) {
            if (enemyOccupiesTile(e, r, c)) return e;
        }
        return null;
    }

    // เลือกรูปของ enemy ตาม Level + Element
    private Image enemyImage(Enemy e) {
        Level lvl = e.getLevel();
        if (lvl == null) return npcImg;
        switch (lvl) {
            case EASY:
                return npcImg;
            case MEDIUM:
                if (e.getElement() == null) return mrKrabImg;
                switch (e.getElement()) {
                    case FIRE:     return mrKrabImg;
                    case WATER:    return garyImg;
                    case ELECTRIC: return sandyImg;
                    default:       return mrKrabImg;
                }
            case HARD:
                return kingNeptuneImg;
            default:
                return npcImg;
        }
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
    private void moveEnemy(Enemy e) {
        if(!stopEnemy){
            if (e.getLevel() == Level.HARD) {
                moveHardFollowPlayer(e);
            } else {
                moveRandom(e);
            }
            // ดาเมจ player ทำใน checkPlayerEnemyCollision() ที่ enemyTimer เรียกอยู่แล้ว
        }
    }

    private void moveRandom(Enemy e) {
        int dir = e.getCurrentDir();
        int r = e.getPosY();
        int c = e.getPosX();

        // 1. ลองเดินทางตรงต่อ
        int nr = r + DR[dir];
        int nc = c + DC[dir];
        if (canEnemyWalk(nr, nc, e)) {
            e.setPosY(nr);
            e.setPosX(nc);
            return;
        }

        // 2. ทางตัน → หาตัวเลือกใหม่ ยกเว้นทิศเดิมและทิศตรงข้าม
        int reverse = dir ^ 1;
        List<Integer> options = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            if (d == dir || d == reverse) continue;
            int rr = r + DR[d];
            int cc = c + DC[d];
            if (canEnemyWalk(rr, cc, e)) options.add(d);
        }

        int newDir;
        if (!options.isEmpty()) {
            newDir = options.get((int)(Math.random() * options.size()));
        } else {
            int rr = r + DR[reverse];
            int cc = c + DC[reverse];
            if (canEnemyWalk(rr, cc, e)) {
                newDir = reverse;
            } else {
                return;
            }
        }

        e.setCurrentDir(newDir);
        e.setPosY(r + DR[newDir]);
        e.setPosX(c + DC[newDir]);
    }

    // Hard เดินตาม player แบบ greedy — ลองทุกทิศ เลือกอันที่ใกล้ player ที่สุด
    private void moveHardFollowPlayer(Enemy e) {
        int r = e.getPosY();
        int c = e.getPosX();
        int bestDir = -1;
        int bestDist = Integer.MAX_VALUE;

        // ลำดับทิศสุ่ม → ถ้าเสมอจะไม่เลือก up เสมอ
        int[] order = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int t = order[i]; order[i] = order[j]; order[j] = t;
        }

        for (int d : order) {
            int nr = r + DR[d];
            int nc = c + DC[d];
            if (!canHardWalk(nr, nc, e)) continue;
            // ระยะจากใจกลาง 2×2 ใหม่ ถึง player (ใช้ Manhattan แบบหยาบ)
            int dist = Math.abs(nr - playerRow) + Math.abs(nc - playerCol);
            if (dist < bestDist) {
                bestDist = dist;
                bestDir  = d;
            }
        }

        if (bestDir >= 0) {
            e.setCurrentDir(bestDir);
            e.setPosY(r + DR[bestDir]);
            e.setPosX(c + DC[bestDir]);
        }
    }

    private void tickEnemies() {
        for (Enemy e : enemies) moveEnemy(e);
        renderGrid();
    }

    private void startEnemyTimer() {
        enemyTimer = new Timeline(new KeyFrame(Duration.millis(600), e -> {
            tickEnemies();
            checkPlayerEnemyCollision();
        }));
        enemyTimer.setCycleCount(Timeline.INDEFINITE);
        enemyTimer.play();
    }

    // ── Pause / Resume ทั้ง main timer + enemy timer + spawn timer พร้อมกัน ──
    private void pauseAll() {
        Timeline t1 = timer;
        Timeline t2 = enemyTimer;
        Timeline t3 = spawnTimer;
        if (t1 != null) t1.stop();
        if (t2 != null) t2.stop();
        if (t3 != null) t3.stop();
    }

    private void resumeAll() {
        Timeline t1 = timer;
        Timeline t2 = enemyTimer;
        Timeline t3 = spawnTimer;
        if (t1 != null) t1.play();
        if (t2 != null) t2.play();
        if (t3 != null) t3.play();
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
            if(name.equals("Patrick")){playerImg  = patrickImg  ;}
            if(name.equals("Squidward")){playerImg = squidWardImg;}
            if(name.equals("SpongeBob")){playerImg = spongebobImg;}

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
            if (seaweedImg != null) {
                cell.setStyle(baseStyle);
                cell.setGraphic(makeCellImage(seaweedImg));
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
            pauseAll();
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
            resumeAll();
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
                resumeAll();
                this.requestFocus();
            }
        });

        infoPopup.setScene(infoScene);

        pauseAll();

        infoPopup.show();
    }

    private void showPauseMenu() {
        // 1. ถ้าเปิดอยู่แล้ว ให้ปิดและเล่นต่อ (Toggle Off)
        if (pausePopup != null && pausePopup.isShowing()) {
            pausePopup.close();
            resumeAll();
            return;
        }

        pauseAll();

        this.pausePopup = new Stage();
        pausePopup.initModality(Modality.APPLICATION_MODAL);
        pausePopup.setTitle("Paused");

        Label pauseLabel = new Label("GAME PAUSED");
        pauseLabel.setFont(Font.font(24));

        Button resumeBtn = new Button("Resume");
        resumeBtn.setPrefWidth(100);
        resumeBtn.setOnAction(e -> {
            pausePopup.close();
            resumeAll();
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
                resumeAll();
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
                cell.setFocusTraversable(false);

                // --- ADDED: Click Listener for Teleportation ---
                cell.setOnAction(e -> {
                    // 1. Check if the player is a FireCharacter and skill is active
                    if (player instanceof FireCharacter fire && fire.isTeleportArmed()) {

                        // 2. Identify what is at the target location
                        Tile targetTile = map[row][col];

                        // Check if there is an active seaweed block at the target
                        boolean seaweedExists = (seaweeds[row][col] != null && !seaweeds[row][col].isDestroyed());
                        if (seaweedExists) {
                            targetTile = seaweeds[row][col];
                        }

                        // 3. Attempt the teleport via the Character model
                        // We pass 'false' for hasEnemy here unless you have an enemy array to check
                        if (fire.teleportTo(col, row, targetTile, false)) {
                            // 4. Update the Controller's logic to match the Model
                            playerRow = row;
                            playerCol = col;
                            renderGrid(); // Redraw immediately
                            setBaseStyle("-fx-background-color: #dcedc8; -fx-border-color: #aed581;");
                            for (int i = 0; i < config.getRows(); i++) {
                                for (int j = 0; j < config.getCols(); j++) {
                                    cells[i][j].setStyle(baseStyle);
                                }
                            }
                        }
                        this.stopEnemy = false;
                        this.stopCharacter = false;
                        timer.play();

                    }
                });
                // ------------------------------------------------

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
        maxBombBadge    = createBadgeLabel();
        bombRangeBadge  = createBadgeLabel();
        bombDamageBadge = createBadgeLabel();
        shieldBadge     = createBadgeLabel();
        healBadge       = createBadgeLabel();

        shieldBadge.setText("✗");
        shieldBadge.setStyle(
                "-fx-background-color: #e53935; " +
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

        StackPane s1 = createSkillWithBadge("buffIcon/increaseMaximumBomb.png", maxBombBadge);
        StackPane s2 = createSkillWithBadge("buffIcon/increaseBombRange.png",   bombRangeBadge);
        StackPane s3 = createSkillWithBadge("buffIcon/increaseBombDamage.png",  bombDamageBadge);
        StackPane s4 = createSkillWithBadge("buffIcon/bubbleShield.png",        shieldBadge);
        StackPane s5 = createSkillWithBadge("buffIcon/heal.png",                healBadge);

        rightCol.getChildren().addAll(s1, s2, s3, s4, s5);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        this.infoBtn = new Button("i");
        infoBtn.setPrefSize(36, 36);
        infoBtn.setFocusTraversable(false);
        infoBtn.setStyle("-fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e53935; -fx-border-width: 2;");
        infoBtn.setOnAction(e -> { pauseAll(); skillsInformation(); });

        Label info = new Label("[U]");
        info.setFont(Font.font(15));

        rightCol.getChildren().addAll(spacer, infoBtn, info);

        mainRightContainer.getChildren().addAll(leftCol, rightCol);
        return mainRightContainer;
    }

    private VBox createBombControls() {
        VBox bombContainer = new VBox(10);
        bombContainer.setAlignment(Pos.BOTTOM_CENTER);

        this.skillBtn = new Button("Skill");
        skillBtn.setPrefSize(80,80);
        skillBtn.setFocusTraversable(false);
        skillBtn.setStyle(normalStyle);
        skillBtn.setOnAction(e -> player.useSkill());

        Label skillLabel = new Label("");
        if(name.equals("Patrick")){skillLabel.setText("Teleport\n[K]");}
        if(name.equals("Squidward")){skillLabel.setText("Generate\nshield [K]");}
        if(name.equals("SpongeBob")){skillLabel.setText("Freeze all\nenemies [K]");}
        skillLabel.setFont(Font.font(15));

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

        bombContainer.getChildren().addAll(skillBtn,skillLabel,explodeBtn, explodeKey, plantBombBtn, bombLabel, plantKey);
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

            if (hearts <= 0){
                setGameStatus(Status.LOSE);
                gameOver();
            }
        }
    }

    // ── TIMER ───────────────────────────────────────────
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeLeft <= 0) {
                pauseAll();
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

            updateSkillButtonUI();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        resumeAll();
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
                                if (player.hasShield()){
                                    player.setShield(false);
                                    shieldBadge.setText("✗");
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
                                }
                                else if (! player.hasShield()){
                                    hearts -= player.getDamage();
                                    //player.takeDamage(player.getDamageBomb());
                                    updateHearts();
                                }
                                takeDamage = true;
                            }
                        }
                    }
                }
            }
        }

        // 1.5 ทำดาเมจ enemy ที่อยู่ในระยะระเบิด
        applyEnemyDamage(toExplode);

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

    // ── ระเบิดทำดาเมจ enemy ──
    //   ใช้ ElementUtil คำนวณดาเมจตามธาตุ (Easy = NONE → ผู้เล่นได้ x2)
    //   Hard = 2×2 → โดนบอม ถ้าระเบิดโดนช่องไหนใน 2×2 ก็นับเป็นโดน
    //   ถ้า enemy มี shield → กิน shield 1 ครั้ง ไม่เสีย HP
    //   ถ้า HP ≤ 0 → ลบออกจาก list, kills++
    private void applyEnemyDamage(boolean[][] toExplode) {
        Iterator<Enemy> it = enemies.iterator();
        while (it.hasNext()) {
            Enemy en = it.next();
            boolean hit = false;

            int rows = config.getRows();
            int cols = config.getCols();
            if (en.getLevel() == Level.HARD) {
                outer:
                for (int dr = 0; dr < 2; dr++) {
                    for (int dc = 0; dc < 2; dc++) {
                        int rr = en.getPosY() + dr;
                        int cc = en.getPosX() + dc;
                        if (rr >= 0 && rr < rows && cc >= 0 && cc < cols && toExplode[rr][cc]) {
                            hit = true;
                            break outer;
                        }
                    }
                }
            } else {
                int er = en.getPosY();
                int ec = en.getPosX();
                if (er >= 0 && er < rows && ec >= 0 && ec < cols && toExplode[er][ec]) {
                    hit = true;
                }
            }
            if (!hit) continue;

            // shield ของ enemy → กิน 1 ครั้งแล้วหาย
            if (en.isShielded()) {
                en.setShielded(false);
                continue;
            }

            int dmg = elementUtil.calculateCharacterDamage(player, en);
            en.setHealth(en.getHealth() - dmg);

            if (en.getHealth() <= 0) {
                it.remove();
                kills++;
                if(kills >= config.getGoal()){
                    if(config.getLevel() == 5){setGameStatus(Status.CLEAR);}
                    else{setGameStatus(Status.WIN);}
                    gameOver();
                    return;
                }
            }
        }
        // อัปเดตตัวเลขที่ top bar
        if (killLabel != null) killLabel.setText(kills + " / " + config.getGoal());

        // Stage 5 — ฆ่าครบ 25 → เข้า phase 2 (เคลียร์ rock/seaweed + 2 Hard)
        if (config.getLevel() == 5 && !phase2Started && kills >= 25) {
            triggerStage5Phase2();
        }
    }

    // ── enemy ทำดาเมจ player (เรียกตอน enemy ขยับมาทับ player หรือ player เดินไปชน enemy) ──
    //   ใช้ ElementUtil คำนวณดาเมจตามธาตุ (Strong x2 / Weak ÷2 / Same x1)
    //   ถ้า player มี shield → กิน 1 ครั้งแล้วหาย ไม่เสียเลือด
    private void damageFromEnemy(Enemy e) {
        if (player.hasShield()) {
            player.setShield(false);
            shieldBadge.setText("✗");
            shieldBadge.setStyle(
                    "-fx-background-color: #e53935; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 11px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-min-width: 18px; " +
                            "-fx-min-height: 18px; " +
                            "-fx-background-radius: 9; " +
                            "-fx-alignment: center; " +
                            "-fx-padding: 0 3 0 3;"
            );
            shieldBadge.setVisible(true);  // stays visible as ✗
            return;
        }
        int dmg = elementUtil.calculateEnemyDamage(e, player);
        hearts -= dmg;
        if (hearts < 0) hearts = 0;
        updateHearts();
    }

    private void checkPlayerEnemyCollision() {
        // ใช้ enemyOccupiesTile() — รองรับ Hard 2×2 ด้วย
        // ทำดาเมจครั้งเดียวต่อ tick (กันโดน Hard ตี 4 ครั้งต่อรอบเพราะกินพื้นที่ 4 ช่อง)
        for (Enemy e : enemies) {
            if (enemyOccupiesTile(e, playerRow, playerCol)) {
                damageFromEnemy(e);
                return;   // โดน 1 ตัวพอต่อ tick
            }
        }
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

    private void updateSkillButtonUI() {
        if (player instanceof Skillable s) {
            if (!s.isSkillReady()) {
                // Calculate remaining time
                long now = System.currentTimeMillis();
                long nextReady = s.getLastSkillUseTime() + (s.getCooldown() * 1000L);
                long remaining = Math.max(0, (nextReady - now) / 1000);

                skillBtn.setText(remaining + "s");
                skillBtn.setDisable(true); // Prevent clicking during cooldown
                skillBtn.setOpacity(0.7);   // Visual feedback for disabled
            } else {
                skillBtn.setText("Skill");
                skillBtn.setDisable(false);
                skillBtn.setOpacity(1.0);
            }
        }
    }

    /** Creates the red badge label (hidden by default) */
    private Label createBadgeLabel() {
        Label badge = new Label("0");
        badge.setStyle(
                "-fx-background-color: #e53935; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 18px; " +
                        "-fx-min-height: 18px; " +
                        "-fx-background-radius: 9; " +
                        "-fx-alignment: center; " +
                        "-fx-padding: 0 3 0 3;"
        );
        badge.setVisible(false);  // hide when count is 0
        return badge;
    }

    /** Wraps a skill ImageView in a StackPane with a badge in top-right corner */
    private StackPane createSkillWithBadge(String skillName, Label badge) {
        ImageView icon = createSkillImage(skillName);
        StackPane stack = new StackPane(icon, badge);
        stack.setPrefSize(54, 54);
        StackPane.setAlignment(badge, javafx.geometry.Pos.TOP_RIGHT);
        // Shift the badge slightly outside the icon corner
        StackPane.setMargin(badge, new javafx.geometry.Insets(-4, -4, 0, 0));
        return stack;
    }

    private void updateBadge(Label badge, int count) {
        badge.setText(String.valueOf(count));
        badge.setVisible(count > 0);
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
        if (gameStatus == Status.WIN || gameStatus == Status.CLEAR) {
            GameProgress.markCleared(config.getLevel() - 1); // level 1 → index 0, level 5 → index 4
        }
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
        if (hearts < 0) {this.hearts = 0;}
        if(hearts > 5){ this.hearts = 5;}
        else{this.hearts = hearts;}
    }

    public void setBaseStyle(String baseStyle) {
        this.baseStyle = baseStyle;
    }
}
