package game.character;

import game.Element;

public class ElectricCharacter extends Character implements Skillable {

    // ── Skill settings ────────────────────────
    // หยุดศัตรูเป็นเวลา 2 วินาที (stun)
    private static final long STUN_DURATION_MS = 2000L;   // 2 วินาที
    private static final int  COOLDOWN_SECONDS = 30;      // cooldown 10 วินาที

    private long lastSkillUseTime = 0L;
    private boolean stunActive    = false;
    private long stunEndTime      = 0L;

    // ── Constructor ───────────────────────────
    // รับ stat ทุกตัวเป็น int (ยังไม่กำหนดตัวเลขจริง ๆ ตอนนี้)
    public ElectricCharacter(int health, int damage, int damageBomb, int bombRange, int maxBombs) {
        super(health, damage, damageBomb, bombRange, maxBombs, Element.ELECTRIC);
    }

    // ── Skillable methods ─────────────────────
    @Override
    public void useSkill() {
        if (!isSkillReady()) return;             // ยังติด cooldown → ใช้ไม่ได้
        long now = System.currentTimeMillis();
        stunActive       = true;
        stunEndTime      = now + STUN_DURATION_MS;
        lastSkillUseTime = now;
    }

    @Override
    public boolean isSkillReady() {
        long now = System.currentTimeMillis();
        return (now - lastSkillUseTime) >= (COOLDOWN_SECONDS * 1000L);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_SECONDS;
    }

    // ── ตรวจสอบว่าศัตรูยังโดน stun อยู่ไหม ─────
    public boolean isStunActive() {
        if (stunActive && System.currentTimeMillis() >= stunEndTime) {
            stunActive = false;                  // หมดเวลา stun แล้ว
        }
        return stunActive;
    }

    @Override
    public long getLastSkillUseTime() {
        return lastSkillUseTime;
    }
    public static long getStunDurationMs() {return STUN_DURATION_MS;}


    // ── Description ───────────────────────────
    @Override
    public String getDescription() {
        return "Electric Character: Stun enemies for 2 seconds.";
    }
}
