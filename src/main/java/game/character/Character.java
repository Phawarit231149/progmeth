package game.character;

import game.Element;

/**
 * Base class for player characters.
 *
 * Element triangle:  FIRE > ELECTRIC > WATER > FIRE
 * Skill cooldown is managed here; concrete skills live in subclasses.
 */
public abstract class Character implements Skillable {

    protected int health;
    protected int maxHealth;
    protected int damage;
    protected int bombRange;
    protected int maxBombs;
    protected boolean haveShield;
    protected boolean immortal;
    protected int posX;
    protected int posY;
    protected Element element;
    private int  cooldownSeconds;
    private long lastSkillUseTime = 0L;
    private static final long immortalDuration = 2000L;

    protected Character(int health, int damage, int bombRange,
                        int maxBombs, Element element) {
        this.health     = health;
        this.damage     = damage;
        this.bombRange  = bombRange;
        this.maxBombs   = maxBombs;
        this.element    = element;

        this.maxHealth  = health;
        this.haveShield = false;
        this.immortal = false;
    }

    // ── Damage ────────────────────────────────────────────────────────────

    public void takeDamage(int amount) {
        if (haveShield) { haveShield = false;}
        else{health = Math.max(0, health - amount);}
    }

    // ── Skill (subclasses override) ───────────────────────────────────────

    public void useSkill() {}

    // ── Skill cooldown helpers ────────────────────────────────────────────

    public boolean isSkillReady() {
        return (System.currentTimeMillis() - lastSkillUseTime)
                >= (long) getCooldown() * 1000L;
    }

    protected void recordSkillUse() {
        lastSkillUseTime = System.currentTimeMillis();
    }

    public int getRemainingCooldown() {
        long elapsed = System.currentTimeMillis() - lastSkillUseTime;
        long cooldownMs = (long) getCooldown() * 1000L;
        return elapsed >= cooldownMs ? 0 : (int) ((cooldownMs - elapsed) / 1000);
    }

    // ── Element helpers ───────────────────────────────────────────────────

    public boolean isStrongAgainst(Element other) {
        return (element == Element.FIRE     && other == Element.ELECTRIC) ||
                (element == Element.WATER    && other == Element.FIRE)     ||
                (element == Element.ELECTRIC && other == Element.WATER)    ||
                (other   == Element.NONE);
    }

    public boolean isWeakAgainst(Element other) {
        return (element == Element.FIRE     && other == Element.WATER)    ||
                (element == Element.WATER    && other == Element.ELECTRIC) ||
                (element == Element.ELECTRIC && other == Element.FIRE);
    }

    // ── Buff helpers ──────────────────────────────────────────────────────

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public void increaseBombRange(int amount)  { bombRange += amount; }
    public void increaseBombDamage(int amount) { damage    += amount; }
    public void increaseMaxBombs(int amount)   { maxBombs  += amount; }

    // ── Abstract ──────────────────────────────────────────────────────────

    public abstract String getDescription();

    // ── Getters ───────────────────────────────────────────────────────────

    public int     getHealth()    { return health; }
    public int     getMaxHealth() { return maxHealth; }
    public int     getDamage()    { return damage; }
    public int     getBombRange() { return bombRange; }
    public int     getMaxBombs()  { return maxBombs; }
    public boolean hasShield()    { return haveShield; }
    public int     getPosX()      { return posX; }
    public int     getPosY()      { return posY; }
    public Element getElement()   { return element; }
    public boolean isAlive()      { return health > 0; }
    public boolean isImmortal()    { return immortal; }
    public static long getImmortalDuration() {return immortalDuration;}
    public long getLastSkillUseTime() { return lastSkillUseTime; }
    // ── Setters ───────────────────────────────────────────────────────────

    public void setPos(int x, int y)        { posX = x; posY = y; }
    public void setShield(boolean s)        { haveShield = s; }
    public void setHealth(int h)            { health    = Math.max(0, h); }
    public void setMaxHealth(int h)         { maxHealth = h; }
    public void setDamage(int d)            { damage    = d; }
    public void setBombRange(int r)         { bombRange = r; }
    public void setMaxBombs(int m)          { maxBombs  = m; }
    public void setCooldownSeconds(int s)   { cooldownSeconds = s; }
    public void setHaveShield(boolean s)    { haveShield = s; }
    public void setPosX(int x)              { posX = x; }
    public void setPosY(int y)              { posY = y; }
    public void setElement(Element e)       { element = e; }
    public boolean setImmortal(boolean s)   { immortal = s; return true; }

}