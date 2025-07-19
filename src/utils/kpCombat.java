package utils;

import data.kpSpecialAttacks;
import data.kpWeapons;
import ids.ItemId;
import jag.game.scene.entity.RSEntityMarker;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.Vars;
import org.rspeer.game.adapter.component.inventory.Equipment;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.component.tdi.*;
import org.rspeer.game.effect.Health;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;

public class kpCombat
{
    /**
     * Supports both Ferox Enclave pool of refreshment and POH ornate rejuvenation pool
     * @return
     */
    public static boolean CleanseStatsWithPool()
    {
        Player self = Players.self();
        if (self == null)
        {
            return false;
        }

        boolean needsRefreshment = Movement.getRunEnergy() < 40;

        if (!needsRefreshment)
        {
            for (Skill skill : Skill.values())
            {
                if (Skills.getCurrentLevel(skill) < Skills.getLevel(skill))
                {
                    needsRefreshment = true;
                    break;
                }
            }
        }

        if (needsRefreshment)
        {
            SceneObject poolOfRefreshment = SceneObjects.query().types(RSEntityMarker.class).names("Pool of Refreshment", "Ornate pool of Rejuvenation").actions("Drink").results().nearest();

            if (poolOfRefreshment == null)
            {
                //Log.warn("Pool of Refreshment not found in Ferox Enclave.");
                return false;
            }

            if (self.getAnimationId() == 7305)
            {
                Log.info("Pool of Refreshment drinking animation...");
                return true;
            }

            Log.info("Drinking from Pool of Refreshment");
            kpUtils.SafeInteractWith(poolOfRefreshment, "Drink"); // Also disables prayers
            return true;
        }

        return false;
    }

    /**
     * Attacks an NPC
     * @param npc the NPC to attack
     * @param specialAttackWeaponName the name of the weapon to use for special attack, null if wishing to not use special attacks
     * @param freeToAttack whether we want to interact with the NPC regardless of us being ready to attack yet (kpGameData.CanAttack())
     */
    public static void Attack(Npc npc, boolean freeToAttack)
    {
        if (npc == null)
        {
            Log.warn("Trying to attack a null npc");
            return;
        }

        if (npc.isDying() || npc.getHeadbarPercent(21) <= 0)
        {
            Log.info("Npc " + npc.getName() + " is dying, not attacking");
            return;
        }

        if (!kpGameData.ShouldResetAttack() && npc.equals(Global.Data.localTarget))
        {
            Log.info("Already attacking " + npc.getName());
            return;
        }

        if (freeToAttack || kpGameData.CanAttack())
        {
            Log.info("Attacking " + npc.getName());
            npc.interact("Attack");
            return;
        }

        Log.debug("Didn't attack " + npc.getName() + " because we are not free to attack");
        return;
    }

    public static enum SpecialAttackResult
    {
        SUCCESS,
        NOT_ENOUGH_SPECIAL_ENERGY,
        FAILED_NO_SPECIAL_ATTACK_WEAPON,
        FAILED_2H_WEAPON_NO_SPACE,
        FAILED
    }

