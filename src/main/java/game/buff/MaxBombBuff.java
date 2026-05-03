package game.buff;

import game.character.Character;

public class MaxBombBuff extends Buff{
    public MaxBombBuff(int posX,int posY){
        super(posX,posY);
    }

    @Override
    public void apply(Character character){character.setMaxBombs(character.getMaxBombs() + 1);}
}
