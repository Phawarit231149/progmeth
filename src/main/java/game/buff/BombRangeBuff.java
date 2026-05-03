package game.buff;

import game.character.Character;

public class BombRangeBuff extends Buff {
    public BombRangeBuff(int posX,int posY){
        super(posX,posY);
    }

    @Override
    public void apply(Character character) {character.setBombRange(character.getBombRange() + 1);}
}
