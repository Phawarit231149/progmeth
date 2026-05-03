package game.util;

public class ScoreManager {

    // ── Fields ────────────────────────────────
    private int score;          // current score (จำนวน enemy ที่ฆ่าได้)
    private final int goal;     // target score (เช่น 30) — ถ้าถึง = ชนะ

    // ── Constructor ───────────────────────────
    public ScoreManager(int goal) {
        this.score = 0;
        this.goal  = goal;
    }

    // ── Add kill ──────────────────────────────
    // ฆ่า enemy 1 ตัว → +1 คะแนน
    public void addKill() {
        addKill(1);
    }

    // เผื่ออนาคต — enemy ต่างชนิด ให้คะแนนต่างกันได้
    // (Easy = 1, Medium = 2, Hard = 3 ฯลฯ)
    public void addKill(int points) {
        if (hasReachedGoal()) return;          // ถึง goal แล้ว ไม่บวกต่อ
        score += points;
        if (score > goal) score = goal;        // cap ที่ goal ไม่ให้เกิน
    }

    // ── Win check ─────────────────────────────
    // ถึง goal แล้วหรือยัง? → GameController เรียกเช็คทุกครั้งหลัง kill
    public boolean hasReachedGoal() {
        return score >= goal;
    }

    // ── Reset ─────────────────────────────────
    // ใช้ตอนเริ่ม stage ใหม่ / restart
    public void reset() {
        score = 0;
    }

    // ── Getters ───────────────────────────────
    public int getScore()           { return score; }
    public int getGoal()            { return goal; }
    public int getRemaining()       { return Math.max(goal - score, 0); }   // เหลืออีกกี่ตัวจะชนะ

    // ── Display helper ────────────────────────
    // คืน string พร้อมโชว์ — เช่น "12 / 30"
    public String formatProgress() {
        return score + " / " + goal;
    }
}
