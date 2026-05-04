package game.buff;

import game.character.Character;

public abstract class Buff {

    // ── Fields ────────────────────────────────
    protected int row;          // ชื่อ buff (ใช้โชว์บน UI)
    protected int col;   // คำอธิบาย buff (ใช้โชว์บน UI)

    // ── Constructor ───────────────────────────
    public Buff(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // ── Abstract method ───────────────────────
    // บังคับให้ subclass บอกว่าจะ apply ผลกับ character ยังไง
    public abstract void apply(Character target);

    // ── Getters ───────────────────────────────

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
