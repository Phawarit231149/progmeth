package game.buff;

import game.character.Character;

public class BombRangeBuff extends Buff {

    private static final int RANGE_BONUS = 1;   // เพิ่ม range +1 ช่อง

    public BombRangeBuff(int r, int c) {
        super(r,c);
    }

    @Override
    public void apply(Character target) {
        target.increaseBombRange(RANGE_BONUS);
    }
}
