	package fr.byxis.event;

	import de.netzkronehd.wgregionevents.events.RegionEnterEvent;
	import de.netzkronehd.wgregionevents.events.RegionLeftEvent;
	import fr.byxis.main.Main;
	import org.bukkit.GameMode;
	import org.bukkit.entity.Player;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.Listener;
	import org.bukkit.event.entity.EntityDamageByEntityEvent;
	import org.bukkit.event.player.PlayerJoinEvent;

public class worldGuardEvent implements Listener {
	
	private Main main;

	public worldGuardEvent(Main main) {
		this.main = main;
	}

	@EventHandler
	public void leftedRegion(RegionLeftEvent e)
	{
		Player p = e.getPlayer();

		if(e.getRegion().getId().contains("sf"))
		{
			if(p.getGameMode() != GameMode.SPECTATOR || p.getGameMode() != GameMode.CREATIVE)
			{
				p.sendTitle("", "§cVous êtes invincible pendant 15 secondes");
				p.playSound(p.getLocation(), "minecraft:gun.hud.leaving_safezone", 1, 1);
				p.setInvulnerable(true);

				main.cfgm.getPlayerDB().set("safezone."+p.getUniqueId()+".time", 15);
				main.cfgm.getPlayerDB().set("safezone."+p.getUniqueId()+".state", false);
				main.cfgm.savePlayerDB();
			}
		}
		
	}

	@EventHandler
	public void joinedRegion(RegionEnterEvent e)
	{
		Player p = e.getPlayer();

		if(e.getRegion().getId().contains("sf"))
		{
			if(p.getGameMode() != GameMode.SPECTATOR || p.getGameMode() != GameMode.CREATIVE)
			{
				p.sendTitle("", "");
				p.setInvulnerable(false);
				p.playSound(p.getLocation(), "minecraft:gun.hud.enter_safezone", 1, 1);
				main.cfgm.getPlayerDB().set("safezone."+p.getUniqueId()+".time", -1);
				main.cfgm.getPlayerDB().set("safezone."+p.getUniqueId()+".state", true);
				main.cfgm.savePlayerDB();
			}

		}
		
	}
	
	@EventHandler
	public void playerPVP(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			if(e.getDamager().isInvulnerable())
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e)
	{
		main.cfgm.getPlayerDB().set("safezone."+e.getPlayer().getUniqueId()+".time", 0);
		main.cfgm.savePlayerDB();
		e.getPlayer().setInvulnerable(false);
	}

}
