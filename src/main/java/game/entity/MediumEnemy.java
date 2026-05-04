package game.entity;

import game.Element;

public class  MediumEnemy extends Enemy {
    public MediumEnemy(int size, int posX, int posY, Element element, boolean isShielded){
        super(size, posX, posY, element, isShielded);
        setHealth(1);
        setDamage(1);
        setLevel(Level.MEDIUM);
    }

}
