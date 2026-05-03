package game.util;

import game.Element;
import game.character.Character;
import game.entity.Enemy;

public class ElementUtil {
    public int calculateCharacterDamage(Character attacker, Enemy defender){
        if((isStrongAgainst(attacker.getElement(),defender.getElement()))){
            return attacker.getDamage() * 2;
        } if(isWeakAgainst(attacker.getElement(),defender.getElement())){
            return attacker.getDamage() / 2;
        }
        return attacker.getDamage();
    }

    public int calculateEnemyDamage(Enemy attacker, Character defender){
        if((isStrongAgainst(attacker.getElement(),defender.getElement()))){
            return attacker.getDamage() * 2;
        } if(isWeakAgainst(attacker.getElement(),defender.getElement())){
            return attacker.getDamage() / 2;
        }
        return attacker.getDamage();
    }

    private boolean isStrongAgainst(Element element, Element other) {
        return ( element== Element.FIRE    && other == Element.WATER)    ||
                (element == Element.WATER   && other == Element.ELECTRIC) ||
                (element == Element.ELECTRIC && other == Element.FIRE) ||
                (other == Element.NONE);
    }

    private boolean isWeakAgainst(Element element, Element other) {
        return (element == Element.FIRE    && other == Element.ELECTRIC) ||
                (element == Element.WATER   && other == Element.FIRE)     ||
                (element == Element.ELECTRIC && other == Element.WATER);
    }
}
