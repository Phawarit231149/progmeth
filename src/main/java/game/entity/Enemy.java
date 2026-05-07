package game.entity;

import game.Element;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Base class for all enemies.
 *
 * Movement logic lives here (it only needs map/collision context passed in),
 * keeping GameController responsible only for orchestration and rendering.
 */
public abstract class Enemy {

    // ── Direction index constants ─────────────
    public static final int DIR_UP    = 0;
    public static final int DIR_DOWN  = 1;
    public static final int DIR_LEFT  = 2;
    public static final int DIR_RIGHT = 3;

    private static final int[] DR = {-1, 1,  0, 0};
    private static final int[] DC = { 0, 0, -1, 1};

    // ── Fields ────────────────────────────────
    private int health;
    private int damage;
    private int size;
    private int posX;
    private int posY;
    private Element element;
    private boolean shielded;
    private Level level;
    private int currentDir;
    private boolean isFreezed;

    protected static final Random RNG = new Random();

    // ── Constructor ───────────────────────────
    public Enemy(int size, int posX, int posY, Element element, boolean shielded) {
        this.size       = size;
        this.posX       = posX;
        this.posY       = posY;
        this.element    = element;
        this.shielded   = shielded;
        this.currentDir = RNG.nextInt(4);
        this.isFreezed = false;
    }

    // ═══════════════════════════════════════════
    // MOVEMENT  (called by GameController each tick)
    // ═══════════════════════════════════════════

    /**
     * Move this enemy one step.
     * Subclasses override to choose a movement strategy.
     *
     * @param map        tile grid
     * @param seaweeds   seaweed grid
     * @param hasBomb    bomb grid
     * @param enemies    all enemies (for collision avoidance)
     * @param playerRow  player's current row (used by HARD follow-AI)
     * @param playerCol  player's current col (used by HARD follow-AI)
     * @param rows       grid height
     * @param cols       grid width
     */
    public abstract void move(Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb,
                              List<Enemy> enemies,
                              int playerRow, int playerCol,
                              int rows, int cols);

    // ── Random wandering (Easy / Medium) ──────

    /**
     * Continue in the current direction; turn on obstacle; reverse as last resort.
     */
    protected void moveRandom(Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb,
                              List<Enemy> enemies, int rows, int cols) {
        int dir = currentDir;
        int r = posY, c = posX;

        // Try to keep going straight
        if (canWalk(r + DR[dir], c + DC[dir], map, seaweeds, hasBomb, enemies, rows, cols)) {
            posY = r + DR[dir];
            posX = c + DC[dir];
            return;
        }

        // Try perpendicular directions
        int reverse = dir ^ 1;
        List<Integer> options = new ArrayList<>();
        for (int d = 0; d < 4; d++) {
            if (d == dir || d == reverse) continue;
            if (canWalk(r + DR[d], c + DC[d], map, seaweeds, hasBomb, enemies, rows, cols))
                options.add(d);
        }

        int newDir;
        if (!options.isEmpty()) {
            newDir = options.get(RNG.nextInt(options.size()));
        } else if (canWalk(r + DR[reverse], c + DC[reverse], map, seaweeds, hasBomb, enemies, rows, cols)) {
            newDir = reverse;
        } else {
            return; // completely stuck
        }
        currentDir = newDir;
        posY = r + DR[newDir];
        posX = c + DC[newDir];
    }

    // ── Player-following (Hard) ────────────────

    /**
     * Move toward the player using Manhattan-distance greedy search.
     * Direction order is shuffled to break ties randomly.
     */
    protected void moveFollowPlayer(Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb,
                                    List<Enemy> enemies,
                                    int playerRow, int playerCol,
                                    int rows, int cols) {
        int r = posY, c = posX;
        int bestDir = -1, bestDist = Integer.MAX_VALUE;

        int[] order = {0, 1, 2, 3};
        // Fisher-Yates shuffle for random tie-breaking
        for (int i = 3; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            int t = order[i]; order[i] = order[j]; order[j] = t;
        }

        for (int d : order) {
            int nr = r + DR[d], nc = c + DC[d];
            if (!canWalkHard(nr, nc, map, seaweeds, hasBomb, enemies, rows, cols)) continue;
            int dist = Math.abs(nr - playerRow) + Math.abs(nc - playerCol);
            if (dist < bestDist) { bestDist = dist; bestDir = d; }
        }

        if (bestDir >= 0) {
            currentDir = bestDir;
            posY = r + DR[bestDir];
            posX = c + DC[bestDir];
        }
    }