    public static SpecialAttackResult DoSpecialAttack(Npc npc, String specialAttackWeaponName)
    {
        if (specialAttackWeaponName == null || specialAttackWeaponName.isEmpty())
        {
            Log.warn("No special attack weapon name set");
            return SpecialAttackResult.FAILED;
        }

        int specialEnergyNeeded = kpSpecialAttacks.GetSpecialAttackCost(specialAttackWeaponName);

        if (specialEnergyNeeded == 0 || specialEnergyNeeded == Integer.MAX_VALUE)
        {
            Log.warn("Failed to get special attack cost for " + specialAttackWeaponName);
            return SpecialAttackResult.FAILED;
        }

        int specialEnergy = Combat.getSpecialEnergy();
        if (specialEnergy >= specialEnergyNeeded)
        {
            Item equippedItem = Inventories.equipment().getItemAt(Equipment.Slot.MAINHAND); // Could also be offhand lol, but there is no way anyone is using an offhand spec weapon
            if (equippedItem == null || !equippedItem.getName().equals(specialAttackWeaponName))
            {
                Log.info("Equipping special attack weapon: " + specialAttackWeaponName);
                Item specWeapon = Inventories.backpack().query().nameContains(specialAttackWeaponName).unnoted().results().first();

                if (specWeapon == null)
                {
                    Log.warn("No special attack weapon found in backpack: " + specialAttackWeaponName);
                    return SpecialAttackResult.FAILED_NO_SPECIAL_ATTACK_WEAPON;
                }

                if (Inventories.backpack().isFull() &&
                        Inventories.equipment().getItemAt(Equipment.Slot.OFFHAND) != null &&
                        kpWeapons.IsTwoHanded(specialAttackWeaponName) &&
                        !kpWeapons.IsTwoHanded(equippedItem.getName()))
                {
                    return SpecialAttackResult.FAILED_2H_WEAPON_NO_SPACE;
                }

                if (kpUtils.CloseInterfacesIfNeeded())
                {
                    Log.info("Closing interfaces for spec");
                    return SpecialAttackResult.FAILED;
                }

                kpGameData.SetWeaponName(specialAttackWeaponName);
                specWeapon.interact("Wield"); // Is wield the only action for weapons?
                kpGameData.ResetAttack();
            }

            Log.info("Special attacking");

            if (Combat.isSpecialBarPresent() && !Combat.isSpecialActive())
            {
                Log.info("Activating special attack for " + specialAttackWeaponName);
                Combat.toggleSpecial(true);
            }

            Attack(npc, true);
            return SpecialAttackResult.SUCCESS;
        }

        Log.debug("Not enough special energy (" + specialEnergy + "/" + specialEnergyNeeded + ") to use " + specialAttackWeaponName);
        return SpecialAttackResult.NOT_ENOUGH_SPECIAL_ENERGY;
    }

    public static void SpawnThralls()
    {
        final int RESURRECT_THRALL = 12413;
        final int RESURRECT_THRALL_COOLDOWN = 12290;
        final int MAGIC_THRALL_NPC_ID = 10880;

        int thrallResurrected = Vars.get(Vars.Type.VARBIT, RESURRECT_THRALL);
        int thrallCooldown = Vars.get(Vars.Type.VARBIT, RESURRECT_THRALL_COOLDOWN);

        Log.debug("Thrall resurrected: " + thrallResurrected + " Thrall cooldown: " + thrallCooldown);

        if (!Magic.getBook().equals(Magic.Book.ARCEUUS))
        {
            Log.warn("Wrong spellbook for thralls");
            return;
        }

        // Need to check cd since the active varbit is set to 1 only after the initial spawn animation is done.
        // The RESURRECT_THRALL varbit stays at one if we leave the instance the thrall is in, so we have to check the Npc
        if (thrallCooldown != 0 || (thrallResurrected == 1 && !Npcs.query().ids(MAGIC_THRALL_NPC_ID).results().isEmpty()))
        {
            return;
        }

        Spell spellToCast = Magic.canCast(Spell.Arceuus.RESURRECT_GREATER_GHOST) ? Spell.Arceuus.RESURRECT_GREATER_GHOST
                : Magic.canCast(Spell.Arceuus.RESURRECT_SUPERIOR_GHOST) ? Spell.Arceuus.RESURRECT_SUPERIOR_GHOST
                : Magic.canCast(Spell.Arceuus.RESURRECT_LESSER_GHOST) ? Spell.Arceuus.RESURRECT_LESSER_GHOST
                : null;

        if (spellToCast == null)
        {
            Log.warn("Cannot cast any thrall spells");
            return;
        }

        if (Inventories.backpack().query().ids(ItemId.BOOK_OF_THE_DEAD).results().isEmpty() && Inventories.equipment().query().ids(ItemId.BOOK_OF_THE_DEAD).results().isEmpty())
        {
            Log.severe("No book of the dead found in backpack or equipment for thralls");
            return;
        }

        Log.info("Spawning thrall");
        Magic.cast(spellToCast);
        kpGameData.ResetAttack();

        return;
    }
}
