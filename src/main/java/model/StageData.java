package model;

public class StageData {
    private final int level;
    private final int rows;
    private final int cols;

    public StageData(int level, int rows, int cols) {
        this.level = level;
        this.rows = rows;
        this.cols = cols;
    }

    public static final StageData[] ALL_STAGES = {
            new StageData(1, 10, 11),  // Stage I
            new StageData(2, 10, 13),  // Stage II
            new StageData(3, 10, 15),  // Stage III
            new StageData(4, 10, 17),  // Stage IV
            new StageData(5, 10, 19),  // Stage V
    };

    public int getLevel() { return level; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }

}
