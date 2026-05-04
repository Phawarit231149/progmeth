package game.buff;

import game.character.Character;

public class ShieldBuff extends Buff {

    public ShieldBuff(int r, int c) {
        super(r,c);
    }

    @Override
    public void apply(Character target) {
        target.setShield(true);
    }
}
