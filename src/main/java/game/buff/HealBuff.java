package game.buff;

import game.character.Character;

public class HealBuff extends Buff {

    private static final int HEAL_AMOUNT = 1;   // ฟื้น 1 หัวใจ

    public HealBuff() {
        super("Heal", "Restore +1 HP (capped at max).");
    }

    @Override
    public void apply(Character target) {
        target.heal(HEAL_AMOUNT);
    }
}
