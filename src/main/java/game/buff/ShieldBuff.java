package game.buff;

import game.character.Character;

public class ShieldBuff extends Buff {
    public ShieldBuff(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void apply(Character character) {character.setHaveShield(true);}
}
