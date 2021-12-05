package fr.byxis.event;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import fr.byxis.main.Main;

public class villagerInteraction implements Listener {
	
	private final Main main;
	
	public villagerInteraction(Main main) {
		this.main = main;
	}

	@EventHandler
	public void OnInteractionWithVillager(PlayerInteractEntityEvent e)
	{
		Player p = e .getPlayer();
		
		if(!(e.getRightClicked() instanceof Villager) || e.getRightClicked().getCustomName() == null)
		{
			return;
		}
		
		if(e.getRightClicked().getCustomName().contains("Utilitaire"))
		{
			main.commandExecutor(p, "shop utilitaire", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Assaut"))
		{
			main.commandExecutor(p, "shop assaut", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Revolver"))
		{
			main.commandExecutor(p, "shop revolver", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("SMG"))
		{
			main.commandExecutor(p, "shop smg", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Fusil"))
		{
			main.commandExecutor(p, "shop fusil", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("lourdes"))
		{
			main.commandExecutor(p, "shop lourd", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Héliport"))
		{
			main.commandExecutor(p, "heliport", "fireland.command.heliport");
		}
		else if(e.getRightClicked().getCustomName().contains("Banquier"))
		{
			main.commandExecutor(p, "bank", "fireland.command.bank");
		}
		else if(e.getRightClicked().getCustomName().contains("Vendeur de Pass Vert"))
		{
			main.commandExecutor(p, "shop passv", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Vendeur de Pass Bleu"))
		{
			main.commandExecutor(p, "shop passb", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Vendeur de Pass Jaune"))
		{
			main.commandExecutor(p, "shop passj", "fireland.command.shop");
		}
		else if(e.getRightClicked().getCustomName().contains("Vendeur de Pass Rouge"))
		{
			main.commandExecutor(p, "shop passr", "fireland.command.shop");
		}
	}
	
}
