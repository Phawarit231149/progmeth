import model.GameProgress;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameProgressTest {

    // ── Always-unlocked stage 0 ───────────────────────────────────

    @Test
    @Order(1)
    void stage0_alwaysUnlocked() {
        assertTrue(GameProgress.isUnlocked(0));
    }

    // ── isCleared ─────────────────────────────────────────────────

    @Test
    @Order(2)
    void isCleared_falseIfNotMarked() {
        // Stage 4 has not been touched yet in this run
        assertFalse(GameProgress.isCleared(4));
    }

    @Test
    @Order(3)
    void markCleared_thenIsCleared_true() {
        GameProgress.markCleared(0);
        assertTrue(GameProgress.isCleared(0));
    }

    // ── Unlock chain ──────────────────────────────────────────────

    @Test
    @Order(4)
    void clearStage0_unlocks1() {
        // stage 0 already marked in previous test
        assertTrue(GameProgress.isUnlocked(1));
    }

    @Test
    @Order(5)
    void stage2_lockedUntilStage1Cleared() {
        // stage 1 not yet marked
        assertFalse(GameProgress.isUnlocked(2));
    }

    @Test
    @Order(6)
    void clearStage1_unlocks2() {
        GameProgress.markCleared(1);
        assertTrue(GameProgress.isUnlocked(2));
    }

    @Test
    @Order(7)
    void clearStage2_unlocks3() {
        GameProgress.markCleared(2);
        assertTrue(GameProgress.isUnlocked(3));
    }

    @Test
    @Order(8)
    void clearStage3_unlocks4() {
        GameProgress.markCleared(3);
        assertTrue(GameProgress.isUnlocked(4));
    }

    @Test
    @Order(9)
    void clearStage4_isCleared() {
        GameProgress.markCleared(4);
        assertTrue(GameProgress.isCleared(4));
    }

    // ── markCleared is idempotent ─────────────────────────────────

    @Test
    @Order(10)
    void markCleared_idempotent() {
        assertDoesNotThrow(() -> {
            GameProgress.markCleared(0);
            GameProgress.markCleared(0);
        });
        assertTrue(GameProgress.isCleared(0));
    }
}