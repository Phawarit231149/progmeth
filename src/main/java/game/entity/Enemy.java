package game.entity;

import game.Element;

public abstract class Enemy {
    private int health;
    private int damage;
    private int size;
    private int posX;
    private int posY;
    private Element element;
    private boolean isShielded;
    private Level level;

    public Enemy(int size,int posX,int posY,Element element,boolean isShielded){
        setSize(size);
        setPosX(posX);
        setPosY(posY);
        setElement(element);
        setShielded(isShielded);
        //this.level=level;
    }

    public void damageDealt(Character character){
        int damage = t
    }

    // setter & getter
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public boolean isShielded() {
        return isShielded;
    }

    public void setShielded(boolean shielded) {
        isShielded = shielded;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
