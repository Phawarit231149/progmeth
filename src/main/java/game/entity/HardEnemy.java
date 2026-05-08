package game.entity;

import game.Element;
import game.map.Seaweed;
import game.map.Tile;

import java.util.List;

public class HardEnemy extends Enemy {

    public HardEnemy(int size, int posX, int posY, Element element, boolean shielded) {
        super(size, posX, posY, element, shielded);
        setLevel(Level.HARD);
        setHealth(50);
        setDamage(2);
    }

    @Override
    public void move(Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb,
                     List<Enemy> enemies,
                     int playerRow, int playerCol,
                     int rows, int cols) {
        moveFollowPlayer(map, seaweeds, hasBomb, enemies,
                playerRow, playerCol, rows, cols);
    }
}