package game.buff;

import game.character.Character;

public class HealBuff extends Buff{
    public HealBuff(int posX,int posY){
        super(posX,posY);
    }

    @Override
    public void apply(Character character) {character.setHealth(character.getHealth() + 1);}
}
