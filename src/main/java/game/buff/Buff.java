package game.buff;

import game.character.Character;

/** Base class for all map pickups that modify a Character's stats. */
public abstract class Buff {

    protected int row;
    protected int col;

    public Buff(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** Apply this buff's effect to the given character. */
    public abstract void apply(Character target);

    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }
}