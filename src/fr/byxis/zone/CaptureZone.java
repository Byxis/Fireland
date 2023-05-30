package fr.byxis.zone;

import fr.byxis.faction.FactionFunctions;
import fr.byxis.main.Main;
import fr.byxis.main.utilities.BasicUtilities;
import fr.byxis.main.utilities.BlockUtilities;
import fr.byxis.zone.zoneclass.FactionCapturingClass;
import fr.byxis.zone.zoneclass.ZoneClass;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CaptureZone {

    private Main main;
    private DataZone data;
    private final int captureRefreshRate = 2;
    private final int boosterCapture = 50;

    public CaptureZone(Main main, DataZone data)
    {
        this.main = main;
        this.data = data;
    }

    public void Loop()
    {
        new BukkitRunnable(){
            @Override
            public void run() {
                if(data.zones.isEmpty())
                {
                    return;
                }
                for(ZoneClass zone : data.zones) {
                    if (data.zoneInCapture.containsKey(zone.getName()) && !data.zoneInCapture.get(zone.getName()).isEmpty() && !zone.isClaimed())
                    {
                        HashMap<FactionCapturingClass, Integer> capture = new HashMap<>();
                        for (FactionCapturingClass factionCapturing : data.zoneInCapture.get(zone.getName())) {

                            capture.put(factionCapturing, factionCapturing.getPlayerList().size());
                            if(factionCapturing.getPlayerList().size() < 1)
                            {
                                addProgressionTime(zone, factionCapturing.getName(), -2*captureRefreshRate);
                            }
                            if(factionCapturing.getProgression() <= 0 && factionCapturing.getPlayerList().size() < 1)
                            {
                                data.zoneInCapture.get(zone.getName()).remove(factionCapturing);
                            }
                        }
                        int max = 0;
                        FactionCapturingClass factionCapturing = null;
                        FactionCapturingClass factionToUncapture = null;
                        List<FactionCapturingClass> factionInContest = new ArrayList<>();
                        List<FactionCapturingClass> factionInMinority = new ArrayList<>();
                        for (FactionCapturingClass factionCapturingClass : capture.keySet())
                        {
                            if(factionCapturingClass.getPlayerList().size() >= 1)
                            {
                                if(capture.get(factionCapturingClass) > max)
                                {
                                    if(!factionInContest.isEmpty())
                                    {
                                        factionInMinority.addAll(factionInContest);
                                        factionInContest.clear();
                                    }
                                    if(factionCapturing != null)
                                    {
                                        factionInMinority.add(factionCapturing);
                                    }
                                    max = capture.get(factionCapturingClass);
                                    factionCapturing = factionCapturingClass;
                                }
                                else if(capture.get(factionCapturingClass) == max)
                                {
                                    factionInContest.add(factionCapturingClass);
                                    factionInContest.add(factionCapturing);
                                    factionCapturing = null;
                                }
                                else
                                {
                                    factionInMinority.add(factionCapturingClass);
                                }
                                if(factionCapturingClass.getProgression() > 0 && factionCapturingClass != factionCapturing)
                                {
                                    factionToUncapture = factionCapturingClass;
                                }
                            }
                        }
                        if(factionCapturing != null)
                        {
                            if(factionToUncapture != null && factionToUncapture.getProgression() > 0)
                            {
                                addProgressionTime(zone, factionToUncapture.getName(), -2*captureRefreshRate);
                                for(Player p : factionCapturing.getPlayerList())
                                {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Vous décapturez la zone "+zone.getName()+" contrôlée par "+zone.getClaimer()+" ("+((double)Math.round(factionCapturing.getProgression()*100)/100D)+"%)"));
                                }
                            }
                            else
                            {
                                addProgressionTime(zone, factionCapturing.getName(), boosterCapture*captureRefreshRate);
                                for(Player p : factionCapturing.getPlayerList())
                                {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aVous capturez la zone "+zone.getName()+" ("+((double)Math.round(factionCapturing.getProgression()*100)/100D)+"%)"));
                                }
                            }
                        }
                        if(!factionInContest.isEmpty())
                        {
                            for (FactionCapturingClass factionContesting : factionInContest)
                            {
                                for(Player p : factionContesting.getPlayerList())
                                {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§eVous êtes en contestation avec la zone "+zone.getName()));
                                }
                            }
                        }
                        if(!factionInMinority.isEmpty())
                        {
                            for (FactionCapturingClass factionMonority : factionInMinority)
                            {
                                if(zone.getClaimer() == null || (zone.getClaimer() != null && !factionMonority.getName().equalsIgnoreCase(zone.getClaimer())))
                                {
                                    addProgressionTime(zone, factionCapturing.getName(), -2*captureRefreshRate);
                                    for(Player p : factionMonority.getPlayerList())
                                    {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cVous êtes en train de perdre votre capture  "+zone.getName()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(main, 20, 20*captureRefreshRate);
    }

    private void addProgressionTime(ZoneClass zone, String factionName, double seconds)
    {
        if(!data.zoneInCapture.containsKey(zone.getName()))
        {
            return;
        }
        for(FactionCapturingClass factionCapturing : data.zoneInCapture.get(zone.getName()))
        {

            if(factionCapturing.getName().equalsIgnoreCase(factionName))
            {
                double prog = factionCapturing.getProgression();
                double nextProg = factionCapturing.getNextProgression(zone.getCaptureTime(), seconds);

                FactionFunctions ff = new FactionFunctions(main, null);
                String color;
                if(seconds > 0)
                {
                    color = ff.getFactionInfo(factionName).getColorcode();
                }
                else
                {
                    color = "§r";
                }

                playAnimation(zone, prog, nextProg, color, factionCapturing.getPlayerList());

                if(nextProg >= 100)
                {
                    factionCapturing.setProgression(100);
                }
                else
                {
                    factionCapturing.addProgression(zone.getCaptureTime(), seconds);
                }

                if(prog <= 5 && nextProg >5)
                {
                    for(Player p: Bukkit.getOnlinePlayers())
                    {
                        BasicUtilities.sendPlayerError(p, "La faction "+factionName+" est en train de capturer la zone "+zone.getName()+" ! Allez-y vite pour contester la capture !");
                    }
                }
                if(prog >= 100)
                {
                    for(ZoneClass zoneClass : data.zones)
                    {
                        if(zoneClass.getName().equalsIgnoreCase(zone.getName()))
                        {
                            zoneClass.setClaimed(factionName, new Timestamp(System.currentTimeMillis()));
                            break;
                        }
                    }
                    BasicUtilities.playPlayersSound(factionCapturing.getPlayerList(), "item.goat_horn.sound.7", SoundCategory.AMBIENT, 1, 2);
                    data.SaveAll();
                    for(Player p: Bukkit.getOnlinePlayers())
                    {
                        BasicUtilities.sendPlayerInformation(p, "La faction "+factionName+" a capturé la zone "+zone.getName()+" !");
                    }
                    ff.sendFactionPlayer(factionName, "Votre faction a capturé la zone "+zone.getName()+" !");

                    data.zoneInCapture.get(zone.getName()).clear();
                }
            }
            break;
        }
    }

    private void playAnimation(ZoneClass zone, double from, double to, String color, List<Player> players)
    {
        if((from <= 0 && to >= 0) || (from > 0  && to <=0))
        {
            changeAnimationStep(1, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 10 && to >= 10) || (from > 10  && to <=10))
        {
            changeAnimationStep(2, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 20 && to >= 20) || (from > 20  && to <=20))
        {
            changeAnimationStep(3, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 30 && to >= 30) || (from > 30  && to <=30))
        {
            changeAnimationStep(4, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 40 && to >= 40) || (from > 40  && to <=40))
        {
            changeAnimationStep(5, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 50 && to >= 50) || (from > 50  && to <=50))
        {
            changeAnimationStep(6, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 60 && to >= 60) || (from > 60  && to <=60))
        {
            changeAnimationStep(7, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 70 && to >= 70) || (from > 70  && to <=70))
        {
            changeAnimationStep(8, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 80 && to >= 80) || (from > 80  && to <=80))
        {
            changeAnimationStep(9, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from < 90 && to >= 90) || (from > 90  && to <=90))
        {
            changeAnimationStep(10, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
        if((from >= 100 && to >= 100) || (from > 100  && to <=100))
        {
            changeAnimationStep(-1, zone, color);
            BasicUtilities.playPlayersSound(players, "block.note_block.basedrum", SoundCategory.AMBIENT, 1, 1);
        }
    }

    public static void changeAnimationStep(int step, ZoneClass zone, String color)
    {
        if(step == -1)
        {
            for(int i = 0;i<4;i++)
            {
                for(int j = 0;j<4;j++)
                {
                    updateGlassWoolBlocks(zone, i,j,color);
                    updateGlassWoolBlocks(zone, -i,j,color);
                    updateGlassWoolBlocks(zone, -i,-j,color);
                    updateGlassWoolBlocks(zone, i,-j,color);
                }
            }
            updateBannerBlocks(zone, color);
        }
        else if(step == 1)
        {
            updateGlassWoolBlocks(zone, 1,1,color);
            updateGlassWoolBlocks(zone, 2,2,color);
        }
        else if(step == 2)
        {
            updateGlassWoolBlocks(zone, 0,1,color);
            updateGlassWoolBlocks(zone, 0,2,color);
            updateGlassWoolBlocks(zone, 1,2,color);
            updateGlassWoolBlocks(zone, 1,3,color);
            updateGlassWoolBlocks(zone, 2,3,color);
        }
        else if(step == 3)
        {
            updateGlassWoolBlocks(zone, -1,2,color);
            updateGlassWoolBlocks(zone, -2,3,color);
            updateGlassWoolBlocks(zone, -1,3,color);
            updateGlassWoolBlocks(zone, 0,3,color);
        }
        else if(step == 4)
        {
            updateGlassWoolBlocks(zone, -1,1,color);
            updateGlassWoolBlocks(zone, -2,1,color);
            updateGlassWoolBlocks(zone, -2,2,color);
            updateGlassWoolBlocks(zone, -3,2,color);
        }
        else if(step == 5)
        {
            updateGlassWoolBlocks(zone, -3,0,color);
            updateGlassWoolBlocks(zone, -3,1,color);
            updateGlassWoolBlocks(zone, -2,0,color);
            updateGlassWoolBlocks(zone, -1,0,color);
            updateGlassWoolBlocks(zone, -3,-1,color);
        }
        else if(step == 6)
        {
            updateGlassWoolBlocks(zone, -1,-1,color);
            updateGlassWoolBlocks(zone, -2,-1,color);
            updateGlassWoolBlocks(zone, -2,-2,color);
            updateGlassWoolBlocks(zone, -3,-2,color);
        }
        else if(step == 7)
        {
            updateGlassWoolBlocks(zone, 0,-1,color);
            updateGlassWoolBlocks(zone, 0,-2,color);
            updateGlassWoolBlocks(zone, -1,-2,color);
            updateGlassWoolBlocks(zone, -1,-3,color);
            updateGlassWoolBlocks(zone, -2,-3,color);
        }
        else if(step == 8)
        {
            updateGlassWoolBlocks(zone, 1,-1,color);
            updateGlassWoolBlocks(zone, 1,-2,color);
            updateGlassWoolBlocks(zone, 1,-3,color);
            updateGlassWoolBlocks(zone, 0,-3,color);
            updateGlassWoolBlocks(zone, 2,-3,color);
            updateGlassWoolBlocks(zone, 2,-2,color);
        }
        else if(step == 9)
        {
            updateGlassWoolBlocks(zone, 1,0,color);
            updateGlassWoolBlocks(zone, 2,0,color);
            updateGlassWoolBlocks(zone, 2,-1,color);
            updateGlassWoolBlocks(zone, 3,-1,color);
            updateGlassWoolBlocks(zone, 3,-2,color);
        }
        else if(step == 10)
        {
            updateGlassWoolBlocks(zone, 3,0,color);
            updateGlassWoolBlocks(zone, 2,1,color);
            updateGlassWoolBlocks(zone, 3,1,color);
            updateGlassWoolBlocks(zone, 3,2,color);
        }
    }

    private static void updateGlassWoolBlocks(ZoneClass zone, int x, int z, String color)
    {
        World world = Bukkit.getWorld("world");
        Location glass = new Location(world, zone.getLocation().getX()+x, zone.getLocation().getY(), zone.getLocation().getZ()+z);
        Location wool = new Location(world, zone.getLocation().getX()+x, zone.getLocation().getY()-1, zone.getLocation().getZ()+z);

        if(glass.getBlock().getType().toString().endsWith("_STAINED_GLASS") && wool.getBlock().getType().toString().endsWith("_WOOL"))
        {
            glass.getBlock().setType(BlockUtilities.getGlassBlockColor(color));
            wool.getBlock().setType(BlockUtilities.getWoolColor(color));
        }
    }

    private static void updateBannerBlocks(ZoneClass zone, String color)
    {
        World world = Bukkit.getWorld("world");
        Location banner1 = new Location(world, zone.getLocation().getX()+1, zone.getLocation().getY()+6, zone.getLocation().getZ()+1);
        Location banner2 = new Location(world, zone.getLocation().getX()+1, zone.getLocation().getY()+6, zone.getLocation().getZ()-1);
        Location banner3 = new Location(world, zone.getLocation().getX()-1, zone.getLocation().getY()+6, zone.getLocation().getZ()+1);
        Location banner4 = new Location(world, zone.getLocation().getX()-1, zone.getLocation().getY()+6, zone.getLocation().getZ()-1);
        List<Location> locations = new ArrayList<>();
        locations.add(banner1);
        locations.add(banner2);
        locations.add(banner3);
        locations.add(banner4);
        Material newMaterial = BlockUtilities.getBannerWallColor(color);

        if(banner1.getBlock().getType().toString().endsWith("_BANNER"))
        {
            for(Location loc : locations)
            {
                final BlockFace bf = ((Directional)loc.getBlock().getBlockData()).getFacing();
                loc.getBlock().setType(newMaterial);

                Directional directional = (Directional) loc.getBlock().getBlockData();

                directional.setFacing(bf);

                loc.getBlock().setBlockData(directional);

            }

        }
    }
}
