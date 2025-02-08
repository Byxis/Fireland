package fr.byxis.player.items.morphine;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FallDamage implements Listener
{
    private static FileConfiguration config = null;
    private final Fireland main;

    public FallDamage(Fireland _main)
    {
        this.main = _main;
        config = _main.getCfgm().getPlayerDB();
    }


    @EventHandler
    public void playerFallEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }

        if (e.getCause() == DamageCause.FALL && !player.getWorld().getName().equalsIgnoreCase("essaim")) {
            if (player.isInvulnerable() || e.isCancelled()) {
                return;
            }

            e.setDamage(e.getDamage() * 1.25);

            boolean reduce = switch (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()) {
                case ACACIA_LEAVES, LEGACY_LEAVES_2, BIRCH_LEAVES, DARK_OAK_LEAVES, JUNGLE_LEAVES,
                        OAK_LEAVES, SPRUCE_LEAVES, LEGACY_LEAVES, WHEAT -> true;
                default -> false;
            };

            if (reduce) {
                e.setDamage(e.getDamage() / 2);
            }

            if (!hasLegsBroken(player) && doBreakLegs(e.getDamage())) {
                InGameUtilities.playWorldSound(player.getLocation(), "entity.player.bonebreak", SoundCategory.AMBIENT, 1, 1);
                setLegsBroken(player, true);
                InGameUtilities.sendPlayerError(player, "Vous vous êtes cassé la jambe ! Utilisez une seringue de morphine pour vous soigner.");
            }
        }
    }


    @EventHandler
    public void playerSoin(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 103) {


            if (hasLegsBroken(p)) {
                setLegsBroken(p, false);
                InGameUtilities.playWorldSound(p.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

                p.sendMessage("§8Vous avez soigné votre jambe !");
                if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
            }
        }
    }

    @EventHandler
    public void friendInteraction(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 103) {


            if (!(e.getRightClicked() instanceof Player friend) || hasLegsBroken(p))
            {
                return;
            }
            if (hasLegsBroken(friend)) {
                setLegsBroken(friend, false);

                InGameUtilities.playWorldSound(friend.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

                p.sendMessage("§8Vous avez soigné la jambe de " + friend.getName() + "!");
                friend.sendMessage("§8 " + p.getName() + " a soigné votre jambe !");
                if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
            }
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e)
    {
        setLegsBroken(e.getPlayer(), false);
    }



    public boolean doBreakLegs(double damage)
    {

        return Math.random() * 10 < damage;
    }

    public static boolean hasLegsBroken(Player p)
    {
        if (config.contains("legs." + p.getUniqueId() + ".state"))
        {
            return config.getBoolean("legs." + p.getUniqueId() + ".state");
        }
        return false;
    }

    public static void setLegsBroken(Player p, boolean bool)
    {
        config.set("legs." + p.getUniqueId() + ".state", bool);
    }

}
