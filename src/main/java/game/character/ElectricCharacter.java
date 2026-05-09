package game.character;

import game.Element;

public class ElectricCharacter extends Character implements Skillable {

    private static final int  COOLDOWN_SECONDS = 30;
    private static final long STUN_DURATION_MS = 2000L;

    // ── Constructor ───────────────────────────
    public ElectricCharacter(int health, int damage, int bombRange, int maxBombs) {
        super(health, damage, bombRange, maxBombs, Element.ELECTRIC);
    }

    // ── Skillable ─────────────────────────────
    @Override
    public void useSkill() {
        if (isSkillReady()){recordSkillUse();}
    }

    @Override
    public int getCooldown() { return COOLDOWN_SECONDS; }

    public static long getStunDurationMs() { return STUN_DURATION_MS; }

    @Override
    public String getDescription() {
        return "Electric Character: Stun all enemies for 2 seconds.";
    }
}