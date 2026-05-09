package game.util;

import game.Element;
import game.character.Character;
import game.entity.Enemy;

/**
 * Calculates elemental damage multipliers.
 *
 * Triangle: FIRE beats ELECTRIC, WATER beats FIRE, ELECTRIC beats WATER.
 * Strong match → base + 1.  Neutral → base.  Weak match → base − 1 (cap ที่ 0).
 *
 * NONE element rule:
 *  - ฝั่งใดเป็น NONE (attacker หรือ defender) → ถือเป็น neutral (base damage ปกติ)
 */
public class ElementUtil {

    public int calculateCharacterDamage(Character attacker, Enemy defender) {
        Element atk = attacker.getElement();
        Element def = defender.getElement();
        if (isStrongAgainst(atk, def)) return attacker.getDamage() + 1;
        if (isWeakAgainst(atk, def))   return Math.max(0, attacker.getDamage() - 1);
        return attacker.getDamage(); // neutral
    }

    public int calculateEnemyDamage(Enemy attacker, Character defender) {
        Element atk = attacker.getElement();
        Element def = defender.getElement();
        if (isStrongAgainst(atk, def)) return attacker.getDamage() + 1;
        if (isWeakAgainst(atk, def))   return Math.max(0, attacker.getDamage() - 1);
        return attacker.getDamage();
    }

    private boolean isStrongAgainst(Element atk, Element def) {
        // ฝั่งใดเป็น NONE / null → ไม่นับ strong (neutral)
        if (atk == null || atk == Element.NONE) return false;
        if (def == null || def == Element.NONE) return false;
        return (atk == Element.FIRE     && def == Element.ELECTRIC) ||
                (atk == Element.WATER    && def == Element.FIRE)     ||
                (atk == Element.ELECTRIC && def == Element.WATER);
    }

    private boolean isWeakAgainst(Element atk, Element def) {
        // ฝั่งใดเป็น NONE / null → ไม่นับ weak (neutral)
        if (atk == null || atk == Element.NONE) return false;
        if (def == null || def == Element.NONE) return false;
        return (atk == Element.FIRE     && def == Element.WATER)    ||
                (atk == Element.WATER    && def == Element.ELECTRIC) ||
                (atk == Element.ELECTRIC && def == Element.FIRE);
    }
}