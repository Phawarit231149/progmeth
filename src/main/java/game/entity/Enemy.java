package game.entity;

import game.character.Character;
import game.Element;

import java.util.Random;

public abstract class Enemy {
    private int health;
    private int damage;
    private int size;
    private int posX;
    private int posY;
    private Element element;
    private boolean isShielded;
    private Level level;
    private int currentDir;   // 0 = up, 1 = down, 2 = left, 3 = right
    protected Random random = new Random();
    private boolean stopEnemy = false;

    public Enemy(int size, int posX,int posY,Element element,boolean isShielded){
        setSize(size);
        setPosX(posX);
        setPosY(posY);
        setElement(element);
        setShielded(isShielded);
        this.currentDir = random.nextInt(4);   // เริ่มสุ่มทิศ 1 ทิศ
        //this.level=level;
    }

    public int getCurrentDir() { return currentDir; }
    public void setCurrentDir(int dir) { this.currentDir = dir; }


    public void moveRandomly(int maxCols, int maxRows) {
        int direction = random.nextInt(4); // สุ่มได้เลข 0, 1, 2, หรือ 3

        int nextX = getPosX();
        int nextY = getPosY();

        // กำหนดทิศทางตามเลขที่สุ่มได้
        switch (direction) {
            case 0 -> nextY--; // 0: เดินขึ้น (Y ลดลง)
            case 1 -> nextY++; // 1: เดินลง (Y เพิ่มขึ้น)
            case 2 -> nextX--; // 2: เดินซ้าย (X ลดลง)
            case 3 -> nextX++; // 3: เดินขวา (X เพิ่มขึ้น)
        }

        // เช็คว่าช่องที่จะเดินไป ไม่ได้หลุดออกนอกขอบของด่าน (Grid)
        if (nextX >= 0 && nextX < maxCols && nextY >= 0 && nextY < maxRows) {
            // ถ้าไม่ทะลุขอบ ก็ให้เดินไปตำแหน่งใหม่ได้
            setPosX(nextX);
            setPosY(nextY);
        }
    }

    //public void attack

    // setter & getter
    public int getHealth() {return health;}
    public int getDamage() {return damage;}
    public int getSize() {return size;}
    public int getPosX() {return posX;}
    public int getPosY() {return posY;}
    public Element getElement() {return element;}
    public boolean isShielded() {return isShielded;}
    public Level getLevel() {return level;}
    public boolean isStopEnemy() {return stopEnemy;}

    public void setHealth(int health) {this.health = health;}
    public void setDamage(int damage) {this.damage = damage;}
    public void setSize(int size) {this.size = size;}
    public void setPosX(int posX) {this.posX = posX;}
    public void setPosY(int posY) {this.posY = posY;}
    public void setElement(Element element) {this.element = element;}
    public void setShielded(boolean shielded) {isShielded = shielded;}
    public void setLevel(Level level) {this.level = level;}
    public void setStopEnemy(boolean stopEnemy) {this.stopEnemy = stopEnemy;}
}
