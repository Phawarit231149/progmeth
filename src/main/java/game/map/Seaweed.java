package game.map;

import game.buff.Buff;

public class Seaweed extends Tile {

    // ── Fields ────────────────────────────────
    private Buff hiddenBuff;          // buff ที่ซ่อนอยู่ใน seaweed (null = ไม่มี)
    private boolean destroyed;        // โดนทำลายแล้วหรือยัง

    // ── Constructors ──────────────────────────
    // Seaweed ปกติ — ไม่มี buff ซ่อน
    public Seaweed(int row, int col) {
        super(row, col);
        this.hiddenBuff = null;
        this.destroyed  = false;
    }

    // Seaweed ที่ซ่อน buff ไว้ข้างใน (เช่น HealBuff, BombRangeBuff ฯลฯ)
    public Seaweed(int row, int col, Buff hiddenBuff) {
        super(row, col);
        this.hiddenBuff = hiddenBuff;
        this.destroyed  = false;
    }

    // ── Movement ──────────────────────────────
    // ยังไม่โดนทำลาย → กั้นทาง (เหมือน Rock)
    // โดนทำลายแล้ว → กลายเป็น tile ปกติ → เดินผ่านได้
    @Override
    public boolean isPassable() {
        return destroyed;
    }

    // ── Destroy ───────────────────────────────
    /**
     * ทำลาย seaweed ด้วยระเบิดของ character (enemy ทำลายไม่ได้!
     * ต้องเรียก method นี้จากฝั่ง character's bomb เท่านั้น)
     *
     * @return Buff ที่ซ่อนอยู่ข้างใน (null ถ้าไม่มี หรือถูกทำลายไปแล้ว)
     */
    public Buff destroy() {
        if (destroyed) return null;       // ทำลายไปแล้ว → ทำซ้ำไม่ได้
        destroyed = true;
        Buff revealed = hiddenBuff;       // เปิดเผย buff ที่ซ่อนอยู่
        hiddenBuff = null;                // เคลียร์ buff ออกจาก seaweed
        return revealed;                  // ส่ง buff กลับให้ GameController จัดการต่อ
    }

    // ── Getters ───────────────────────────────
    public boolean isDestroyed()    { return destroyed; }
    public boolean hasHiddenBuff()  { return hiddenBuff != null && !destroyed; }
}
