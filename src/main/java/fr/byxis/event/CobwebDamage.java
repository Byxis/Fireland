package fr.byxis.event;

import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CobwebDamage implements Listener
{
    
    public static void damagePlayerInCobweb(Player p)
    {
        if (p.getLocation().getBlock().getType() == Material.COBWEB
                || p.getLocation().add(0, 1, 0).getBlock().getType() == Material.COBWEB)
        {
            p.damage(0.5D);
            InGameUtilities.playWorldSound(p.getLocation(),
                    "entity.player.hurt_sweet_berry_bush",
                    SoundCategory.PLAYERS, 1, 1);
        }
    }
}
