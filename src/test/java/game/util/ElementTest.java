package game.util;

import game.Element;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.entity.EasyEnemy;
import game.entity.MediumEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import game.util.ElementUtil;

public class ElementTest {
    private ElementUtil util;
    private FireCharacter fire;
    private WaterCharacter water;
    private ElectricCharacter electric;

    private EasyEnemy easyEnemy; // Element.NONE
    private MediumEnemy fireMedium;
    private MediumEnemy waterMedium;
    private MediumEnemy electricMedium;

    @BeforeEach
    void setUp(){
        util = new ElementUtil();
        // Assuming parameters are (damage, ..., ...) based on your snippet
        // Setting base damage to 5 for all characters
        fire = new FireCharacter(5, 1, 1, 5);
        water = new WaterCharacter(5, 1, 1, 5);
        electric = new ElectricCharacter(5, 1, 1, 5);

        easyEnemy = new EasyEnemy(1, 0, 0, false);
        fireMedium = new MediumEnemy(1, 0, 0, Element.FIRE, false);
        waterMedium = new MediumEnemy(1, 0, 0, Element.WATER, false);
        electricMedium = new MediumEnemy(1, 0, 0, Element.ELECTRIC, false);
    }

    @Test
    void testCharacterStrongMatch() {
        // Water beats Fire: 5 (base) + 2 = 7
        assertEquals(3, util.calculateCharacterDamage(water, fireMedium));
        // Fire beats Electric: 5 (base) + 2 = 7
        assertEquals(3, util.calculateCharacterDamage(fire, electricMedium));
        // Electric beats Water: 5 (base) + 2 = 7
        assertEquals(3, util.calculateCharacterDamage(electric, waterMedium));
    }

    @Test
    void testCharacterNeutralMatch() {
        // Same elements: 5 (base) + 1 = 6
        assertEquals(2, util.calculateCharacterDamage(fire, fireMedium));
        assertEquals(2, util.calculateCharacterDamage(water, waterMedium));
    }

    @Test
    void testCharacterWeakMatch() {
        // Fire is weak against Water: 5 (base) + 0 = 5
        assertEquals(1, util.calculateCharacterDamage(fire, waterMedium));
        // Water is weak against Electric: 5 (base) + 0 = 5
        assertEquals(1, util.calculateCharacterDamage(water, electricMedium));
    }

    @Test
    void testCharacterVsNoneElement() {
        // Enemy.NONE is always treated as weak (Attacker gets +2)
        assertEquals(3, util.calculateCharacterDamage(fire, easyEnemy));
    }

    @Test
    void testEnemyDamage() {
        // Strong: Water Enemy vs Fire Character = 3
        assertEquals(3, util.calculateEnemyDamage(waterMedium, fire));

        // Neutral: Fire Enemy vs Fire Character = 2
        assertEquals(2, util.calculateEnemyDamage(fireMedium, fire));

        // Weak: Fire Enemy vs Water Character = 1
        assertEquals(1, util.calculateEnemyDamage(fireMedium, water));

        // Special: EasyEnemy (NONE) is always weak = 1
        assertEquals(1, util.calculateEnemyDamage(easyEnemy, fire));
    }
}