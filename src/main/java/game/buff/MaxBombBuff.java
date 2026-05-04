package game.buff;

import game.character.Character;

public class MaxBombBuff extends Buff {

    private static final int BOMB_BONUS = 1;   // เพิ่ม max bombs +1

    public MaxBombBuff(int r, int c) {
        super(r,c);
    }

    @Override
    public void apply(Character target) {
        target.increaseMaxBombs(BOMB_BONUS);
    }
}
