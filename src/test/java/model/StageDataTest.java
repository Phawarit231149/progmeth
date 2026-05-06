package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class StageDataTest {

    @Test
    void testStageInitialization() {
        StageData stage1 = StageData.ALL_STAGES[0];
        assertEquals(1, stage1.getLevel());
        assertEquals(10, stage1.getGoal());
        assertEquals(11, stage1.getCols());
    }

    @Test
    void testTileAt() {
        StageData stage1 = StageData.ALL_STAGES[0];
        // According to MAP_I, index (1,1) is 'R'
        assertEquals('R', stage1.tileAt(1, 1));
        // Boundary check
        assertEquals('.', stage1.tileAt(-1, -1));
        assertEquals('.', stage1.tileAt(100, 100));
    }

    @Test
    void testSpawnPoints() {
        StageData stage1 = StageData.ALL_STAGES[0];

        // Test Player Spawn 'C'
        int[] playerSpawn = stage1.getPlayerSpawn();
        assertEquals(9, playerSpawn[0]); // Row 9
        assertEquals(5, playerSpawn[1]); // Col 5

        // Test Enemy Spawns 'P'
        List<int[]> enemySpawns = stage1.getEnemySpawns();
        // MAP_I has two 'P's at the bottom corners
        assertEquals(2, enemySpawns.size());
        assertArrayEquals(new int[]{9, 0}, enemySpawns.get(0));
        assertArrayEquals(new int[]{9, 10}, enemySpawns.get(1));
    }

    @Test
    void testAllStagesExist() {
        assertEquals(5, StageData.ALL_STAGES.length);
    }
}