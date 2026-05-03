package game.entity;

import game.Element;

public class EasyEnemy extends Enemy{
    public EasyEnemy(int health, int damage, int size, int posX, int posY, boolean isShielded){
        super(size, posX, posY, Element.NONE, isShielded);
        setHealth(health);
        setDamage(damage);
        setLevel(Level.EASY);
    }

}
