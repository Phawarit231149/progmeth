package game.character;

public interface Skillable {
    void useSkill();          // ใช้ skill
    boolean isSkillReady();   // cooldown หมดยัง?
    int getCooldown();// cooldown กี่วินาที
    long getLastSkillUseTime();
}