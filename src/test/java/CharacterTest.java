import game.Element;
import game.character.ElectricCharacter;
import game.character.FireCharacter;
import game.character.WaterCharacter;
import game.map.Rock;
import game.map.Seaweed;
import game.map.Tile;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterTest {

    // ══════════════════════════════════════════════════════════════
    // Health & Shield
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Health & Shield")
    class HealthShieldTest {

        @Test
        void takeDamage_reducesHealth() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.takeDamage(2);
            assertEquals(3, c.getHealth());
        }

        @Test
        void takeDamage_doesNotGoBelowZero() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.takeDamage(999);
            assertEquals(0, c.getHealth());
            assertFalse(c.isAlive());
        }

        @Test
        void shield_absorbsOneDamage() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.setShield(true);
            c.takeDamage(3);
            assertEquals(5, c.getHealth());
            assertFalse(c.hasShield());
        }

        @Test
        void shield_consumed_secondHitDealsNormalDamage() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.setShield(true);
            c.takeDamage(1); // absorbed
            c.takeDamage(2); // hits normally
            assertEquals(3, c.getHealth());
        }

        @Test
        void heal_capsAtMaxHealth() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.takeDamage(3);
            c.heal(10);
            assertEquals(5, c.getHealth());
        }

        @Test
        void heal_fullHealth_noChange() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            c.heal(3);
            assertEquals(5, c.getHealth());
        }

        @Test
        void isAlive_falseWhenHealthIsZero() {
            ElectricCharacter c = new ElectricCharacter(5, 1, 1, 5);
            assertTrue(c.isAlive());
            c.takeDamage(5);
            assertFalse(c.isAlive());
        }

        @Test
        void setHealth_negativeClampsToZero() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.setHealth(-10);
            assertEquals(0, c.getHealth());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Stat Upgrades
    // ══════════════════════════════════════════════════════════════

    @Nested
    class StatUpgradeTest {

        @Test
        void increaseBombRange_byAmount() {
            WaterCharacter c = new WaterCharacter(5, 1, 2, 5);
            c.increaseBombRange(3);
            assertEquals(5, c.getBombRange());
        }

        @Test
        void increaseBombDamage_byAmount() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            c.increaseBombDamage(2);
            assertEquals(3, c.getDamage());
        }

        @Test
        void increaseMaxBombs_byOne() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.increaseMaxBombs(1);
            assertEquals(6, c.getMaxBombs());
        }

        @Test
        void multipleUpgrades_stack() {
            FireCharacter c = new FireCharacter(5, 1, 1, 3);
            c.increaseBombRange(1);
            c.increaseBombRange(1);
            c.increaseBombDamage(1);
            c.increaseMaxBombs(2);
            assertEquals(3, c.getBombRange());
            assertEquals(2, c.getDamage());
            assertEquals(5, c.getMaxBombs());
        }

        @Test
        void setDamage_direct() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.setDamage(5);
            assertEquals(5, c.getDamage());
        }

        @Test
        void setBombRange_direct() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.setBombRange(4);
            assertEquals(4, c.getBombRange());
        }

        @Test
        void setMaxBombs_direct() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.setMaxBombs(10);
            assertEquals(10, c.getMaxBombs());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Element Matchups
    // ══════════════════════════════════════════════════════════════

    @Nested
    class ElementMatchupTest {

        @Test
        void fire_strongAgainst_electric() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            assertTrue(c.isStrongAgainst(Element.ELECTRIC));
            assertFalse(c.isWeakAgainst(Element.ELECTRIC));
        }

        @Test
        void fire_weakAgainst_water() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            assertTrue(c.isWeakAgainst(Element.WATER));
            assertFalse(c.isStrongAgainst(Element.WATER));
        }

        @Test
        void fire_neutral_againstFire() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            assertFalse(c.isStrongAgainst(Element.FIRE));
            assertFalse(c.isWeakAgainst(Element.FIRE));
        }

        @Test
        void water_strongAgainst_fire() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            assertTrue(c.isStrongAgainst(Element.FIRE));
            assertFalse(c.isWeakAgainst(Element.FIRE));
        }

        @Test
        void water_weakAgainst_electric() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            assertTrue(c.isWeakAgainst(Element.ELECTRIC));
            assertFalse(c.isStrongAgainst(Element.ELECTRIC));
        }

        @Test
        void electric_strongAgainst_water() {
            ElectricCharacter c = new ElectricCharacter(5, 1, 1, 5);
            assertTrue(c.isStrongAgainst(Element.WATER));
            assertFalse(c.isWeakAgainst(Element.WATER));
        }

        @Test
        void electric_weakAgainst_fire() {
            ElectricCharacter c = new ElectricCharacter(5, 1, 1, 5);
            assertTrue(c.isWeakAgainst(Element.FIRE));
            assertFalse(c.isStrongAgainst(Element.FIRE));
        }

        @Test
        void allElements_strongAgainst_none() {
            assertTrue(new FireCharacter(5, 1, 1, 5).isStrongAgainst(Element.NONE));
            assertTrue(new WaterCharacter(5, 1, 1, 5).isStrongAgainst(Element.NONE));
            assertTrue(new ElectricCharacter(5, 1, 1, 5).isStrongAgainst(Element.NONE));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Skill Cooldown (shared behaviour)
    // ══════════════════════════════════════════════════════════════

    @Nested
    class SkillCooldownTest {

        @Test
        void allCharacters_skillReady_beforeFirstUse() {
            assertTrue(new FireCharacter(5, 1, 1, 5).isSkillReady());
            assertTrue(new WaterCharacter(5, 1, 1, 5).isSkillReady());
            assertTrue(new ElectricCharacter(5, 1, 1, 5).isSkillReady());
        }

        @Test
        void fire_skillNotReady_afterUse() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            assertFalse(c.isSkillReady());
        }

        @Test
        void water_skillNotReady_afterUse() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            c.useSkill();
            assertFalse(c.isSkillReady());
        }

        @Test
        void electric_skillNotReady_afterUse() {
            ElectricCharacter c = new ElectricCharacter(5, 1, 1, 5);
            c.useSkill();
            assertFalse(c.isSkillReady());
        }

        @Test
        void cooldownValues_correct() {
            assertEquals(30, new FireCharacter(5, 1, 1, 5).getCooldown());
            assertEquals(30, new ElectricCharacter(5, 1, 1, 5).getCooldown());
            assertEquals(60, new WaterCharacter(5, 1, 1, 5).getCooldown());
        }

        @Test
        void remainingCooldown_zeroBeforeUse() {
            assertEquals(0, new FireCharacter(5, 1, 1, 5).getRemainingCooldown());
        }

        @Test
        void remainingCooldown_positiveAfterUse() {
            ElectricCharacter c = new ElectricCharacter(5, 1, 1, 5);
            c.useSkill();
            assertTrue(c.getRemainingCooldown() > 0);
        }

        @Test
        void lastSkillUseTime_updatedAfterUse() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            assertEquals(0L, c.getLastSkillUseTime());
            c.useSkill();
            assertTrue(c.getLastSkillUseTime() > 0L);
        }

        @Test
        void useSkill_onCooldown_doesNotResetTimer() throws InterruptedException {
            ElectricCharacter c = new ElectricCharacter(5, 1, 1, 5);
            c.useSkill();
            long firstTimestamp = c.getLastSkillUseTime();
            Thread.sleep(10);
            c.useSkill(); // should be ignored
            assertEquals(firstTimestamp, c.getLastSkillUseTime());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // FireCharacter — Teleport Skill
    // ══════════════════════════════════════════════════════════════

    @Nested
    class FireTeleportTest {

        @Test
        void teleport_notArmedInitially() {
            assertFalse(new FireCharacter(5, 1, 1, 5).isTeleportArmed());
        }

        @Test
        void useSkill_armsTeleport() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            assertTrue(c.isTeleportArmed());
        }

        @Test
        void cancelTeleport_disarms() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            c.cancelTeleport();
            assertFalse(c.isTeleportArmed());
        }

        @Test
        void teleportTo_succeedsOnPassableTile() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            assertTrue(c.teleportTo(4, 3, new Tile(3, 4), false));
            assertEquals(4, c.getPosX());
            assertEquals(3, c.getPosY());
            assertFalse(c.isTeleportArmed());
        }

        @Test
        void teleportTo_failsOnRock() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            assertFalse(c.teleportTo(4, 3, new Rock(3, 4), false));
            assertTrue(c.isTeleportArmed());
        }

        @Test
        void teleportTo_failsOnSeaweed() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            assertFalse(c.teleportTo(4, 3, new Seaweed(3, 4), false));
        }

        @Test
        void teleportTo_failsWhenEnemyPresent() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            c.useSkill();
            assertFalse(c.teleportTo(4, 3, new Tile(3, 4), true));
        }

        @Test
        void teleportTo_failsWhenNotArmed() {
            FireCharacter c = new FireCharacter(5, 1, 1, 5);
            assertFalse(c.teleportTo(4, 3, new Tile(3, 4), false));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // WaterCharacter — Shield Skill
    // ══════════════════════════════════════════════════════════════

    @Nested
    class WaterShieldSkillTest {

        @Test
        void noShield_initially() {
            assertFalse(new WaterCharacter(5, 1, 1, 5).hasShield());
        }

        @Test
        void useSkill_grantsShield() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            c.useSkill();
            assertTrue(c.hasShield());
        }

        @Test
        void useSkill_shield_absorbsDamage() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            c.useSkill();
            c.takeDamage(3);
            assertEquals(5, c.getHealth());
            assertFalse(c.hasShield());
        }

        @Test
        void useSkill_onCooldown_doesNotGrantShield() {
            WaterCharacter c = new WaterCharacter(5, 1, 1, 5);
            c.useSkill();
            c.setShield(false);
            c.useSkill(); // on cooldown — no-op
            assertFalse(c.hasShield());
        }
    }
}