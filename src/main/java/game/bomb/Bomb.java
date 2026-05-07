package game.bomb;

import game.character.Character;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;

/**
 * Represents a single placed bomb.
 * Also owns the blast-zone calculation so GameController stays thin.
 */
public class Bomb {

    // ── Fields ────────────────────────────────
    private final int row;
    private final int col;
    private final int range;
    private final int damage;
    private final Character owner;
    private boolean exploded;

    // ── Constructor ───────────────────────────
    public Bomb(int row, int col, int range, int damage, Character owner) {
        this.row      = row;
        this.col      = col;
        this.range    = range;
        this.damage   = damage;
        this.owner    = owner;
        this.exploded = false;
    }

    // ── State ─────────────────────────────────
    public void detonate() { exploded = true; }
    public boolean isExploded() { return exploded; }

    // ── Blast zone ────────────────────────────

    /**
     * Computes which tiles are hit by this bomb's explosion.
     * Stops at Rocks; destroys (and stops at) the first Seaweed in each direction.
     *
     * @param map      the tile grid
     * @param seaweeds the seaweed grid (entries mutated: hit seaweeds are destroyed)
     * @param rows     grid height
     * @param cols     grid width
     * @return boolean[rows][cols] — true where the blast reaches
     */
    public boolean[][] computeBlastZone(Tile[][] map, Seaweed[][] seaweeds,
                                        int rows, int cols) {
        boolean[][] zone = new boolean[rows][cols];
        zone[row][col] = true;

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            for (int step = 1; step <= range; step++) {
                int rr = row + d[0] * step;
                int cc = col + d[1] * step;
                if (rr < 0 || rr >= rows || cc < 0 || cc >= cols) break;
                if (map[rr][cc] instanceof Rock) break;
                zone[rr][cc] = true;
                if (seaweeds[rr][cc] != null && !seaweeds[rr][cc].isDestroyed()) {
                    seaweeds[rr][cc].destroy();
                    break; // seaweed blocks further spread
                }
            }
        }
        return zone;
    }

    // ── Getters ───────────────────────────────
    public int getRow()         { return row; }
    public int getCol()         { return col; }
    public int getRange()       { return range; }
    public int getDamage()      { return damage; }
    public Character getOwner() { return owner; }
}