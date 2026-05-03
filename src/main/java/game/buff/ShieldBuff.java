package game.buff;

import game.character.Character;

public class ShieldBuff extends Buff {

    public ShieldBuff() {
        super("Shield", "Block one incoming attack.");
    }

    @Override
    public void apply(Character target) {
        target.setShield(true);
    }
}
