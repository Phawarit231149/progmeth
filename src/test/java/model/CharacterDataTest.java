package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterDataTest {
    private CharacterData character;

    @BeforeEach
    void setUp() {
        // Start at middle position (5, 5)
        character = new CharacterData(5, 5);
    }

    @Test
    void testMovement() {
        character.moveUp();
        assertEquals(4, character.getRow());

        character.moveDown();
        assertEquals(5, character.getRow());

        character.moveLeft();
        assertEquals(4, character.getCol());

        character.moveRight();
        assertEquals(5, character.getCol());
    }

    @Test
    void testHealthLogic() {
        assertTrue(character.isAlive());
        assertEquals(5, character.getHearts());

        character.takeDamage();
        assertEquals(4, character.getHearts());

        // Drain health
        for(int i = 0; i < 5; i++) character.takeDamage();

        assertEquals(0, character.getHearts());
        assertFalse(character.isAlive());
    }

    @Test
    void testBombLogic() {
        assertTrue(character.canPlaceBomb());
        assertEquals(5, character.getBombsLeft());

        character.placeBomb();
        assertEquals(4, character.getBombsLeft());

        // Use all bombs
        for(int i = 0; i < 4; i++) character.placeBomb();
        assertFalse(character.canPlaceBomb());

        character.restoreBombs();
        assertEquals(5, character.getBombsLeft());
        assertTrue(character.canPlaceBomb());
    }
}