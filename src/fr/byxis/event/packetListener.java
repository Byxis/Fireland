package fr.byxis.event;

import org.bukkit.event.Listener;

/*
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.byxis.main.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
*/
public class packetListener implements Listener {

	/*private Main main;
	
	public packetListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onjoin(PlayerJoinEvent e)
	{
		injectPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onleave(PlayerQuitEvent e)
	{
		removePlayer(e.getPlayer());
	}
	
	private void removePlayer(Player player) 
	{
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(()->
		{
			channel.pipeline().remove(player.getName());
			return null;
		});
	}
	
	
	
	private void injectPlayer(Player player)
	{
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() 
		{
			
			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception
			{
				super.channelRead(channelHandlerContext, packet);
			}
			
			@Override
			public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception
			{
				super.write(channelHandlerContext, packet, channelPromise);
			}
			
		};
		
		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
	}
	*/
}
