package game.character;

import game.Element;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;

public class FireCharacter extends Character implements Skillable {

    private static final int COOLDOWN_SECONDS = 30;

    private boolean teleportArmed = false;

    // ── Constructor ───────────────────────────
    public FireCharacter(int health, int damage, int bombRange, int maxBombs) {
        super(health, damage, bombRange, maxBombs, Element.FIRE);
    }

    // ── Skillable ─────────────────────────────
    @Override
    public void useSkill() {
        if (!isSkillReady()) return;
        teleportArmed = true;
        recordSkillUse(); // handled in Character base class
    }

    @Override
    public boolean isSkillReady() {
        return (System.currentTimeMillis() - getLastSkillUseTime())
                >= (long) COOLDOWN_SECONDS * 1000L;
    }

    @Override
    public int getCooldown() { return COOLDOWN_SECONDS; }

    // ── Teleport ──────────────────────────────
    @Override
    public boolean isTeleportArmed() { return teleportArmed; }

    public void cancelTeleport() { teleportArmed = false; }

    /**
     * Attempt to teleport to (x, y).
     * Fails if the target is a Rock, Seaweed, or occupied by an enemy.
     */
    public boolean teleportTo(int x, int y, Tile targetTile, boolean hasEnemy) {
        if (!teleportArmed)            return false;
        if (targetTile instanceof Rock)    return false;
        if (targetTile instanceof Seaweed) return false;
        if (hasEnemy)                      return false;
        setPos(x, y);
        teleportArmed = false;
        return true;
    }

    @Override
    public String getDescription() {
        return "Fire Character: Teleport to any open tile (cooldown 30s).";
    }
}