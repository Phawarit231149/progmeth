import model.StageData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StageDataTest {

    // ── tileAt ────────────────────────────────────────────────────

    @Test

    void tileAt_returnsEmptyTile() {
        StageData s = StageData.ALL_STAGES[0]; // row 0 of MAP_I is "..........."
        assertEquals('.', s.tileAt(0, 0));
    }

    @Test
    void tileAt_returnsSeaweed() {
        StageData s = StageData.ALL_STAGES[0];
        // MAP_I row 2: ".S.RS.SR.S." — col 1 is 'S'
        assertEquals('S', s.tileAt(2, 1));
    }

    @Test
    void tileAt_returnsRock() {
        StageData s = StageData.ALL_STAGES[0];
        // MAP_I row 1: ".R.......R." — col 1 is 'R'
        assertEquals('R', s.tileAt(1, 1));
    }

    @Test
    void tileAt_returnsPlayerSpawn() {
        StageData s = StageData.ALL_STAGES[0];
        // MAP_I row 9: "P..S.C.S..P" — col 5 is 'C'
        assertEquals('C', s.tileAt(9, 5));
    }

    @Test
    void tileAt_outOfBounds_negativeRow() {
        assertEquals('.', StageData.ALL_STAGES[0].tileAt(-1, 0));
    }

    @Test
    void tileAt_outOfBounds_rowTooLarge() {
        StageData s = StageData.ALL_STAGES[0];
        assertEquals('.', s.tileAt(s.getRows(), 0));
    }

    @Test
    void tileAt_outOfBounds_colTooLarge() {
        StageData s = StageData.ALL_STAGES[0];
        assertEquals('.', s.tileAt(0, s.getCols() + 5));
    }

    // ── getPlayerSpawn ────────────────────────────────────────────

    @Test
    void getPlayerSpawn_findsC() {
        StageData s = StageData.ALL_STAGES[0];
        int[] spawn = s.getPlayerSpawn();
        assertEquals('C', s.tileAt(spawn[0], spawn[1]));
    }

    @Test
    void getPlayerSpawn_allStages() {
        for (StageData s : StageData.ALL_STAGES) {
            int[] spawn = s.getPlayerSpawn();
            assertEquals('C', s.tileAt(spawn[0], spawn[1]),
                    "Stage " + s.getLevel() + " player spawn not found");
        }
    }

    // ── getEnemySpawns ────────────────────────────────────────────

    @Test
    void getEnemySpawns_notEmpty() {
        StageData s = StageData.ALL_STAGES[0];
        assertFalse(s.getEnemySpawns().isEmpty());
    }

    @Test
    void getEnemySpawns_allArePTiles() {
        StageData s = StageData.ALL_STAGES[0];
        for (int[] pos : s.getEnemySpawns())
            assertEquals('P', s.tileAt(pos[0], pos[1]));
    }

    @Test
    void mapI_hasTwoEnemySpawns() {
        assertEquals(2, StageData.ALL_STAGES[0].getEnemySpawns().size());
    }

    // ── ALL_STAGES ────────────────────────────────────────────────

    @Test
    void allStages_hasFiveEntries() {
        assertEquals(5, StageData.ALL_STAGES.length);
    }

    @Test
    void allStages_levelsOneToFive() {
        for (int i = 0; i < 5; i++)
            assertEquals(i + 1, StageData.ALL_STAGES[i].getLevel());
    }

    @Test
    void allStages_rowsAtLeast10() {
        for (StageData s : StageData.ALL_STAGES)
            assertTrue(s.getRows() >= 10, "Stage " + s.getLevel() + " has too few rows");
    }

    @Test
    void allStages_goalsIncreasing() {
        int prev = 0;
        for (StageData s : StageData.ALL_STAGES) {
            assertTrue(s.getGoal() > prev,
                    "Stage " + s.getLevel() + " goal not greater than previous");
            prev = s.getGoal();
        }
    }

    @Test
    void allStages_colsIncreasing() {
        int prev = 0;
        for (StageData s : StageData.ALL_STAGES) {
            assertTrue(s.getCols() > prev,
                    "Stage " + s.getLevel() + " cols not wider than previous");
            prev = s.getCols();
        }
    }
}