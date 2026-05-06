import game.Element;
import game.entity.EasyEnemy;
import game.entity.HardEnemy;
import game.entity.MediumEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {
    private EasyEnemy easy;
    private MediumEnemy mediumFire;
    private MediumEnemy mediumWater;
    private MediumEnemy mediumElectric;
    private HardEnemy hardFire;
    private HardEnemy hardWater;
    private HardEnemy hardElectric;

    @BeforeEach
    void setUp(){
        easy = new EasyEnemy(1,0,0,false); // easy always none element

        mediumFire = new MediumEnemy(1,0,0,Element.FIRE,false);
        mediumWater = new MediumEnemy(1,0,0,Element.WATER,false);
        mediumElectric = new MediumEnemy(1,0,0,Element.ELECTRIC,false);

        hardFire = new HardEnemy(2,0,0,Element.FIRE,false);
        hardWater = new HardEnemy(2,0,0,Element.WATER,false);
        hardElectric = new HardEnemy(2,0,0,Element.ELECTRIC,false);
    }

    //@Test
    //void
}
