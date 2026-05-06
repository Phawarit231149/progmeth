import game.Element;
import game.buff.ShieldBuff;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.entity.EasyEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterTest {
    private FireCharacter fire;
    private WaterCharacter water;
    private ElectricCharacter electric;

    @BeforeEach
    void setUp() {
        // health=5, damage=1, damageBomb=1, bombRange=1, maxBombs=5
        fire     = new FireCharacter(5, 1, 1, 5);
        water    = new WaterCharacter(5, 1, 1, 5);
        electric = new ElectricCharacter(5, 1, 1, 5);
    }

    @Test
    void takeDamageTest_noShield() {
        fire.takeDamage(1);
        water.takeDamage(1);
        electric.takeDamage(1);

        assertEquals(4,fire.getHealth());
        assertEquals(4,water.getHealth());
        assertEquals(4,electric.getHealth());

        fire.takeDamage(5);
        water.takeDamage(5);
        electric.takeDamage(5);
        assertEquals(0,fire.getHealth());
        assertEquals(0,water.getHealth());
        assertEquals(0,electric.getHealth());
    }

    @Test
    void takeDamageTest_shield() {
        new ShieldBuff(0, 0).apply(fire);
        new ShieldBuff(0, 0).apply(water);
        new ShieldBuff(0, 0).apply(electric);

        fire.takeDamage(9);
        water.takeDamage(9);
        electric.takeDamage(9);

        assertEquals(5,fire.getHealth());
        assertEquals(5,water.getHealth());
        assertEquals(5,electric.getHealth());
    }

    @Test
    void isStrongAgainstTest_fireCharacter() {
        boolean bool_1 = fire.isStrongAgainst(Element.NONE);
        boolean bool_2 = fire.isStrongAgainst(Element.ELECTRIC);
        boolean bool_3 = fire.isStrongAgainst(Element.WATER);
        boolean bool_4 = fire.isStrongAgainst(Element.FIRE);

        assertTrue(bool_1);
        assertTrue(bool_2);
        assertFalse(bool_3);
        assertFalse(bool_4);
    }

    @Test
    void isStrongAgainstTest_waterCharacter() {
        boolean bool_1 = water.isStrongAgainst(Element.NONE);
        boolean bool_2 = water.isStrongAgainst(Element.FIRE);
        boolean bool_3 = water.isStrongAgainst(Element.ELECTRIC);
        boolean bool_4 = water.isStrongAgainst(Element.WATER);

        assertTrue(bool_1);
        assertTrue(bool_2);
        assertFalse(bool_3);
        assertFalse(bool_4);
    }

    @Test
    void isStrongAgainstTest_electricCharacter() {
        boolean bool_1 = electric.isStrongAgainst(Element.NONE);
        boolean bool_2 = electric.isStrongAgainst(Element.WATER);
        boolean bool_3 = electric.isStrongAgainst(Element.FIRE);
        boolean bool_4 = electric.isStrongAgainst(Element.ELECTRIC);
        assertTrue(bool_1);
        assertTrue(bool_2);
        assertFalse(bool_3);
        assertFalse(bool_4);
    }

    @Test
    void isWeakAgainstTest_fireCharacter() {
        boolean bool_1 = fire.isWeakAgainst(Element.NONE);
        boolean bool_2 = fire.isWeakAgainst(Element.WATER);
        boolean bool_3 = fire.isWeakAgainst(Element.ELECTRIC);
        boolean bool_4 = fire.isWeakAgainst(Element.FIRE);

        assertFalse(bool_1);
        assertTrue(bool_2);
        assertFalse(bool_3);
        assertFalse(bool_4);
    }

    @Test
    void isWeakAgainstTest_waterCharacter() {
        boolean bool_1 = water.isWeakAgainst(Element.NONE);
        boolean bool_2 = water.isWeakAgainst(Element.ELECTRIC);
        boolean bool_3 = water.isWeakAgainst(Element.FIRE);
        boolean bool_4 = water.isWeakAgainst(Element.WATER);

        assertFalse(bool_1);
        assertTrue(bool_2);
        assertFalse(bool_3);
        assertFalse(bool_4);
    }

    @Test
    void isWeakAgainstTest_electricCharacter() {
        boolean bool_1 = electric.isWeakAgainst(Element.NONE);
        boolean bool_2 = electric.isWeakAgainst(Element.FIRE);
        boolean bool_3 = electric.isWeakAgainst(Element.WATER);
        boolean bool_4 = electric.isWeakAgainst(Element.ELECTRIC);

        assertFalse(bool_1);
        assertTrue(bool_2);
        assertFalse(bool_3);
        assertFalse(bool_4);
    }
}
