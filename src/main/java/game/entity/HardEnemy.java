package game.entity;

import game.Element;

public class HardEnemy extends Enemy {
    public HardEnemy(int health, int damage, int size, int posX, int posY, Element element, boolean isShielded,Level level){
        super(size, posX, posY, element, isShielded);
        setHealth(health);
        setDamage(damage);
        setLevel(Level.HARD);
    }

}
