package game.buff;

import game.character.Character;

public class BombDamageBuff extends Buff {

    private static final int DAMAGE_BONUS = 1;   // เพิ่ม damage +1

    public BombDamageBuff() {
        super("Bomb Damage +1", "Bomb damage +1.");
    }

    @Override
    public void apply(Character target) {
        target.increaseBombDamage(DAMAGE_BONUS);
    }
}
