package fr.byxis.essaim;

import fr.byxis.fireland.Fireland;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssaimEventHandler implements Listener {

    private Fireland main;

    public EssaimEventHandler(Fireland main)
    {
        this.main = main;
    }

    @EventHandler
    public void mobKilled(MythicMobDeathEvent e)
    {

        MobDisappearingHandler(e.getMob());
    }

    @EventHandler
    public void mobDispawn(MythicMobDespawnEvent e)
    {
        MobDisappearingHandler(e.getMob());
    }

    private void MobDisappearingHandler(ActiveMob mob)
    {
        if(mob.getLocation().getWorld().getName().equals("essaim"))
        {
            for(String spawner : main.essaimManager.activeSpawners.keySet())
            {
                if(main.essaimManager.activeSpawners.get(spawner).contains(mob))
                {
                    main.essaimManager.activeSpawners.get(spawner).remove(mob);
                    if(main.essaimManager.activeSpawners.get(spawner).size() == 0)
                    {
                        for(String essaim : main.essaimManager.existingEssaims.keySet())
                        {
                            main.getLogger().info(essaim);
                            if(main.essaimManager.existingEssaims.get(essaim).containsKey(spawner))
                            {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), main.essaimManager.existingEssaims.get(essaim).get(spawner).getCommand());
                                break;
                            }
                        }

                        main.essaimManager.activeSpawners.remove(spawner);
                    }
                    break;
                }
            }
        }}

}
