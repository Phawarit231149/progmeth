package game;

import game.bomb.Bomb;
import game.character.Character;
import game.character.FireCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BombTest {

    private Bomb bomb;
    private Character owner;

    // Test constants
    private final int ROW = 5;
    private final int COL = 10;
    private final int RANGE = 3;
    private final int DAMAGE = 15;

    @BeforeEach
    void setUp() {
        // Initialize an owner for the bomb
        owner = new FireCharacter(5, 1, 1, 5);
        // Create the bomb instance
        bomb = new Bomb(ROW, COL, RANGE, DAMAGE, owner);
    }

    @Test
    void testInitialState() {
        // Verify constructor values are set correctly
        assertEquals(ROW, bomb.getRow());
        assertEquals(COL, bomb.getCol());
        assertEquals(RANGE, bomb.getRange());
        assertEquals(DAMAGE, bomb.getDamage());
        assertEquals(owner, bomb.getOwner());

        // Verify initial exploded state
        assertFalse(bomb.isExploded(), "Bomb should not be exploded initially");
    }

    @Test
    void testDetonate() {
        // Trigger detonation
        bomb.detonate();

        // State should change to true
        assertTrue(bomb.isExploded(), "Bomb state should be 'exploded' after detonate() is called");
    }

    @Test
    void testDetonateMultipleTimes() {
        // Calling detonate multiple times should keep the state as true
        bomb.detonate();
        assertTrue(bomb.isExploded());

        bomb.detonate();
        assertTrue(bomb.isExploded(), "Bomb should remain in exploded state");
    }
}