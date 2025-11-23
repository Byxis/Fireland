package fr.byxis.faction.zone;

import static fr.byxis.player.level.LevelStorage.addPlayerXp;

import fr.byxis.faction.faction.FactionFunctions;
import fr.byxis.faction.zone.zoneclass.FactionCapturingClass;
import fr.byxis.faction.zone.zoneclass.ZoneClass;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BlockUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.level.LevelStorage;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CaptureZone
{

    private static Fireland main;
    private final DataZone data;
    private final int captureRefreshRate = 2;
    private final float boosterCapture = 1.5f;
    private int i = 0;

    public CaptureZone(Fireland _main, DataZone _data)
    {
        if (CaptureZone.main == null)
            CaptureZone.main = _main;
        this.data = _data;
    }

    public static void changeAnimationStep(int step, ZoneClass zone, String color)
    {
        if (step == -1)
        {
            for (int i = 0; i < 4; i++)
            {
                for (int j = 0; j < 4; j++)
                {
                    updateGlassWoolBlocks(zone, i, j, color);
                    updateGlassWoolBlocks(zone, -i, j, color);
                    updateGlassWoolBlocks(zone, -i, -j, color);
                    updateGlassWoolBlocks(zone, i, -j, color);
                }
            }
            updateBannerBlocks(zone, color);
        }
        else if (step == 1)
        {
            updateGlassWoolBlocks(zone, 1, 1, color);
            updateGlassWoolBlocks(zone, 2, 2, color);
        }
        else if (step == 2)
        {
            updateGlassWoolBlocks(zone, 0, 1, color);
            updateGlassWoolBlocks(zone, 0, 2, color);
            updateGlassWoolBlocks(zone, 1, 2, color);
            updateGlassWoolBlocks(zone, 1, 3, color);
            updateGlassWoolBlocks(zone, 2, 3, color);
        }
        else if (step == 3)
        {
            updateGlassWoolBlocks(zone, -1, 2, color);
            updateGlassWoolBlocks(zone, -2, 3, color);
            updateGlassWoolBlocks(zone, -1, 3, color);
            updateGlassWoolBlocks(zone, 0, 3, color);
        }
        else if (step == 4)
        {
            updateGlassWoolBlocks(zone, -1, 1, color);
            updateGlassWoolBlocks(zone, -2, 1, color);
            updateGlassWoolBlocks(zone, -2, 2, color);
            updateGlassWoolBlocks(zone, -3, 2, color);
        }
        else if (step == 5)
        {
            updateGlassWoolBlocks(zone, -3, 0, color);
            updateGlassWoolBlocks(zone, -3, 1, color);
            updateGlassWoolBlocks(zone, -2, 0, color);
            updateGlassWoolBlocks(zone, -1, 0, color);
            updateGlassWoolBlocks(zone, -3, -1, color);
        }
        else if (step == 6)
        {
            updateGlassWoolBlocks(zone, -1, -1, color);
            updateGlassWoolBlocks(zone, -2, -1, color);
            updateGlassWoolBlocks(zone, -2, -2, color);
            updateGlassWoolBlocks(zone, -3, -2, color);
        }
        else if (step == 7)
        {
            updateGlassWoolBlocks(zone, 0, -1, color);
            updateGlassWoolBlocks(zone, 0, -2, color);
            updateGlassWoolBlocks(zone, -1, -2, color);
            updateGlassWoolBlocks(zone, -1, -3, color);
            updateGlassWoolBlocks(zone, -2, -3, color);
        }
        else if (step == 8)
        {
            updateGlassWoolBlocks(zone, 1, -1, color);
            updateGlassWoolBlocks(zone, 1, -2, color);
            updateGlassWoolBlocks(zone, 1, -3, color);
            updateGlassWoolBlocks(zone, 0, -3, color);
            updateGlassWoolBlocks(zone, 2, -3, color);
            updateGlassWoolBlocks(zone, 2, -2, color);
        }
        else if (step == 9)
        {
            updateGlassWoolBlocks(zone, 1, 0, color);
            updateGlassWoolBlocks(zone, 2, 0, color);
            updateGlassWoolBlocks(zone, 2, -1, color);
            updateGlassWoolBlocks(zone, 3, -1, color);
            updateGlassWoolBlocks(zone, 3, -2, color);
        }
        else if (step == 10)
        {
            updateGlassWoolBlocks(zone, 3, 0, color);
            updateGlassWoolBlocks(zone, 2, 1, color);
            updateGlassWoolBlocks(zone, 3, 1, color);
            updateGlassWoolBlocks(zone, 3, 2, color);
        }
    }

    private static void updateGlassWoolBlocks(ZoneClass zone, int x, int z, String color)
    {
        World world = zone.getLocation().getWorld();
        Location glass = new Location(world, zone.getLocation().getX() + x, zone.getLocation().getY(), zone.getLocation().getZ() + z);
        Location wool = new Location(world, zone.getLocation().getX() + x, zone.getLocation().getY() - 1, zone.getLocation().getZ() + z);
        if (glass.getBlock().getType().toString().endsWith("_STAINED_GLASS") && wool.getBlock().getType().toString().endsWith("_WOOL"))
        {
            glass.getBlock().setType(BlockUtilities.getGlassBlockColor(color));
            wool.getBlock().setType(BlockUtilities.getWoolColor(color));
        }
    }

    private static void updateBannerBlocks(ZoneClass zone, String color)
    {
        World world = zone.getLocation().getWorld();
        Location banner1 = new Location(world, zone.getLocation().getX() + 1, zone.getLocation().getY() + 6, zone.getLocation().getZ() + 1);
        Location banner2 = new Location(world, zone.getLocation().getX() + 1, zone.getLocation().getY() + 6, zone.getLocation().getZ() - 1);
        Location banner3 = new Location(world, zone.getLocation().getX() - 1, zone.getLocation().getY() + 6, zone.getLocation().getZ() + 1);
        Location banner4 = new Location(world, zone.getLocation().getX() - 1, zone.getLocation().getY() + 6, zone.getLocation().getZ() - 1);
        List<Location> locations = new ArrayList<>();
        locations.add(banner1);
        locations.add(banner2);
        locations.add(banner3);
        locations.add(banner4);
        Material newMaterial = BlockUtilities.getBannerWallColor(color);

        if (banner1.getBlock().getType().toString().endsWith("_BANNER"))
        {
            for (Location loc : locations)
            {
                final BlockFace bf = ((Directional) loc.getBlock().getBlockData()).getFacing();
                loc.getBlock().setType(newMaterial);

                Directional directional = (Directional) loc.getBlock().getBlockData();

                directional.setFacing(bf);

                loc.getBlock().setBlockData(directional);
            }
        }
    }

    public void loop()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (data.getZones().isEmpty())
                {
                    return;
                }

                for (ZoneClass zone : data.getZones())
                {
                    if (data.getZoneInCapture().containsKey(zone.getName()) && !data.getZoneInCapture().get(zone.getName()).isEmpty()
                            && (!zone.isClaimed() || (zone.isClaimed() && zone.isClaimable())))
                    {
                        HashMap<FactionCapturingClass, Integer> capture = new HashMap<>();

                        Iterator<FactionCapturingClass> iterator = data.getZoneInCapture().get(zone.getName()).iterator();
                        while (iterator.hasNext())
                        {
                            FactionCapturingClass factionCapturing = iterator.next();
                            capture.put(factionCapturing, factionCapturing.getPlayerList().size());
                            if (factionCapturing.getPlayerList().isEmpty())
                            {
                                if (zone.getClaimer() == null || !zone.getClaimer().equalsIgnoreCase(factionCapturing.getName()))
                                {
                                    addProgressionTime(zone, factionCapturing, -2 * captureRefreshRate * boosterCapture);
                                    // Décapture automatique
                                }
                            }
                            if (factionCapturing.getProgression() <= 0 && factionCapturing.getPlayerList().isEmpty())
                            {
                                iterator.remove();
                            }
                        }
                        int max = 0;
                        FactionCapturingClass factionCapturing = null;
                        FactionCapturingClass factionToUncapture = null;
                        List<FactionCapturingClass> factionInContest = new ArrayList<>();
                        List<FactionCapturingClass> factionInMinority = new ArrayList<>();
                        for (FactionCapturingClass factionCapturingClass : capture.keySet())
                        {
                            if (!factionCapturingClass.getPlayerList().isEmpty())
                            {
                                if (capture.get(factionCapturingClass) > max)
                                {
                                    if (!factionInContest.isEmpty())
                                    {
                                        factionInMinority.addAll(factionInContest);
                                        factionInContest.clear();
                                    }
                                    if (factionCapturing != null)
                                    {
                                        factionInMinority.add(factionCapturing);
                                    }
                                    max = capture.get(factionCapturingClass);
                                    factionCapturing = factionCapturingClass;
                                    if (factionToUncapture != null && factionToUncapture == factionCapturing)
                                    {
                                        factionToUncapture = null;
                                    }
                                }
                                else if (capture.get(factionCapturingClass) == max)
                                {
                                    factionInContest.add(factionCapturingClass);
                                    factionInContest.add(factionCapturing);
                                    factionCapturing = null;
                                }
                                else
                                {
                                    factionInMinority.add(factionCapturingClass);
                                }
                                if (factionCapturingClass.getProgression() > 0 && factionCapturingClass != factionCapturing)
                                {
                                    factionToUncapture = factionCapturingClass;
                                }
                            }
                            else if (zone.isClaimed() && factionCapturingClass.getName().equalsIgnoreCase(zone.getClaimer())
                                    && zone.isClaimable())
                            {
                                if (factionToUncapture == null)
                                {
                                    factionToUncapture = factionCapturingClass;
                                }
                            }
                        }

                        if (factionCapturing != null)
                        {
                            if (factionToUncapture != null)
                            {
                                if (factionToUncapture.getName().equalsIgnoreCase(factionCapturing.getName()))
                                {
                                    addProgressionTime(zone, factionToUncapture, 2 * captureRefreshRate * boosterCapture);
                                    // Si tu proteges ta faction, ça capture
                                }
                                else
                                {
                                    addProgressionTime(zone, factionToUncapture, -2 * captureRefreshRate * boosterCapture);
                                    // Si tu décaptures une faction, ça décapture
                                }
                            }
                            else
                            {
                                addProgressionTime(zone, factionCapturing, boosterCapture * captureRefreshRate);
                                // Si t'es tout seul, tu captures
                            }
                        }
                        if (!factionInMinority.isEmpty())
                        {
                            for (FactionCapturingClass factionMonority : factionInMinority)
                            {
                                if (zone.getClaimer() == null
                                        || (zone.getClaimer() != null && !factionMonority.getName().equalsIgnoreCase(zone.getClaimer())))
                                {
                                    // Si t'es minoritaire, tu décaptures
                                    addProgressionTime(zone, factionCapturing, -2 * captureRefreshRate);
                                }
                            }
                        }
                    }
                }
                i++;
            }
        }.runTaskTimer(main, 20, 20 * captureRefreshRate);
    }

    private void addProgressionTime(ZoneClass zone, FactionCapturingClass faction, double seconds)
    {
        double prog = faction.getProgression();
        double nextProg = faction.getNextProgression((int) zone.getCaptureTime(), seconds);

        if (prog == 1 && nextProg < 1)
        {
            nextProg = 0;
        }

        FactionFunctions ff = new FactionFunctions(main, null);
        String color;
        if (seconds > 0)
        {
            color = ff.getFactionInfo(faction.getName()).getColorcode();
        }
        else
        {
            color = "§r";
        }

        zone.setColor(color);
        zone.setProgressBar(nextProg, faction.getFormattedName());

        playAnimation(zone, prog, nextProg, color);

        if (nextProg >= 100)
        {
            faction.setProgression(100);
        }
        else if (nextProg >= 90 && zone.getClaimer() != null && zone.getClaimer().equalsIgnoreCase(faction.getName()))
        {
            faction.setProgression(90);
        }
        else if (nextProg <= 0)
        {
            faction.setProgression(0);
        }
        else
        {
            faction.addProgression((int) zone.getCaptureTime(), seconds);
        }

        if (prog < nextProg)
        {
            data.setZoneEnterBool(zone.getName(), true);
            if ((int) nextProg % 10 == 0)
            {
                data.playSoundToPlayerZoneEnter(zone.getName(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1, 1);
            }
            else
            {
                data.playSoundToPlayerZoneEnter(zone.getName(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.AMBIENT, 1, 1);
            }
        }
        else if (data.getZoneEnterBool(zone.getName()))
        {
            data.setZoneEnterBool(zone.getName(), false);
            data.sendTextToPlayerZoneEnter(zone.getName(), "§cLa zone se décapture !");
        }

        if (prog <= 5 && nextProg > 5)
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                InGameUtilities.sendPlayerError(p, "La faction " + color + faction.getName() + "§R§c est en train de capturer la zone "
                        + zone.getFormattedName() + " ! Allez-y vite pour contester la capture !");
            }
        }
        if (prog >= 100)
        {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "summon firework_rocket " + (zone.getLocation().getX()) + " "
                    + (zone.getLocation().getY() + 2) + " " + (zone.getLocation().getZ() - 1)
                    + " {LifeTime:30,FireworksItem:{id:firework_rocket,Count:1,tag:{Fireworks:{Explosions:[{Type:1,Flicker:1,Trail:1,Colors:[I;11743532],FadeColors:[I;15435844]}],Flight:2}}}}");
            zone.removeAllBar();
            for (ZoneClass zoneClass : data.getZones())
            {
                if (zoneClass.getName().equalsIgnoreCase(zone.getName()))
                {
                    if (zone.isClaimed())
                    {
                        data.saveTiming(zone.getClaimer(), zone.getClaimedAt(), zone.getName());
                        zoneClass.unclaim();
                    }
                    changeAnimationStep(-1, zone, color);
                    zoneClass.setClaimed(faction.getName(), new Timestamp(System.currentTimeMillis()));
                    break;
                }
            }
            data.saveAll();
            for (Player p : Bukkit.getOnlinePlayers())
            {
                if (faction.getPlayerList().contains(p))
                {
                    addPlayerXp(p.getUniqueId(), 300, LevelStorage.Nation.Bannis);
                }
                InGameUtilities.playPlayerSound(p, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 1, 1);
                InGameUtilities.sendPlayerInformation(p,
                        "La faction " + color + faction.getName() + "§R§7 a capturé la zone " + zone.getFormattedName() + " !");
            }

            data.getZoneInCapture().get(zone.getName()).clear();
        }
        if (prog < 0)
        {
            if (faction.getName().equalsIgnoreCase(zone.getClaimer()))
            {
                changeAnimationStep(-1, zone, "§r");
                zone.unclaim();
                data.removeSavedClaiming(zone.getName(), null);
                data.saveTiming(faction.getName(), zone.getClaimedAt(), zone.getName());
            }
            data.getZoneInCapture().get(zone.getName()).remove(faction);
        }
    }

    private void playAnimation(ZoneClass zone, double from, double to, String color)
    {
        if ((from <= 0 && to >= 0) || (from > 0 && to <= 0))
        {
            changeAnimationStep(1, zone, color);
        }
        if ((from < 10 && to >= 10) || (from > 10 && to <= 10))
        {
            changeAnimationStep(2, zone, color);
        }
        if ((from < 20 && to >= 20) || (from > 20 && to <= 20))
        {
            changeAnimationStep(3, zone, color);
        }
        if ((from < 30 && to >= 30) || (from > 30 && to <= 30))
        {
            changeAnimationStep(4, zone, color);
        }
        if ((from < 40 && to >= 40) || (from > 40 && to <= 40))
        {
            changeAnimationStep(5, zone, color);
        }
        if ((from < 50 && to >= 50) || (from > 50 && to <= 50))
        {
            changeAnimationStep(6, zone, color);
        }
        if ((from < 60 && to >= 60) || (from > 60 && to <= 60))
        {
            changeAnimationStep(7, zone, color);
        }
        if ((from < 70 && to >= 70) || (from > 70 && to <= 70))
        {
            changeAnimationStep(8, zone, color);
        }
        if ((from < 80 && to >= 80) || (from > 80 && to <= 80))
        {
            changeAnimationStep(9, zone, color);
        }
        if ((from < 90 && to >= 90) || (from >= 90 && to <= 90))
        {
            changeAnimationStep(10, zone, color);
        }
        if ((from >= 100 && to >= 100) || (from > 100 && to <= 100))
        {
            changeAnimationStep(-1, zone, color);
        }
    }
}
