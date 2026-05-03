package game.entity;

import game.Element;

public class MediumEnemy extends Enemy {
    public MediumEnemy(int health, int damage, int size, int posX, int posY, Element element, boolean isShielded){
        super(size, posX, posY, element, isShielded);
        setHealth(health);
        setDamage(damage);
        setLevel(Level.MEDIUM);
    }

}
