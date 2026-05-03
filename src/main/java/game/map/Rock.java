package game.map;

public class Rock extends Tile {

    // ── Constructor ───────────────────────────
    public Rock(int row, int col) {
        super(row, col);
    }

    // ── Movement ──────────────────────────────
    // Rock = กั้นทาง → character / enemy เดินผ่านไม่ได้
    @Override
    public boolean isPassable() {
        return false;
    }
}
