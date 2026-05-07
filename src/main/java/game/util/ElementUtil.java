package game.util;

import game.Element;
import game.character.Character;
import game.entity.Enemy;

/**
 * Calculates elemental damage multipliers.
 *
 * Triangle: FIRE beats ELECTRIC, WATER beats FIRE, ELECTRIC beats WATER.
 * Strong match → base + 2.  Neutral → base + 1.  Weak match → base (no bonus).
 * Enemy.NONE element → always treated as weak (attacker is always strong).
 */
public class ElementUtil {

    public int calculateCharacterDamage(Character attacker, Enemy defender) {
        Element atk = attacker.getElement();
        Element def = defender.getElement();
        if (isStrongAgainst(atk, def)) return attacker.getDamage() + 2;
        if (isWeakAgainst(atk, def)) return attacker.getDamage();
        return attacker.getDamage() + 1; // neutral
    }

    public int calculateEnemyDamage(Enemy attacker, Character defender) {
        Element atk = attacker.getElement();
        Element def = defender.getElement();
        if (isStrongAgainst(atk, def)) return attacker.getDamage() + 2;
        if (isWeakAgainst(atk, def)) return attacker.getDamage();
        return attacker.getDamage() + 1;
    }

    private boolean isStrongAgainst(Element atk, Element def) {
        return (atk == Element.FIRE     && def == Element.ELECTRIC) ||
                (atk == Element.WATER    && def == Element.FIRE) ||
                (atk == Element.ELECTRIC && def == Element.WATER) ||
                (def == Element.NONE);
    }

    private boolean isWeakAgainst(Element atk, Element def) {
        return (atk == Element.FIRE     && def == Element.WATER)    ||
                (atk == Element.WATER    && def == Element.ELECTRIC) ||
                (atk == Element.ELECTRIC && def == Element.FIRE) ||
                (atk ==  Element.NONE);
    }
}