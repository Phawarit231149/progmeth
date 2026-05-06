package model;

import java.util.HashSet;
import java.util.Set;

public class GameProgress {
    private static final Set<Integer> clearedStages = new HashSet<>();

    public static void markCleared(int stageIndex) {
        clearedStages.add(stageIndex);
    }

    public static boolean isCleared(int stageIndex) {
        return clearedStages.contains(stageIndex);
    }

    // Stage is unlocked if it's stage 0, or the previous stage is cleared
    public static boolean isUnlocked(int stageIndex) {
        //if (stageIndex == 0) return true;
        //return clearedStages.contains(stageIndex - 1);
        return true;
    }
}