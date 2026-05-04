package game.entity;

import game.Element;

public class HardEnemy extends Enemy {
    public HardEnemy(int size, int posX, int posY, Element element, boolean isShielded){
        super(size, posX, posY, element, isShielded);
        setHealth(1);
        setDamage(1);
        setLevel(Level.HARD);
    }

}
