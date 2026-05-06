import game.util.ScoreManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreManagerTest {

    private ScoreManager scoreManager;
    private final int GOAL = 10;

    @BeforeEach
    void setUp() {
        // Initialize with a goal of 10 for testing
        scoreManager = new ScoreManager(GOAL);
    }

    @Test
    void testInitialState() {
        assertEquals(0, scoreManager.getScore());
        assertEquals(GOAL, scoreManager.getGoal());
        assertEquals(GOAL, scoreManager.getRemaining());
        assertFalse(scoreManager.hasReachedGoal());
    }

    @Test
    void testSingleKill() {
        scoreManager.addKill(); // Default +1
        assertEquals(1, scoreManager.getScore());
        assertEquals(9, scoreManager.getRemaining());
        assertEquals("1 / 10", scoreManager.formatProgress());
    }

    @Test
    void testMultiplePoints() {
        scoreManager.addKill(3); // Adding 3 points (e.g., Hard Enemy)
        assertEquals(3, scoreManager.getScore());
        assertEquals(7, scoreManager.getRemaining());
    }

    @Test
    void testReachGoalExactly() {
        scoreManager.addKill(10);
        assertTrue(scoreManager.hasReachedGoal());
        assertEquals(10, scoreManager.getScore());
        assertEquals(0, scoreManager.getRemaining());
    }

    @Test
    void testScoreCapping() {
        // Add more than the goal
        scoreManager.addKill(15);

        // Should be capped at 10 based on your logic
        assertEquals(10, scoreManager.getScore());
        assertEquals(0, scoreManager.getRemaining());
        assertTrue(scoreManager.hasReachedGoal());
    }

    @Test
    void testNoIncrementAfterGoalReached() {
        scoreManager.addKill(10); // Goal reached
        scoreManager.addKill(5);  // Should return early due to hasReachedGoal() check

        assertEquals(10, scoreManager.getScore());
    }

    @Test
    void testReset() {
        scoreManager.addKill(5);
        assertEquals(5, scoreManager.getScore());

        scoreManager.reset();
        assertEquals(0, scoreManager.getScore());
        assertEquals(10, scoreManager.getRemaining());
        assertFalse(scoreManager.hasReachedGoal());
    }
}