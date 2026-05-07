import game.bomb.Bomb;
import game.character.FireCharacter;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class BombBlastTest {

    // ── Helpers ───────────────────────────────────────────────────

    private static Tile[][] emptyMap(int rows, int cols) {
        Tile[][] m = new Tile[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                m[r][c] = new Tile(r, c);
        return m;
    }

    private static Seaweed[][] emptySeaweeds(int rows, int cols) {
        return new Seaweed[rows][cols];
    }

    private static FireCharacter owner() {
        return new FireCharacter(5, 1, 1, 5);
    }

    // ── Basic spread ──────────────────────────────────────────────

    @Test
    void blast_includesBombCell() {
        Bomb bomb = new Bomb(3, 3, 2, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(7, 7), emptySeaweeds(7, 7), 7, 7);
        assertTrue(zone[3][3]);
    }

    @Test
    void blast_spreadsUpByRange() {
        Bomb bomb = new Bomb(4, 4, 2, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(9, 9), emptySeaweeds(9, 9), 9, 9);
        assertTrue(zone[3][4]); // 1 up
        assertTrue(zone[2][4]); // 2 up
        assertFalse(zone[1][4]); // 3 up — beyond range
    }

    @Test
    void blast_spreadsDownByRange() {
        Bomb bomb = new Bomb(4, 4, 2, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(9, 9), emptySeaweeds(9, 9), 9, 9);
        assertTrue(zone[5][4]);
        assertTrue(zone[6][4]);
        assertFalse(zone[7][4]);
    }

    @Test
    void blast_spreadsHorizontallyByRange() {
        Bomb bomb = new Bomb(4, 4, 2, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(9, 9), emptySeaweeds(9, 9), 9, 9);
        assertTrue(zone[4][2]);
        assertTrue(zone[4][6]);
        assertFalse(zone[4][1]);
        assertFalse(zone[4][7]);
    }

    // ── Rock blocking ─────────────────────────────────────────────

    @Test
    void blast_rockTileNotHit() {
        Tile[][] map = emptyMap(7, 7);
        map[4][5] = new Rock(4, 5);
        Bomb bomb = new Bomb(4, 4, 2, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(map, emptySeaweeds(7, 7), 7, 7);
        assertFalse(zone[4][5]);
    }

    @Test
    void blast_tilesBehindRockNotHit() {
        Tile[][] map = emptyMap(7, 7);
        map[4][5] = new Rock(4, 5);
        Bomb bomb = new Bomb(4, 4, 3, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(map, emptySeaweeds(7, 7), 7, 7);
        assertFalse(zone[4][6]);
    }

    // ── Seaweed blocking ──────────────────────────────────────────

    @Test
    void blast_seaweedTileHit_andDestroyed() {
        Seaweed[][] seaweeds = emptySeaweeds(7, 7);
        seaweeds[4][5] = new Seaweed(4, 5);
        Bomb bomb = new Bomb(4, 4, 2, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(7, 7), seaweeds, 7, 7);
        assertTrue(zone[4][5]);
        assertTrue(seaweeds[4][5].isDestroyed());
    }

    @Test
    void blast_tilesBehindSeaweedNotHit() {
        Seaweed[][] seaweeds = emptySeaweeds(7, 7);
        seaweeds[4][5] = new Seaweed(4, 5);
        Bomb bomb = new Bomb(4, 4, 3, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(7, 7), seaweeds, 7, 7);
        assertFalse(zone[4][6]);
    }

    @Test
    void blast_destroyedSeaweed_doesNotBlockSpread() {
        Seaweed[][] seaweeds = emptySeaweeds(7, 7);
        seaweeds[4][5] = new Seaweed(4, 5);
        seaweeds[4][5].destroy(); // pre-destroyed
        Bomb bomb = new Bomb(4, 4, 3, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(7, 7), seaweeds, 7, 7);
        assertTrue(zone[4][6]); // passes through destroyed seaweed
    }

    // ── Boundary safety ───────────────────────────────────────────

    @Test
    void blast_cornerBomb_noBoundsException() {
        Bomb bomb = new Bomb(0, 0, 5, 1, owner());
        assertDoesNotThrow(() ->
                bomb.computeBlastZone(emptyMap(5, 5), emptySeaweeds(5, 5), 5, 5)
        );
    }

    @Test
    void blast_cornerBomb_staysInBounds() {
        Bomb bomb = new Bomb(0, 0, 3, 1, owner());
        boolean[][] zone = bomb.computeBlastZone(emptyMap(5, 5), emptySeaweeds(5, 5), 5, 5);
        // Verify every true cell is within bounds
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                if (zone[r][c])
                    assertTrue(r >= 0 && r < 5 && c >= 0 && c < 5,
                            "Out-of-bounds cell flagged: (" + r + "," + c + ")");
    }

    // ── Detonate flag ─────────────────────────────────────────────

    @Test
    void bomb_notExploded_initially() {
        Bomb bomb = new Bomb(0, 0, 1, 1, owner());
        assertFalse(bomb.isExploded());
    }

    @Test
    void detonate_setsFlag() {
        Bomb bomb = new Bomb(0, 0, 1, 1, owner());
        bomb.detonate();
        assertTrue(bomb.isExploded());
    }
}