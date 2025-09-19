package fr.byxis.player.items.serum;

import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fr.byxis.player.items.toxic.InfectedPlayer.setInfection;

public class Serum implements Listener
{

    @EventHandler
    public void playerSoin(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 113)
        {


            InGameUtilities.playWorldSound(p.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

            p.sendMessage("§cVous avez utilisé un serum du berserker !");
            addSerum(p);
            if (!p.getGameMode().equals(GameMode.CREATIVE))
            {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void friendInteraction(PlayerInteractEntityEvent e)
    {
        Player p = e.getPlayer();
        if (p.getItemInHand().getType() == Material.WHEAT_SEEDS && p.getItemInHand().getItemMeta().hasCustomModelData() && p.getItemInHand().getItemMeta().getCustomModelData() == 113)
        {

            if (!(e.getRightClicked() instanceof Player friend))
            {
                return;
            }
            InGameUtilities.playWorldSound(friend.getLocation(), "gun.hud.seringue", SoundCategory.PLAYERS, 1, 1);

            p.sendMessage("§cVous avez utilisé un serum du berserker sur " + friend.getName() + "!");
            friend.sendMessage("§c" + p.getName() + " a utilisé un serum du berserker sur vous !");

            addSerum(friend);

            if (!p.getGameMode().equals(GameMode.CREATIVE))
            {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
        }
    }

    void addSerum(Player p)
    {
        int intRandom = BasicUtilities.generateInt(0, 3);
        if (intRandom == 2)
        {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 120, 4, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 120, 1, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 40, 1, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 4, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * 120, 4, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 5, 0, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 300, 2, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 300, 2, true));
        }
        else
        {
            setInfection(p, true,  1);
        }
    }
}
