package game.entity;

import game.Element;
import game.map.Seaweed;
import game.map.Tile;

import java.util.List;

public class HardEnemy extends Enemy {

    public HardEnemy(int size, int posX, int posY, Element element, boolean shielded) {
        // KingNeptune ไม่มีธาตุ — บังคับเป็น NONE เสมอ (โจมตี 2 ดาเมจ neutral)
        super(size, posX, posY, Element.NONE, shielded);
        setLevel(Level.HARD);
        setHealth(50);
        setDamage(2);
    }

    @Override
    public void move(Tile[][] map, Seaweed[][] seaweeds,
                     List<Enemy> enemies,
                     int playerRow, int playerCol,
                     int rows, int cols) {
        moveFollowPlayer(map, seaweeds, enemies,
                playerRow, playerCol, rows, cols);
    }
}