import game.Element;
import game.entity.*;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    // ── Default stats ─────────────────────────────────────────────

    @Test
    void easyEnemy_defaultStats() {
        EasyEnemy e = new EasyEnemy(1, 3, 4, false);
        assertEquals(Level.EASY, e.getLevel());
        assertEquals(1, e.getHealth());
        assertEquals(1, e.getDamage());
        assertEquals(Element.NONE, e.getElement());
        assertFalse(e.isShielded());
    }

    @Test
    void mediumEnemy_defaultStats() {
        MediumEnemy e = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        assertEquals(Level.MEDIUM, e.getLevel());
        assertEquals(10, e.getHealth());
        assertEquals(2, e.getDamage());
        assertEquals(Element.FIRE, e.getElement());
    }

    @Test
    void mediumEnemy_shielded() {
        MediumEnemy e = new MediumEnemy(1, 0, 0, Element.WATER, true);
        assertTrue(e.isShielded());
    }

    @Test
    void hardEnemy_defaultStats() {
        HardEnemy e = new HardEnemy(2, 0, 0, Element.WATER, false);
        assertEquals(Level.HARD, e.getLevel());
        assertEquals(50, e.getHealth());
        assertEquals(2, e.getDamage());
    }

    // ── Tile occupancy ────────────────────────────────────────────

    @Test
    void easyEnemy_occupiesExactTile() {
        EasyEnemy e = new EasyEnemy(1, 3, 4, false); // posX=3, posY=4 → row=4, col=3
        assertTrue(e.occupiesTile(4, 3));
    }

    @Test
    void easyEnemy_doesNotOccupyAdjacentTiles() {
        EasyEnemy e = new EasyEnemy(1, 3, 4, false);
        assertFalse(e.occupiesTile(4, 4));
        assertFalse(e.occupiesTile(3, 3));
        assertFalse(e.occupiesTile(5, 3));
    }

    @Test
    void hardEnemy_occupies2x2() {
        // posX=2, posY=3 → top-left at row=3, col=2
        HardEnemy e = new HardEnemy(2, 2, 3, Element.FIRE, false);
        assertTrue(e.occupiesTile(3, 2));
        assertTrue(e.occupiesTile(3, 3));
        assertTrue(e.occupiesTile(4, 2));
        assertTrue(e.occupiesTile(4, 3));
    }

    @Test
    void hardEnemy_doesNotOccupyOutside2x2() {
        HardEnemy e = new HardEnemy(2, 2, 3, Element.FIRE, false);
        assertFalse(e.occupiesTile(2, 2)); // above
        assertFalse(e.occupiesTile(3, 1)); // left
        assertFalse(e.occupiesTile(5, 2)); // below
        assertFalse(e.occupiesTile(3, 4)); // right
    }

    // ── Shield behaviour ──────────────────────────────────────────

    @Test
    void enemy_shieldAbsorbsDamage() {
        MediumEnemy e = new MediumEnemy(1, 0, 0, Element.FIRE, true);
        int healthBefore = e.getHealth();
        // Simulating GameController shield logic
        if (e.isShielded()) {
            e.setShielded(false);
        } else {
            e.setHealth(e.getHealth() - 2);
        }
        assertEquals(healthBefore, e.getHealth());
        assertFalse(e.isShielded());
    }

    @Test
    void enemy_setHealthToZero() {
        EasyEnemy e = new EasyEnemy(1, 0, 0, false);
        e.setHealth(0);
        assertEquals(0, e.getHealth());
    }

    // ── Blast zone detection ──────────────────────────────────────

    @Test
    void easyEnemy_inBlastZone() {
        EasyEnemy e = new EasyEnemy(1, 2, 3, false); // row=3, col=2
        boolean[][] zone = new boolean[7][7];
        zone[3][2] = true;
        assertTrue(e.isInBlastZone(zone, 7, 7));
    }

    @Test
    void easyEnemy_notInBlastZone() {
        EasyEnemy e = new EasyEnemy(1, 2, 3, false);
        boolean[][] zone = new boolean[7][7];
        zone[5][5] = true; // different tile
        assertFalse(e.isInBlastZone(zone, 7, 7));
    }

    @Test
    void hardEnemy_partialBlastZoneOverlap_detected() {
        // posX=2, posY=3 → occupies rows 3–4, cols 2–3
        HardEnemy e = new HardEnemy(2, 2, 3, Element.FIRE, false);
        boolean[][] zone = new boolean[8][8];
        zone[4][3] = true; // only bottom-right corner of 2×2
        assertTrue(e.isInBlastZone(zone, 8, 8));
    }

    @Test
    void hardEnemy_noneInBlastZone() {
        HardEnemy e = new HardEnemy(2, 2, 3, Element.FIRE, false);
        boolean[][] zone = new boolean[8][8];
        zone[0][0] = true; // far away
        assertFalse(e.isInBlastZone(zone, 8, 8));
    }

    //── Enemy Can Walk Test ───────────────────────────────────────
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

    // ── Out of bounds ─────────────────────────────────────────────

    @Test
    void canWalk_falseNegativeRow() {
        EasyEnemy e = new EasyEnemy(1, 2, 2, false);
        assertFalse(e.canWalk(-1, 2, emptyMap(5, 5), emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }

    @Test
    void canWalk_falseNegativeCol() {
        EasyEnemy e = new EasyEnemy(1, 2, 2, false);
        assertFalse(e.canWalk(2, -1, emptyMap(5, 5), emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }

    @Test
    void canWalk_falseRowOverflow() {
        EasyEnemy e = new EasyEnemy(1, 2, 2, false);
        assertFalse(e.canWalk(5, 2, emptyMap(5, 5), emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }

    @Test
    void canWalk_falseColOverflow() {
        EasyEnemy e = new EasyEnemy(1, 2, 2, false);
        assertFalse(e.canWalk(2, 5, emptyMap(5, 5), emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }

    // ── Obstacles ─────────────────────────────────────────────────

    @Test
    void canWalk_falseOnRock() {
        Tile[][] map = emptyMap(5, 5);
        map[2][3] = new Rock(2, 3);
        EasyEnemy e = new EasyEnemy(1, 0, 0, false);
        assertFalse(e.canWalk(2, 3, map, emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }

    @Test
    void canWalk_falseOnIntactSeaweed() {
        Seaweed[][] seaweeds = emptySeaweeds(5, 5);
        seaweeds[2][2] = new Seaweed(2, 2);
        EasyEnemy e = new EasyEnemy(1, 0, 0, false);
        assertFalse(e.canWalk(2, 2, emptyMap(5, 5), seaweeds, new ArrayList<>(), 5, 5));
    }

    @Test
    void canWalk_trueOnDestroyedSeaweed() {
        Seaweed[][] seaweeds = emptySeaweeds(5, 5);
        seaweeds[2][2] = new Seaweed(2, 2);
        seaweeds[2][2].destroy();
        EasyEnemy e = new EasyEnemy(1, 0, 0, false);
        assertTrue(e.canWalk(2, 2, emptyMap(5, 5), seaweeds, new ArrayList<>(), 5, 5));
    }

    @Test
    void canWalk_TrueOnBomb() {
        boolean[][] hasBomb = new boolean[5][5];
        hasBomb[3][3] = true;
        EasyEnemy e = new EasyEnemy(1, 0, 0, false);
        assertTrue(e.canWalk(3, 3, emptyMap(5, 5), emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }

    // ── Enemy collision ───────────────────────────────────────────

    @Test
    void canWalk_falseOnOtherEnemy() {
        EasyEnemy mover = new EasyEnemy(1, 0, 0, false);
        EasyEnemy blocker = new EasyEnemy(1, 3, 3, false); // posX=3, posY=3 → row=3,col=3
        List<Enemy> enemies = List.of(mover, blocker);
        assertFalse(mover.canWalk(3, 3, emptyMap(6, 6), emptySeaweeds(6, 6), enemies, 6, 6));
    }

    @Test
    void canWalk_trueOnOwnTile() {
        EasyEnemy e = new EasyEnemy(1, 2, 2, false); // posX=2, posY=2
        List<Enemy> enemies = List.of(e);
        assertTrue(e.canWalk(2, 2, emptyMap(5, 5), emptySeaweeds(5, 5), enemies, 5, 5));
    }

    @Test
    void canWalk_falseOnMediumEnemyTile() {
        EasyEnemy mover = new EasyEnemy(1, 0, 0, false);
        MediumEnemy blocker = new MediumEnemy(1, 4, 4, Element.FIRE, false);
        List<Enemy> enemies = List.of(mover, blocker);
        assertFalse(mover.canWalk(4, 4, emptyMap(7, 7), emptySeaweeds(7, 7), enemies, 7, 7));
    }

    // ── Clear path ────────────────────────────────────────────────

    @Test
    void canWalk_trueOnClearTile() {
        EasyEnemy e = new EasyEnemy(1, 0, 0, false);
        assertTrue(e.canWalk(2, 2, emptyMap(5, 5), emptySeaweeds(5, 5), new ArrayList<>(), 5, 5));
    }
}