import game.buff.*;
import game.character.FireCharacter;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class BuffTest {

    // ── MaxBombBuff ───────────────────────────────────────────────

    @Test
    void maxBombBuff_increasesByOne() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        new MaxBombBuff(0, 0).apply(c);
        assertEquals(4, c.getMaxBombs());
    }

    @Test
    void maxBombBuff_stacksCorrectly() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        new MaxBombBuff(0, 0).apply(c);
        new MaxBombBuff(0, 0).apply(c);
        assertEquals(5, c.getMaxBombs());
    }

    // ── BombRangeBuff ─────────────────────────────────────────────

    @Test
    void bombRangeBuff_increasesByOne() {
        FireCharacter c = new FireCharacter(5, 1, 2, 3);
        new BombRangeBuff(0, 0).apply(c);
        assertEquals(3, c.getBombRange());
    }

    // ── BombDamageBuff ────────────────────────────────────────────

    @Test
    void bombDamageBuff_increasesByOne() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        new BombDamageBuff(0, 0).apply(c);
        assertEquals(2, c.getDamage());
    }

    // ── ShieldBuff ────────────────────────────────────────────────

    @Test
    void shieldBuff_setsShield() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        assertFalse(c.hasShield());
        new ShieldBuff(0, 0).apply(c);
        assertTrue(c.hasShield());
    }

    @Test
    void shieldBuff_alreadyShielded_remainsShielded() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        c.setShield(true);
        new ShieldBuff(0, 0).apply(c);
        assertTrue(c.hasShield());
    }

    // ── HealBuff ──────────────────────────────────────────────────

    @Test
    void healBuff_restoresOneHp() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        c.takeDamage(3); // health = 2
        new HealBuff(0, 0).apply(c);
        assertEquals(3, c.getHealth());
    }

    @Test
    void healBuff_fullHealth_noOverHeal() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3); // full health = 5
        new HealBuff(0, 0).apply(c);
        assertEquals(5, c.getHealth());
    }

    @Test
    void healBuff_from1hp() {
        FireCharacter c = new FireCharacter(5, 1, 1, 3);
        c.takeDamage(4); // health = 1
        new HealBuff(0, 0).apply(c);
        assertEquals(2, c.getHealth());
    }
}