package game;

import game.buff.*;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MapTest {
    private Tile tile;
    private Rock rock;
    private Seaweed seaweed;

    @BeforeEach
    void setUp() {
        tile = new Tile(1, 1);
        rock = new Rock(1, 2);
        seaweed = new Seaweed(1, 3);
    }

    @Test
    void passableTest() {
        assertTrue(tile.isPassable());
        assertFalse(rock.isPassable());
        assertFalse(seaweed.isPassable());

        seaweed.destroy();
        assertTrue(seaweed.isPassable());
        assertTrue(seaweed.isDestroyed());
    }

    @Test
    void hasHiddenBuffTest() {
        assertFalse(seaweed.hasHiddenBuff());

        seaweed = new Seaweed(0, 0, new MaxBombBuff(0, 0));
        assertTrue(seaweed.hasHiddenBuff());

        seaweed = new Seaweed(0, 0, new BombRangeBuff(0, 0));
        assertTrue(seaweed.hasHiddenBuff());

        seaweed = new Seaweed(0, 0, new BombDamageBuff(0, 0));
        assertTrue(seaweed.hasHiddenBuff());

        seaweed = new Seaweed(0, 0, new ShieldBuff(0, 0));
        assertTrue(seaweed.hasHiddenBuff());

        seaweed = new Seaweed(0, 0, new HealBuff(0, 0));
        assertTrue(seaweed.hasHiddenBuff());
    }

    @Test
    void destroyAndRetrieveBuffTest() {
        Buff expectedBuff = new HealBuff(0, 0);
        seaweed = new Seaweed(0, 0, expectedBuff);

        assertTrue(seaweed.hasHiddenBuff());
        assertFalse(seaweed.isDestroyed());

        Buff actualBuff = seaweed.destroy();

        assertEquals(expectedBuff, actualBuff);
        assertTrue(seaweed.isDestroyed());
        assertFalse(seaweed.hasHiddenBuff());

        // Test double destruction (should return null)
        assertNull(seaweed.destroy());
    }

    @Test
    void getterTest() {
        assertEquals(1, tile.getRow());
        assertEquals(1, tile.getCol());
        assertEquals(1, rock.getRow());
        assertEquals(2, rock.getCol());
    }
}