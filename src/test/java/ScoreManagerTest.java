import game.util.ScoreManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreManagerTest {

    // ── Initial state ─────────────────────────────────────────────

    @Test
    void initialScore_isZero() {
        assertEquals(0, new ScoreManager(10).getScore());
    }

    @Test
    void goal_storedCorrectly() {
        assertEquals(15, new ScoreManager(15).getGoal());
    }

    @Test
    void hasReachedGoal_falseInitially() {
        assertFalse(new ScoreManager(5).hasReachedGoal());
    }

    // ── addKill ───────────────────────────────────────────────────

    @Test
    void addKill_incrementsByOne() {
        ScoreManager sm = new ScoreManager(10);
        sm.addKill(1);
        assertEquals(1, sm.getScore());
    }

    @Test
    void addKill_byPoints() {
        ScoreManager sm = new ScoreManager(20);
        sm.addKill(5);
        assertEquals(5, sm.getScore());
    }

    @Test
    void addKill_accumulates() {
        ScoreManager sm = new ScoreManager(20);
        sm.addKill(1);
        sm.addKill(1);
        sm.addKill(3);
        assertEquals(5, sm.getScore());
    }

    // ── Goal cap ──────────────────────────────────────────────────

    @Test
    void score_cappedAtGoal() {
        ScoreManager sm = new ScoreManager(5);
        for (int i = 0; i < 100; i++) sm.addKill(1);
        assertEquals(5, sm.getScore());
    }

    @Test
    void addKill_noOpAfterGoalReached() {
        ScoreManager sm = new ScoreManager(3);
        sm.addKill(3);   // reaches goal
        sm.addKill(1);    // should be ignored
        assertEquals(3, sm.getScore());
    }

    @Test
    void addKill_largeAmount_cappedAtGoal() {
        ScoreManager sm = new ScoreManager(5);
        sm.addKill(999);
        assertEquals(5, sm.getScore());
    }

    // ── hasReachedGoal ────────────────────────────────────────────

    @Test
    void hasReachedGoal_trueAtGoal() {
        ScoreManager sm = new ScoreManager(3);
        sm.addKill(3);
        assertTrue(sm.hasReachedGoal());
    }

    @Test
    void hasReachedGoal_falseOneBelow() {
        ScoreManager sm = new ScoreManager(5);
        sm.addKill(4);
        assertFalse(sm.hasReachedGoal());
    }

    // ── getRemaining ──────────────────────────────────────────────

    @Test
    void getRemaining_correct() {
        ScoreManager sm = new ScoreManager(10);
        sm.addKill(4);
        assertEquals(6, sm.getRemaining());
    }

    @Test
    void getRemaining_zeroAtGoal() {
        ScoreManager sm = new ScoreManager(5);
        sm.addKill(5);
        assertEquals(0, sm.getRemaining());
    }

    // ── reset ─────────────────────────────────────────────────────

    @Test
    void reset_setsScoreToZero() {
        ScoreManager sm = new ScoreManager(10);
        sm.addKill(7);
        sm.reset();
        assertEquals(0, sm.getScore());
    }

    @Test
    void reset_thenAddKill_works() {
        ScoreManager sm = new ScoreManager(10);
        sm.addKill(10); // reaches goal
        sm.reset();
        sm.addKill(2);
        assertEquals(2, sm.getScore());
    }

    // ── formatProgress ────────────────────────────────────────────

    @Test
    void formatProgress_correctString() {
        ScoreManager sm = new ScoreManager(15);
        sm.addKill(7);
        assertEquals("7 / 15", sm.formatProgress());
    }

    @Test
    void formatProgress_zeroKills() {
        assertEquals("0 / 10", new ScoreManager(10).formatProgress());
    }
}