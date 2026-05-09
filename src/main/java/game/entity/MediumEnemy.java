package game.entity;

import game.Element;
import game.map.Seaweed;
import game.map.Tile;

import java.util.List;

public class MediumEnemy extends Enemy {

    public MediumEnemy(int size, int posX, int posY, Element element, boolean shielded) {
        super(size, posX, posY, element, shielded);
        setLevel(Level.MEDIUM);
        setHealth(10);
        setDamage(2);
    }

    @Override
    public void move(Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb,
                     List<Enemy> enemies,
                     int playerRow, int playerCol,
                     int rows, int cols) {
        moveRandom(map, seaweeds, hasBomb, enemies, rows, cols);
    }
}