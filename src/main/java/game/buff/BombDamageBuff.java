package game.buff;

import game.character.Character;

public class BombDamageBuff extends Buff {

    private static final int DAMAGE_BONUS = 1;

    public BombDamageBuff(int r, int c) {
        super(r, c);
    }

    @Override
    public void apply(Character target) {
        target.increaseBombDamage(DAMAGE_BONUS);
    }
}