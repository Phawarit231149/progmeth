package game.entity;

import game.Element;
import java.util.Random;

/**
 * Base class for all enemies.
 * Movement logic lives in GameController (it needs map/collision context).
 * This class is pure data + direction state.
 */
public abstract class Enemy {

    private int health;
    private int damage;
    private int size;
    private int posX;
    private int posY;
    private Element element;
    private boolean shielded;
    private Level level;
    private int currentDir;

    protected static final Random RNG = new Random();

    public Enemy(int size, int posX, int posY, Element element, boolean shielded) {
        this.size      = size;
        this.posX      = posX;
        this.posY      = posY;
        this.element   = element;
        this.shielded  = shielded;
        this.currentDir = RNG.nextInt(4); // 0=up 1=down 2=left 3=right
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int     getHealth()     { return health; }
    public int     getDamage()     { return damage; }
    public int     getSize()       { return size; }
    public int     getPosX()       { return posX; }
    public int     getPosY()       { return posY; }
    public Element getElement()    { return element; }
    public boolean isShielded()    { return shielded; }
    public Level   getLevel()      { return level; }
    public int     getCurrentDir() { return currentDir; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setHealth(int h)       { this.health     = h; }
    public void setDamage(int d)       { this.damage     = d; }
    public void setSize(int s)         { this.size       = s; }
    public void setPosX(int x)         { this.posX       = x; }
    public void setPosY(int y)         { this.posY       = y; }
    public void setElement(Element e)  { this.element    = e; }
    public void setShielded(boolean s) { this.shielded   = s; }
    public void setLevel(Level l)      { this.level      = l; }
    public void setCurrentDir(int d)   { this.currentDir = d; }
}