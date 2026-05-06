package game.character;

import game.Element;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;

public class FireCharacter extends Character implements Skillable {

    // ── Skill settings ────────────────────────
    // Teleport ไปยังช่อง tile ปกติ (ไม่ใช่ rock / seaweed / ช่องที่มีศัตรู)
    private static final int COOLDOWN_SECONDS = 30;   // cooldown 15 วินาที

    private long lastSkillUseTime = 0L;
    private boolean teleportArmed = false;            // กดสกิลแล้ว รอเลือกช่อง

    // ── Constructor ───────────────────────────
    public FireCharacter(int health, int damage, int bombRange, int maxBombs) {
        super(health, damage, bombRange, maxBombs, Element.FIRE);
    }

    // ── Skillable methods ─────────────────────
    // Inside FireCharacter class

    @Override
    public void useSkill() {
        if (!isSkillReady()) return;
        this.teleportArmed = true;
        System.out.println("Teleport Armed! Click a tile within 5 seconds.");
    }

    // Add this method to cancel if the player is too slow
    public void cancelTeleport() {
        this.teleportArmed = false;
    }

    @Override
    public boolean isSkillReady() {
        long now = System.currentTimeMillis();
        // Simplified the parentheses to avoid syntax errors
        return (now - lastSkillUseTime) >= (getCooldown() * 1000L);
    }

    @Override
    public int getCooldown() {
        return COOLDOWN_SECONDS; // Directly return the value or use a variable
    }

    // ── ตรวจสอบว่า skill teleport พร้อมเลือกช่องไหม ──
    @Override
    public boolean isTeleportArmed() {
        return teleportArmed;
    }

    @Override
    public long getLastSkillUseTime() {
        return lastSkillUseTime;
    }

    /**
     * Teleport ไปยังช่อง (x, y) ได้ก็ต่อเมื่อช่องเป้าหมายเป็น tile ปกติ
     * (ไม่ใช่ rock, seaweed, หรือช่องที่มีศัตรู)
     *
     * @param x          พิกัด x ของช่องเป้าหมาย
     * @param y          พิกัด y ของช่องเป้าหมาย
     * @param targetTile tile ที่ตำแหน่งนั้น (null = ช่องว่างปกติ)
     * @param hasEnemy   true ถ้าตำแหน่งนั้นมีศัตรูอยู่
     * @return true ถ้า teleport สำเร็จ, false ถ้าช่องเป้าหมายไม่ถูกต้อง
     */
    public boolean teleportTo(int x, int y, Tile targetTile, boolean hasEnemy) {
        if (!teleportArmed) return false;                 // ยังไม่ได้กดสกิล
        if (targetTile instanceof Rock)    return false;  // ห้าม → rock
        if (targetTile instanceof Seaweed) return false;  // ห้าม → seaweed
        if (hasEnemy)                      return false;  // ห้าม → ช่องที่มีศัตรู

        // teleport สำเร็จ
        setPos(x, y);
        teleportArmed   = false;
        lastSkillUseTime = System.currentTimeMillis();
        return true;
    }

    // ── Description ───────────────────────────
    @Override
    public String getDescription() {
        return "Fire Character: Teleport to a normal tile " +
                "(not rock, seaweed, or enemy position).";
    }

}
