package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameProgressTest {

    @Test
    void testStageUnlocking() {
        // Stage 0 is always unlocked
        assertTrue(GameProgress.isUnlocked(0));

        // Stage 1 should be locked initially
        assertFalse(GameProgress.isCleared(0));
        assertFalse(GameProgress.isUnlocked(1));

        // Mark stage 0 as cleared
        GameProgress.markCleared(0);
        assertTrue(GameProgress.isCleared(0));
        assertTrue(GameProgress.isUnlocked(1));
    }
}