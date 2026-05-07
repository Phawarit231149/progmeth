import game.buff.HealBuff;
import game.buff.Buff;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MapTest {

    // ── Tile ──────────────────────────────────────────────────────

    @Test
    void tile_isPassable() {
        assertTrue(new Tile(0, 0).isPassable());
    }

    @Test
    void tile_positionGetters() {
        Tile t = new Tile(3, 7);
        assertEquals(3, t.getRow());
        assertEquals(7, t.getCol());
    }

    // ── Rock ──────────────────────────────────────────────────────

    @Test
    void rock_notPassable() {
        assertFalse(new Rock(0, 0).isPassable());
    }

    // ── Seaweed — passability ─────────────────────────────────────

    @Test
    void seaweed_notPassableBeforeDestroy() {
        assertFalse(new Seaweed(0, 0).isPassable());
    }

    @Test
    void seaweed_passableAfterDestroy() {
        Seaweed s = new Seaweed(0, 0);
        s.destroy();
        assertTrue(s.isPassable());
    }

    @Test
    void seaweed_notDestroyedInitially() {
        assertFalse(new Seaweed(0, 0).isDestroyed());
    }

    @Test
    void seaweed_destroyedAfterDestroy() {
        Seaweed s = new Seaweed(0, 0);
        s.destroy();
        assertTrue(s.isDestroyed());
    }

    // ── Seaweed — hidden buff ─────────────────────────────────────

    @Test
    void seaweed_destroyReturnsNull_noBuff() {
        assertNull(new Seaweed(0, 0).destroy());
    }

    @Test
    void seaweed_destroyReturnsBuff() {
        HealBuff buff = new HealBuff(0, 0);
        Seaweed s = new Seaweed(0, 0, buff);
        Buff result = s.destroy();
        assertSame(buff, result);
    }

    @Test
    void seaweed_destroyTwice_secondReturnsNull() {
        Seaweed s = new Seaweed(0, 0, new HealBuff(0, 0));
        s.destroy();          // first: returns buff
        assertNull(s.destroy()); // second: already destroyed
    }

    @Test
    void seaweed_hasHiddenBuff_beforeDestroy() {
        Seaweed s = new Seaweed(0, 0, new HealBuff(0, 0));
        assertTrue(s.hasHiddenBuff());
    }

    @Test
    void seaweed_hasHiddenBuff_falseAfterDestroy() {
        Seaweed s = new Seaweed(0, 0, new HealBuff(0, 0));
        s.destroy();
        assertFalse(s.hasHiddenBuff());
    }

    @Test
    void seaweed_hasHiddenBuff_falseWhenNoBuff() {
        assertFalse(new Seaweed(0, 0).hasHiddenBuff());
    }
}