    // ═══════════════════════════════════════════
    // COLLISION HELPERS
    // ═══════════════════════════════════════════

    /**
     * Whether this enemy (1×1) can step onto (r,c).
     */
    public boolean canWalk(int r, int c, Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb, List<Enemy> enemies, int rows, int cols) {
        if(isFreezed) return false;
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        if (map[r][c] instanceof Rock) return false;
        if (seaweeds[r][c] != null && !seaweeds[r][c].isDestroyed()) return false;
        //if (hasBomb[r][c]) return false;
        for (Enemy o : enemies) {
            if (o != this && o.occupiesTile(r, c)) return false;
        }
        return true;
    }

    /**
     * Whether a 2×2 Hard enemy can anchor at top-left (nr, nc).
     */
    public boolean canWalkHard(int nr, int nc, Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb, List<Enemy> enemies, int rows, int cols) {
        if (nr < 0 || nr + 1 >= rows || nc < 0 || nc + 1 >= cols) return false;
        for (int dr = 0; dr < 2; dr++)
            for (int dc = 0; dc < 2; dc++)
                if (!canWalk(nr + dr, nc + dc, map, seaweeds, hasBomb, enemies, rows, cols))
                    return false;
        return true;
    }

    /**
     * True if this enemy occupies tile (r, c).
     * Hard enemies occupy a 2×2 area; all others occupy exactly one tile.
     */
    public boolean occupiesTile(int r, int c) {
        if (level == Level.HARD)
            return (r == posY || r == posY + 1) && (c == posX || c == posX + 1);
        return r == posY && c == posX;
    }

    // ═══════════════════════════════════════════
    // BLAST-ZONE CHECK
    // ═══════════════════════════════════════════

    /**
     * True if this enemy is touched by the blast zone.
     */
    public boolean isInBlastZone(boolean[][] zone, int rows, int cols) {
        if (level == Level.HARD) {
            for (int dr = 0; dr < 2; dr++)
                for (int dc = 0; dc < 2; dc++) {
                    int rr = posY + dr, cc = posX + dc;
                    if (rr >= 0 && rr < rows && cc >= 0 && cc < cols && zone[rr][cc])
                        return true;
                }
            return false;
        }
        return posY >= 0 && posY < rows && posX >= 0 && posX < cols && zone[posY][posX];
    }

    // ═══════════════════════════════════════════
    // GETTERS
    // ═══════════════════════════════════════════

    public int     getHealth()     { return health; }
    public int     getDamage()     { return damage; }
    public int     getSize()       { return size; }
    public int     getPosX()       { return posX; }
    public int     getPosY()       { return posY; }
    public Element getElement()    { return element; }
    public boolean isShielded()    { return shielded; }
    public Level   getLevel()      { return level; }
    public int     getCurrentDir() { return currentDir; }
    public boolean isFreezed()         {return isFreezed;}

    // ═══════════════════════════════════════════
    // SETTERS
    // ═══════════════════════════════════════════

    public void setHealth(int h)       { this.health     = h; }
    public void setDamage(int d)       { this.damage     = d; }
    public void setSize(int s)         { this.size       = s; }
    public void setPosX(int x)         { this.posX       = x; }
    public void setPosY(int y)         { this.posY       = y; }
    public void setElement(Element e)  { this.element    = e; }
    public void setShielded(boolean s) { this.shielded   = s; }
    public void setLevel(Level l)      { this.level      = l; }
    public void setCurrentDir(int d)   { this.currentDir = d; }
    public void setFreezed(boolean f)  { this.isFreezed  = f; }
}