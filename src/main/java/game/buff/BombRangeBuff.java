package game.buff;

import game.character.Character;

public class BombRangeBuff extends Buff {

    private static final int RANGE_BONUS = 1;   // เพิ่ม range +1 ช่อง

    public BombRangeBuff() {
        super("Bomb Range +1", "Bomb range +1 tile.");
    }

    @Override
    public void apply(Character target) {
        target.increaseBombRange(RANGE_BONUS);
    }
}
