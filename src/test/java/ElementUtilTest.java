import game.Element;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.entity.EasyEnemy;
import game.entity.MediumEnemy;
import game.util.ElementUtil;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ElementUtilTest {

    private final ElementUtil util = new ElementUtil();

    // ══════════════════════════════════════════════
    // Character attacks Enemy
    // ══════════════════════════════════════════════

    @Test
    void charFireVsElectric_strong() {
        FireCharacter a = new FireCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.ELECTRIC, false);
        assertEquals(3, util.calculateCharacterDamage(a, d));
    }

    @Test
    void charFireVsWater_weak() {
        FireCharacter a = new FireCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.WATER, false);
        assertEquals(1, util.calculateCharacterDamage(a, d)); // 2 + 0
    }

    @Test
    void charFireVsFire_neutral() {
        FireCharacter a = new FireCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        assertEquals(2, util.calculateCharacterDamage(a, d)); // 2 + 1
    }

    @Test
    void charWaterVsFire_strong() {
        WaterCharacter a = new WaterCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        assertEquals(3, util.calculateCharacterDamage(a, d));
    }

    @Test
    void charWaterVsElectric_weak() {
        WaterCharacter a = new WaterCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.ELECTRIC, false);
        assertEquals(1, util.calculateCharacterDamage(a, d));
    }

    @Test
    void charElectricVsWater_strong() {
        ElectricCharacter a = new ElectricCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.WATER, false);
        assertEquals(3, util.calculateCharacterDamage(a, d));
    }

    @Test
    void charElectricVsFire_weak() {
        ElectricCharacter a = new ElectricCharacter(5, 2, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        assertEquals(1, util.calculateCharacterDamage(a, d));
    }

    @Test
    void charVsNoneElement_alwaysStrong() {
        FireCharacter a = new FireCharacter(5, 2, 1, 5);
        EasyEnemy d = new EasyEnemy(1, 0, 0, false); // Element.NONE
        assertEquals(2, util.calculateCharacterDamage(a, d));
    }

    @Test
    void charDamageScalesWithBaseDamage() {
        // base damage 3, FIRE vs ELECTRIC (strong) → 3 + 2 = 5
        FireCharacter a = new FireCharacter(5, 3, 1, 5);
        MediumEnemy d = new MediumEnemy(1, 0, 0, Element.ELECTRIC, false);
        assertEquals(4, util.calculateCharacterDamage(a, d));
    }

    // ══════════════════════════════════════════════
    // Enemy attacks Character
    // ══════════════════════════════════════════════

    @Test
    void enemyFireVsCharElectric_strong() {
        MediumEnemy a = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        ElectricCharacter d = new ElectricCharacter(5, 1, 1, 5);
        assertEquals(3, util.calculateEnemyDamage(a, d));
    }

    @Test
    void enemyFireVsCharWater_weak() {
        MediumEnemy a = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        WaterCharacter d = new WaterCharacter(5, 1, 1, 5);
        assertEquals(1, util.calculateEnemyDamage(a, d));
    }

    @Test
    void enemyFireVsCharFire_neutral() {
        MediumEnemy a = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        FireCharacter d = new FireCharacter(5, 1, 1, 5);
        assertEquals(2, util.calculateEnemyDamage(a, d));
    }

    @Test
    void enemyWaterVsCharFire_strong() {
        MediumEnemy a = new MediumEnemy(1, 0, 0, Element.WATER, false);
        FireCharacter d = new FireCharacter(5, 1, 1, 5);
        assertEquals(3, util.calculateEnemyDamage(a, d));
    }

    @Test
    void enemyElectricVsCharWater_strong() {
        MediumEnemy a = new MediumEnemy(1, 0, 0, Element.ELECTRIC, false);
        WaterCharacter d = new WaterCharacter(5, 1, 1, 5);
        assertEquals(3, util.calculateEnemyDamage(a, d));
    }

    @Test
    void enemyElectricVsCharFire_weak() {
        MediumEnemy a = new MediumEnemy(1, 0, 0, Element.ELECTRIC, false);
        FireCharacter d = new FireCharacter(5, 1, 1, 5);
        assertEquals(1, util.calculateEnemyDamage(a, d));
    }
}