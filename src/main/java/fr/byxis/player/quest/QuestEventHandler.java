package fr.byxis.player.quest;

import de.netzkronehd.wgregionevents.events.RegionEnterEvent;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.jetbrains.annotations.NotNull;

import static fr.byxis.player.quest.QuestManager.*;

public class QuestEventHandler implements @NotNull Listener {

    private final Fireland main;

    public QuestEventHandler(Fireland main)
    {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerJoin(PlayerJoinEvent e)
    {
        if(!QuestManager.getPlayerQuest().containsKey(e.getPlayer().getUniqueId()) || !QuestManager.getPlayerQuest().get(e.getPlayer().getUniqueId()).hasFinished())
        {
            if(e.getPlayer().hasPlayedBefore())
            {
                InGameUtilities.sendPlayerSucces(e.getPlayer(), "De nouvelles quêtes quotidiennes sont disponibles !");
            }
            QuestManager.fillQuests(e.getPlayer());
        }
    }

    @EventHandler
    public void save(WorldSaveEvent e)
    {
        QuestManager.saveProgress();
    }

    @EventHandler
    public void playerKillEntity(MythicMobDeathEvent e)
    {

        if(e.getKiller() instanceof Player p)
        {
            actualiseKillProgress(p, ChatColor.stripColor(e.getEntity().getName()));
        }
    }

    @EventHandler
    public void playerEnterRegion(RegionEnterEvent e)
    {
        actualiseRegionProgress(e.getPlayer(), e.getRegion().getId());
    }

    @EventHandler
    public void playerInteraction(PlayerInteractEvent e)
    {
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            actualiseInteractProgress(e.getPlayer(), e.getMaterial());
            actualiseInteractSpecificProgress(e.getPlayer(), e.getClickedBlock().getLocation());
        }
    }

}
