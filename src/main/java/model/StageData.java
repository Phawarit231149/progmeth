package model;

public class StageData {
    private final int level;
    private final int rows;
    private final int cols;
    private final int goal;     // จำนวน enemy ที่ต้องฆ่าเพื่อชนะ stage นี้

    public StageData(int level, int rows, int cols, int goal) {
        this.level = level;
        this.rows  = rows;
        this.cols  = cols;
        this.goal  = goal;
    }

    public static final StageData[] ALL_STAGES = {
            new StageData(1, 10, 11, 10),   // Stage I   — 10 kills
            new StageData(2, 10, 13, 15),   // Stage II  — 15 kills
            new StageData(3, 10, 15, 20),   // Stage III — 20 kills
            new StageData(4, 10, 17, 25),   // Stage IV  — 25 kills
            new StageData(5, 10, 19, 30),   // Stage V   — 30 kills (final)
    };

    public int getLevel() { return level; }
    public int getRows()  { return rows; }
    public int getCols()  { return cols; }
    public int getGoal()  { return goal; }
}
