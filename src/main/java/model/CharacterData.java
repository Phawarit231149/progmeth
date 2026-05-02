package model;

public class CharacterData {
    private int row;
    private int col;
    private int hearts;
    private final int maxHearts = 5;
    private int bombsLeft;
    private final int maxBombs = 5;

    public CharacterData(int startRow, int startCol) {
        this.row = startRow;
        this.col = startCol;
        this.hearts = maxHearts;
        this.bombsLeft = maxBombs;
    }

    // ── movement ──────────────────────
    public void moveUp()    { row--; }
    public void moveDown()  { row++; }
    public void moveLeft()  { col--; }
    public void moveRight() { col++; }

    // ── bomb ──────────────────────────
    public boolean canPlaceBomb() { return bombsLeft > 0; }
    public void placeBomb()       { if (canPlaceBomb()) bombsLeft--; }
    public void restoreBombs()    { bombsLeft = maxBombs; }

    // ── health ────────────────────────
    public void takeDamage()      { if (hearts > 0) hearts--; }
    public boolean isAlive()      { return hearts > 0; }

    // ── getters ───────────────────────
    public int getRow()       { return row; }
    public int getCol()       { return col; }
    public int getHearts()    { return hearts; }
    public int getMaxHearts() { return maxHearts; }
    public int getBombsLeft() { return bombsLeft; }
    public int getMaxBombs()  { return maxBombs; }
}