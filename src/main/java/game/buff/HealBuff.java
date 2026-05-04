package game.buff;

import game.character.Character;

public class HealBuff extends Buff {

    private static final int HEAL_AMOUNT = 1;   // ฟื้น 1 หัวใจ

    public HealBuff(int r, int c) {
        super(r,c);
    }

    @Override
    public void apply(Character target) {
        target.heal(HEAL_AMOUNT);
    }
}
