package game.character;

import game.Element;

import java.util.Timer;

public abstract class Character implements Skillable {

    // Stats
    protected int health;
    protected int maxHealth;
    protected int damage;
    //protected int damageBomb;
    protected int bombRange;
    protected int maxBombs;
    protected boolean haveShield;
    private int COOLDOWN_SECONDS;
    private long lastSkillUseTime = 0L;

    // Position
    protected int posX;
    protected int posY;

    // Element
    protected Element element;

    public Character(int health, int damage, int damageBomb,
                     int bombRange, int maxBombs, Element element) {
        this.health     = health;
        this.maxHealth  = health;
        this.damage     = damage;
        //this.damageBomb = damageBomb;
        this.bombRange  = bombRange;
        this.maxBombs   = maxBombs;
        this.haveShield = false;
        this.element    = element;
    }

    // ── รับดาเมจ ──────────────────────────────
    public void takeDamage(int incomingDamage) {
        if (haveShield) {
            haveShield = false; // shield หายไป 1 ครั้ง
            return;             // ไม่โดนดาเมจ
        }
        health -= incomingDamage;
        if (health < 0) health = 0;
    }

    public int calculateDamage(Element enemyElement) {
        if (enemyElement == null) {
            return damage; // ดาเมจปกติ
        }
        if (isWeakAgainst(enemyElement)) {
            return damage / 2; // อ่อนแอ → ดาเมจครึ่งนึง
        }
        if (isStrongAgainst(enemyElement)) {
            return damage * 2; // แข็งแกร่ง → ดาเมจสองเท่า
        }
        return damage; // เท่ากัน → ดาเมจปกติ
    }

    // ── ตรวจสอบ element ───────────────────────
    private boolean isStrongAgainst(Element other) {
        return (element == Element.FIRE    && other == Element.WATER)    ||
                (element == Element.WATER   && other == Element.ELECTRIC) ||
                (element == Element.ELECTRIC && other == Element.FIRE);
    }

    private boolean isWeakAgainst(Element other) {
        return (element == Element.FIRE    && other == Element.ELECTRIC) ||
                (element == Element.WATER   && other == Element.FIRE)     ||
                (element == Element.ELECTRIC && other == Element.WATER);
    }

    public int getRemainingCoolDown(){
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastSkillUseTime;
        long cooldownMs = getCooldown() * 1000L;

        if (timeElapsed >= cooldownMs) return 0;

        // Calculate remaining seconds
        return (int) ((cooldownMs - timeElapsed) / 1000);
    }

    public void useSkill(){}
    public boolean isTeleportArmed(){return false;}
    // ── รับดาเมจจาก element ───────────────────
    // ใช้ตอน enemy โจมตีเรา (enemy ไม่มี element)
    public void takeDamageFromEnemy(int baseDamage) {
        takeDamage(baseDamage); // ปกติ 1 heart
    }

    // ── Getters ───────────────────────────────
    public int getHealth()      { return health; }
    public int getMaxHealth()   { return maxHealth; }
    public int getDamage()      { return damage; }
    //public int getDamageBomb()  { return damageBomb; }
    public int getBombRange()   { return bombRange; }
    public int getMaxBombs()    { return maxBombs; }
    public boolean hasShield()  { return haveShield; }
    public int getPosX()        { return posX; }
    public int getPosY()        { return posY; }
    public Element getElement() { return element; }

    // ── Setters ───────────────────────────────
    public void setPos(int x, int y) { posX = x; posY = y; }
    public void setShield(boolean s) { haveShield = s; }
    public boolean isAlive()         { return health > 0; }

    public void setCOOLDOWN_SECONDS(int COOLDOWN_SECONDS) {
        this.COOLDOWN_SECONDS = COOLDOWN_SECONDS;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /*
    public void setDamageBomb(int damageBomb) {
        this.damageBomb = damageBomb;
    }
     */

    public void setBombRange(int bombRange) {
        this.bombRange = bombRange;
    }

    public void setMaxBombs(int maxBombs) {
        this.maxBombs = maxBombs;
    }

    public void setHaveShield(boolean haveShield) {
        this.haveShield = haveShield;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    // ── Buff helpers ──────────────────────────
    // เพิ่ม health (cap ที่ maxHealth ไม่ให้เกิน)
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    // เพิ่ม bomb range
    public void increaseBombRange(int amount) {
        bombRange += amount;
    }

    // เพิ่ม damage ของระเบิด
    public void increaseBombDamage(int amount) {
        damage += amount;
    }

    // เพิ่มจำนวนระเบิดที่ถือได้สูงสุด
    public void increaseMaxBombs(int amount) {
        maxBombs += amount;
    }

    // ── Abstract ──────────────────────────────
    public abstract String getDescription();
}