package game.map;

public class Tile {

    // ── Position ──────────────────────────────
    protected int row;
    protected int col;

    // ── Constructor ───────────────────────────
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // ── Movement ──────────────────────────────
    // ค่าเริ่มต้น: เดินผ่านได้ (subclass ที่กั้นทางจะ override เป็น false)
    public boolean isPassable() {
        return true;
    }

    // ── Getters ───────────────────────────────
    public int getRow() { return row; }
    public int getCol() { return col; }
}
