package model;

import java.util.ArrayList;
import java.util.List;

public class StageData {
    private final int level;
    private final int rows;
    private final int cols;
    private final int goal;     // จำนวน enemy ที่ต้องฆ่าเพื่อชนะ stage นี้
    private final String[] layout;

    public StageData(int level, int rows, int cols, int goal, String[] layout) {
        this.level  = level;
        this.rows   = rows;
        this.cols   = cols;
        this.goal   = goal;
        this.layout = layout;
    }

    // ─────────────────────────────────────────────
    //  Layout legend
    //    .  = empty tile (เดินผ่านได้)
    //    R  = rock (กั้นทาง + กั้นระเบิด)
    //    S  = seaweed (กั้นทาง แต่ระเบิดทำลายได้)
    //    C  = player spawn point
    //    P  = enemy spawn point (ยังไม่ได้ใช้ — ทำทีหลัง)
    // ─────────────────────────────────────────────

    // ── MAP I  (11 cols × 10 rows) ───────────────
    public static final String[] MAP_I = {
            "...........",
            ".R.......R.",
            ".S.RS.SR.S.",
            ".R.......R.",
            ".....R.....",
            ".R.S...S.R.",
            ".S.RS.SR.S.",
            ".R.......R.",
            "....SRS....",
            "P..S.C.S..P"
    };

    // ── MAP II (13 cols × 10 rows) ───────────────
    public static final String[] MAP_II = {
            "PSR.......SRP",
            "....RR.RR.SR.",
            "S..RSSSSSR.S.",
            "S.RS..C..SRS.",
            "...RS...SR.R.",
            "S...RS.SRSS..",
            "RRSRSRSR...SS",
            "...RS.R.SRR..",
            "...S.S.S.R...",
            "P...........P"
    };

    // ── MAP III (15 cols × 10 rows) ──────────────
    public static final String[] MAP_III = {
            "SS...........SS",
            ".PRS.S.S.S.SRP.",
            ".RR.S.S.S.S.RR.",
            "...............",
            "SSSRSSSRSSSRSSS",
            "SSSRSSSRSSSRSSS",
            "...............",
            ".RR.S.S.S.S.RR.",
            ".PRS.S.S.S.SRP.",
            "SS.....C.....SS"
    };

    // ── MAP IV (17 cols × 10 rows) ───────────────
    public static final String[] MAP_IV = {
            ".S.............S.",
            ".RS...SSSSS...SR.",
            ".RS..S.....S..SR.",
            ".S..S.PR.RP.S..S.",
            ".RS.S.R...R.S.SR.",
            ".RS.S...C...S.SR.",
            ".S..S.R...R.S..S.",
            ".RS.S.PR.RP.S.SR.",
            ".RS..S.....S..SR.",
            ".S....SSSSS....S."
    };

    // ── MAP V  (19 cols × 10 rows) — Final ───────
    public static final String[] MAP_V = {
            "P...RRSSSSSSSRR.SSS",
            "....R.........R.S.S",
            "SS..S.........S.SSS",
            "..S.S..RRSRR..S....",
            "..S.S..RS.SR..S....",
            "SS..S..S.C.S..S....",
            "....S..RS.SR..S....",
            "SSS.R..RRSRR..R....",
            "S.S.RR.SSSSS.RR....",
            "SSS....S...S......P"
    };

    public static final StageData[] ALL_STAGES = {
            new StageData(1, 10, 11, 10, MAP_I),   // Stage I   — 10 kills
            new StageData(2, 10, 13, 15, MAP_II),  // Stage II  — 15 kills
            new StageData(3, 10, 15, 20, MAP_III), // Stage III — 20 kills
            new StageData(4, 10, 17, 25, MAP_IV),  // Stage IV  — 25 kills
            new StageData(5, 10, 19, 30, MAP_V),   // Stage V   — 30 kills (final)
    };

    public int getLevel() { return level; }
    public int getRows()  { return rows; }
    public int getCols()  { return cols; }
    public int getGoal()  { return goal; }
    public String[] getLayout() { return layout; }

    /** อ่าน character ที่ตำแหน่ง (r,c). คืน '.' ถ้าเลย bound */
    public char tileAt(int r, int c) {
        if (r < 0 || r >= rows) return '.';
        String row = layout[r];
        if (c < 0 || c >= row.length()) return '.';
        return row.charAt(c);
    }

    /** หา player spawn (ตัว 'C') — fallback คืน (rows-1, 0) ถ้าไม่เจอ */
    public int[] getPlayerSpawn() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tileAt(r, c) == 'C') return new int[]{r, c};
            }
        }
        return new int[]{rows - 1, 0};
    }

    /** หา enemy spawn ทุกตัว (ตัว 'P') — สำหรับใช้ทีหลัง */
    public List<int[]> getEnemySpawns() {
        List<int[]> spawns = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tileAt(r, c) == 'P') spawns.add(new int[]{r, c});
            }
        }
        return spawns;
    }
}
