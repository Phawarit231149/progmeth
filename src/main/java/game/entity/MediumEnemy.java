package game.entity;

import game.Element;

public class MediumEnemy extends Enemy {
    public MediumEnemy(int size, int posX, int posY, Element element, boolean isShielded){
        super(size, posX, posY, element, isShielded);
        setHealth(5);
        setDamage(5);
        setLevel(Level.MEDIUM);
    }

}
