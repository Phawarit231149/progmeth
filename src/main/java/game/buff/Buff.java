package game.buff;

import game.character.Character;

public abstract class Buff {

    // ── Fields ────────────────────────────────
    protected String name;          // ชื่อ buff (ใช้โชว์บน UI)
    protected String description;   // คำอธิบาย buff (ใช้โชว์บน UI)

    // ── Constructor ───────────────────────────
    public Buff(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // ── Abstract method ───────────────────────
    // บังคับให้ subclass บอกว่าจะ apply ผลกับ character ยังไง
    public abstract void apply(Character target);

    // ── Getters ───────────────────────────────
    public String getName()        { return name; }
    public String getDescription() { return description; }
}
