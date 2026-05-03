package game.buff;

import game.character.Character;

public abstract class Buff {
    protected int posX;
    protected int posY;

    public Buff(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public abstract void apply(Character character);

    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
}