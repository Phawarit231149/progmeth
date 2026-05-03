package game.buff;

import game.character.Character;

public class BombDamageBuff extends Buff {
    public BombDamageBuff(int posX,int posY){
        super(posX,posY);
    }

    @Override
    public void apply(Character character) {character.setDamage(character.getDamage() + 1);}
}
