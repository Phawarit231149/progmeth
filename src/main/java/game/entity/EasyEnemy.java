package game.entity;

import game.Element;
import game.map.Seaweed;
import game.map.Tile;

import java.util.List;

public class EasyEnemy extends Enemy {

    public EasyEnemy(int size, int posX, int posY, boolean shielded) {
        super(size, posX, posY, Element.NONE, shielded);
        setLevel(Level.EASY);
        setHealth(1);
        setDamage(1);
    }

    @Override
    public void move(Tile[][] map, Seaweed[][] seaweeds, boolean[][] hasBomb,
                     List<Enemy> enemies,
                     int playerRow, int playerCol,
                     int rows, int cols) {
        moveRandom(map, seaweeds, hasBomb, enemies, rows, cols);
    }
}