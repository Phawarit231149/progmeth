package game.entity;

import game.Element;

public class EasyEnemy extends Enemy{
    public EasyEnemy(int size, int posX, int posY, boolean isShielded){
        super(size, posX, posY, Element.NONE, isShielded);
        setHealth(1);
        setDamage(1);
        setLevel(Level.EASY);
    }

}
