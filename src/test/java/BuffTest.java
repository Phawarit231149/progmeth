import game.buff.*;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.character.ElectricCharacter;
import game.character.Character;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuffTest {

    private FireCharacter fire;
    private WaterCharacter water;
    private ElectricCharacter electric;

    @BeforeEach
    void setUp() {
        // health=5, damage=1, damageBomb=1, bombRange=1, maxBombs=5
        fire     = new FireCharacter(5, 1, 1, 5);
        water    = new WaterCharacter(5 , 1, 1, 5);
        electric = new ElectricCharacter(5, 1, 1, 5);
    }

    // ── MaxBombBuff ──────────────────────────────────────
    @Test
    void maxBombBuffTest() {
        new MaxBombBuff(0, 0).apply(fire);
        assertEquals(6, fire.getMaxBombs());
    }

    // ── BombRangeBuff ────────────────────────────────────
    @Test
    void bombRangeBuffTest() {
        new BombRangeBuff(0, 0).apply(fire);
        assertEquals(2, fire.getBombRange());
    }

    // ── BombDamageBuff ───────────────────────────────────
    @Test
    void bombDamageBuffTest() {
        new BombDamageBuff(0, 0).apply(fire);
        assertEquals(2, fire.getDamage());
    }

    // ── HealBuff ─────────────────────────────────────────
    @Test
    void healBuffTest() {
        fire.setHealth(3);
        new HealBuff(0, 0).apply(fire);
        assertEquals(4, fire.getHealth());
    }

    @Test
    void healBuffTest_doesNotExceedMaxHealth() {
        fire.setHealth(5);
        new HealBuff(0, 0).apply(fire);
        assertEquals(5, fire.getHealth());
    }

    // ── ShieldBuff ───────────────────────────────────────
    @Test
    void shieldBuff_worksForFire() {
        assertFalse(fire.hasShield());
        new ShieldBuff(0, 0).apply(fire);
        assertTrue(fire.hasShield());
    }

    @Test
    void shieldBuff_worksForWater() {
        assertFalse(water.hasShield());
        new ShieldBuff(0, 0).apply(water);
        assertTrue(water.hasShield());
    }

    @Test
    void shieldBuff_worksForElectric() {
        assertFalse(electric.hasShield());
        new ShieldBuff(0, 0).apply(electric);
        assertTrue(electric.hasShield());
    }

    // ── Shield consumed ──────────────────────────────────
    @Test
    void shield_consumedAfterSetFalse() {
        new ShieldBuff(0, 0).apply(fire);
        assertTrue(fire.hasShield());
        fire.setShield(false);
        assertFalse(fire.hasShield());
    }
}