package game.entity;

import game.Element;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import model.StageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Owns all enemy-spawn logic that previously cluttered GameController.
 *
 * GameController calls:
 *   spawner.setupInitial()  — once, at game start
 *   spawner.tick()          — every 10 s (or 30 s in phase 2)
 *   spawner.triggerPhase2() — when Stage-5 kill threshold is hit
 */
public class EnemySpawner {

    // ── Fields ────────────────────────────────
    private final StageData   config;
    private final List<Enemy> enemies;
    private final Tile[][]    map;
    private final Seaweed[][] seaweeds;
    private final boolean[][] hasBomb;

    private int     totalSpawned  = 0;
    private int     stagePhase    = 1;
    private boolean phase2Started = false;

    private int playerRow;
    private int playerCol;

    private static final Random RNG = new Random();

    // ── Constructor ───────────────────────────
    public EnemySpawner(StageData config, List<Enemy> enemies,
                        Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb) {
        this.config   = config;
        this.enemies  = enemies;
        this.map      = map;
        this.seaweeds = seaweeds;
        this.hasBomb  = hasBomb;
    }

    // ── Called by GameController each step ────
    public void setPlayerPos(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
    }

    // ═══════════════════════════════════════════
    // INITIAL SPAWN
    // ═══════════════════════════════════════════

    /** Populate the first wave based on stage level. */
    public void setupInitial() {
        totalSpawned  = 0;
        stagePhase    = 1;
        phase2Started = false;

        switch (config.getLevel()) {
            case 1 -> spawnInitialEasy(5);
            case 2 -> spawnInitialMedium(7,  false);
            case 3 -> spawnInitialMedium(5,  true);
            case 4 -> spawnInitialMedium(7,  true);
            case 5 -> spawnInitialMedium(10, false);
        }
    }

    private void spawnInitialEasy(int count) {
        for (int i = 0; i < count; i++) {
            int[] pos = randomWalkable(3);
            if (pos == null) break;
            enemies.add(new EasyEnemy(1, pos[1], pos[0], false));
            totalSpawned++;
        }
    }

    private void spawnInitialMedium(int count, boolean someShielded) {
        for (int i = 0; i < count; i++) {
            int[] pos = randomWalkable(3);
            if (pos == null) break;
            enemies.add(new MediumEnemy(1, pos[1], pos[0],
                    randomElement(), someShielded && RNG.nextDouble() < 0.4));
            totalSpawned++;
        }
    }

    // ═══════════════════════════════════════════
    // PERIODIC TICK  (called by spawnTimer)
    // ═══════════════════════════════════════════

    /** Returns true if the grid needs a re-render after this tick. */
    public boolean tick() {
        int level = config.getLevel();
        if (level == 5 && stagePhase == 2) {
            if (totalSpawned < 30) { spawnHardRandom(); return true; }
            return false;
        }
        if (totalSpawned < phase1Cap(level)) {
            spawnMediumAtSpawnPoint(level == 3 || level == 4);
            return true;
        }
        return false;
    }

    private int phase1Cap(int level) {
        return switch (level) {
            case 1 -> 10; case 2 -> 15; case 3 -> 20; case 4 -> 25; default -> 25;
        };
    }

    // ═══════════════════════════════════════════
    // PHASE 2  (Stage 5 only)
    // ═══════════════════════════════════════════

    /**
     * Clears all obstacles and switches to Hard-enemy spawning.
     * Returns true on first trigger; false if already in phase 2.
     */
    public boolean triggerPhase2() {
        if (config.getLevel() != 5 || phase2Started) return false;
        phase2Started = true;
        stagePhase    = 2;

        int rows = config.getRows(), cols = config.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] instanceof Rock) map[r][c] = new Tile(r, c);
                if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed())
                    seaweeds[r][c].destroy();
            }
        }
        spawnHardRandom();
        spawnHardRandom();
        return true;
    }

    public boolean isPhase2Started() { return phase2Started; }
    public int     getStagePhase()   { return stagePhase;    }

    // ═══════════════════════════════════════════
    // SPAWN HELPERS
    // ═══════════════════════════════════════════

    private void spawnMediumAtSpawnPoint(boolean canShield) {
        List<int[]> spawns = new ArrayList<>(config.getEnemySpawns());
        if (spawns.isEmpty()) {
            int[] pos = randomWalkable(3);
            if (pos != null) addMedium(pos[0], pos[1], canShield);
            return;
        }
        Collections.shuffle(spawns);
        for (int[] s : spawns) {
            if (isFreeForSpawn(s[0], s[1])) { addMedium(s[0], s[1], canShield); return; }
        }
    }

    private void addMedium(int row, int col, boolean canShield) {
        enemies.add(new MediumEnemy(1, col, row,
                randomElement(), canShield && RNG.nextDouble() < 0.4));
        totalSpawned++;
    }

    private void spawnHardRandom() {
        int rows = config.getRows(), cols = config.getCols();
        for (int i = 0; i < 300; i++) {
            int r = RNG.nextInt(rows - 1);
            int c = RNG.nextInt(cols - 1);
            if (!canHardOccupy(r, c)) continue;
            if (Math.abs(r - playerRow) + Math.abs(c - playerCol) < 3) continue;
            enemies.add(new HardEnemy(2, c, r, randomElement(), false));
            totalSpawned++;
            return;
        }
    }

    // ═══════════════════════════════════════════
    // TILE / POSITION UTILITIES
    // ═══════════════════════════════════════════

    public boolean isFreeForSpawn(int r, int c) {
        int rows = config.getRows(), cols = config.getCols();
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        if (map[r][c] instanceof Rock) return false;
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) return false;
        if (hasBomb[r][c]) return false;
        if (r == playerRow && c == playerCol) return false;
        for (Enemy e : enemies) if (e.occupiesTile(r, c)) return false;
        return true;
    }

    private boolean canHardOccupy(int r, int c) {
        for (int dr = 0; dr < 2; dr++)
            for (int dc = 0; dc < 2; dc++)
                if (!isFreeForSpawn(r + dr, c + dc)) return false;
        return true;
    }

    private int[] randomWalkable(int minDistFromPlayer) {
        int rows = config.getRows(), cols = config.getCols();
        for (int i = 0; i < 200; i++) {
            int r = RNG.nextInt(rows);
            int c = RNG.nextInt(cols);
            if (!isFreeForSpawn(r, c)) continue;
            if (Math.abs(r - playerRow) + Math.abs(c - playerCol) < minDistFromPlayer) continue;
            return new int[]{r, c};
        }
        return null;
    }

    private Element randomElement() {
        Element[] elems = {Element.FIRE, Element.WATER, Element.ELECTRIC};
        return elems[RNG.nextInt(elems.length)];
    }
}