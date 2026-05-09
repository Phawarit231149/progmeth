package game.character;

import game.Element;

public class WaterCharacter extends Character implements Skillable {

    private static final int COOLDOWN_SECONDS = 60;

    // ── Constructor ───────────────────────────
    public WaterCharacter(int health, int damage, int bombRange, int maxBombs) {
        super(health, damage, bombRange, maxBombs, Element.WATER);
    }

    // ── Skillable ─────────────────────────────
    @Override
    public void useSkill() {
        if (!isSkillReady()) return;
        setShield(true);
        recordSkillUse(); // handled in Character base class
    }

    @Override
    public int getCooldown() { return COOLDOWN_SECONDS; }

    @Override
    public String getDescription() {
        return "Water Character: Create a shield to block one incoming attack (cooldown 90s).";
    }
}