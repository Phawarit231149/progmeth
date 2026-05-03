package game.bomb;

import game.character.Character;

public class Bomb {

    // ── Fields ────────────────────────────────
    private final int row;          // ตำแหน่งแถวบน grid
    private final int col;          // ตำแหน่งคอลัมน์บน grid
    private final int range;        // ระยะระเบิด (ทิศละกี่ช่อง) — snapshot ตอนวาง
    private final int damage;       // ดาเมจของระเบิด — snapshot ตอนวาง
    private final Character owner;  // character ที่เป็นคนวางระเบิด

    private boolean exploded;       // ระเบิดไปแล้วหรือยัง

    // ── Constructor ───────────────────────────
    public Bomb(int row, int col, int range, int damage, Character owner) {
        this.row      = row;
        this.col      = col;
        this.range    = range;
        this.damage   = damage;
        this.owner    = owner;
        this.exploded = false;
    }

    // ── State ─────────────────────────────────
    // เรียกเมื่อระเบิดทำงาน (กด O) — เพื่อกันไม่ให้ระเบิดซ้ำ
    public void detonate() {
        exploded = true;
    }

    public boolean isExploded() {
        return exploded;
    }

    // ── Getters ───────────────────────────────
    public int getRow()           { return row; }
    public int getCol()           { return col; }
    public int getRange()         { return range; }
    public int getDamage()        { return damage; }
    public Character getOwner()   { return owner; }
}
