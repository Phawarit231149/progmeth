package game.buff;

import game.character.Character;

public class MaxBombBuff extends Buff {

    private static final int BOMB_BONUS = 1;   // เพิ่ม max bombs +1

    public MaxBombBuff() {
        super("Max Bomb +1", "Max bombs you can carry +1.");
    }

    @Override
    public void apply(Character target) {
        target.increaseMaxBombs(BOMB_BONUS);
    }
}
