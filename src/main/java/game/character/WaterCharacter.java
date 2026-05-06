package game.character;

import game.Element;

public class WaterCharacter extends Character implements Skillable {

    // ── Skill settings ────────────────────────
    // สร้าง shield ป้องกันตัวเอง 1 ครั้ง (cooldown 90 วินาที)
    private static final int COOLDOWN_SECONDS = 30;   // cooldown 90 วินาที

    private long lastSkillUseTime = 0L;

    // ── Constructor ───────────────────────────
    public WaterCharacter(int health, int damage, int bombRange, int maxBombs) {
        super(health, damage, bombRange, maxBombs, Element.WATER);
        setCooldownSeconds(90);
    }

    // ── Skillable methods ─────────────────────
    @Override
    public void useSkill() {
        if (!isSkillReady()) return;              // ยังติด cooldown → ใช้ไม่ได้
        setShield(true);                          // เปิด shield ให้ตัวเอง
        lastSkillUseTime = System.currentTimeMillis();
    }

    @Override
    public boolean isSkillReady() {
        long now = System.currentTimeMillis();
        // Simplified the parentheses to avoid syntax errors
        return (now - lastSkillUseTime) >= (getCooldown() * 1000L);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_SECONDS;
    }

    @Override
    public long getLastSkillUseTime() {
        return lastSkillUseTime;
    }

    // ── Description ───────────────────────────
    @Override
    public String getDescription() {
        return "Water Character: Create a shield to block one incoming attack " +
                "(cooldown 90s).";
    }
}
