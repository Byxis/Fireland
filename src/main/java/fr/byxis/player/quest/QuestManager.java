package fr.byxis.player.quest;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.jeton.JetonManager;
import fr.byxis.player.quest.questclass.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class QuestManager {

    private static Fireland main = null;
    public static QuestConfig config = null;

    private static HashMap<Integer, QuestClass> availableQuests;
    private static HashMap<UUID, PlayerQuests> playerQuest;

    public QuestManager(Fireland main)
    {
        QuestManager.main = main;
        playerQuest = new HashMap<UUID, PlayerQuests>();
        config = (new QuestConfig(main));
        QuestSeek();
        Init();
        main.getServer().getPluginManager().registerEvents(new QuestEventHandler(main), main);
    }
    public static HashMap<UUID, PlayerQuests> getPlayerQuest()
    {
        return playerQuest;
    }

    public static HashMap<Integer, QuestClass> getAvailableQuests()
    {
        return availableQuests;
    }

    public static void fillQuests(Player p)
    {
        if (!playerQuest.containsKey(p.getUniqueId()))
        {
            playerQuest.put(p.getUniqueId(), new PlayerQuests(p.getUniqueId()));
        }
        if (main.cfgm.getPlayerDB().contains("quest." + p.getUniqueId()))
        {
            PlayerQuests pq = new PlayerQuests(p.getUniqueId());
            if (main.cfgm.getPlayerDB().contains("quest." + p.getUniqueId() + ".1"))
                pq.setFirst(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".1.id"), main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".1.progress")));
            if (main.cfgm.getPlayerDB().contains("quest." + p.getUniqueId() + ".2"))
                pq.setSecond(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".2.id"), main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".2.progress")));
            if (main.cfgm.getPlayerDB().contains("quest." + p.getUniqueId() + ".3"))
                pq.setThird(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".3.id"), main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".3.progress")));
            if (main.cfgm.getPlayerDB().contains("quest." + p.getUniqueId() + ".4"))
                pq.setFourth(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".4.id"), main.cfgm.getPlayerDB().getInt("quest." + p.getUniqueId() + ".4.progress")));
            if (main.cfgm.getPlayerDB().contains("quest." + p.getUniqueId() + ".claimed"))
                pq.setClaimed(main.cfgm.getPlayerDB().getBoolean("quest." + p.getUniqueId() + ".claimed"));
            playerQuest.replace(p.getUniqueId(), pq);
        }
        int i = 0;

        while (!playerQuest.get(p.getUniqueId()).isFull() && i < 100)
        {
            int random = BasicUtilities.generateInt(1, availableQuests.size() - 1);
            i++;
            if (config.getConfig().get("quest." + random + ".before") == null)
            {
                if (playerQuest.get(p.getUniqueId()).getFirst() == null || random != getOriginalQuest(playerQuest.get(p.getUniqueId()).getFirst().getId()))
                {
                    if (playerQuest.get(p.getUniqueId()).getSecond() == null || random != getOriginalQuest(playerQuest.get(p.getUniqueId()).getSecond().getId()))
                    {
                        if (playerQuest.get(p.getUniqueId()).getThird() == null || random != getOriginalQuest(playerQuest.get(p.getUniqueId()).getThird().getId()))
                        {
                            ProgressQuest pq = new ProgressQuest(random, 0);
                            playerQuest.get(p.getUniqueId()).set(pq);
                        }
                    }
                }
            }
        }
        if (i >= 100)
        {
            InGameUtilities.sendPlayerError(p, "Une erreur est survenue lors du chargement des quêtes.");
        }
    }

    public static boolean isNewDay()
    {
        Timestamp today = new Timestamp(System.currentTimeMillis());
        Timestamp date = Timestamp.valueOf(config.getConfig().getString("date"));
        return (date.getDate() != today.getDate()) /*&& today.getHours() >= 3*/;
    }

    public static void saveProgress()
    {
        if (playerQuest.isEmpty())
        {
            return;
        }
        for (PlayerQuests pq : playerQuest.values())
        {
            if (pq.getFirst() != null)
            {
                for (int i = 1; i < 5; i++)
                {
                    main.cfgm.getPlayerDB().set("quest." + pq.getUuid() + "." + i, null);
                    main.cfgm.getPlayerDB().set("quest." + pq.getUuid() + "." + i + ".id", pq.getQuest(i).getId());
                    main.cfgm.getPlayerDB().set("quest." + pq.getUuid() + "." + i + ".progress", pq.getQuest(i).getProgress());
                }
                main.cfgm.getPlayerDB().set("quest." + pq.getUuid() + ".claimed", pq.isClaimed());
            }
        }
        main.cfgm.savePlayerDB();
    }

    public static void actualiseKillProgress(Player p, String type)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof KillQuestClass quest)
            {
                if (quest.getKilled().equalsIgnoreCase(type) && !playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).addProgress(1);
                    if (quest.getAmount() <= playerQuest.get(p.getUniqueId()).getQuest(i).getProgress())
                    {
                        playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                    }
                }
            }
        }
    }

    private static int getOriginalQuest(int id)
    {
        while (config.getConfig().get("quest." + id + ".before") != null)
        {
            id = config.getConfig().getInt("quest." + id + ".before");
        }
        return id;
    }

    public static void actualiseRegionProgress(Player p, String region)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof EnterRegionQuestClass quest)
            {
                if (quest.getRegion().equalsIgnoreCase(region) && !playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                }
            }
        }
    }

    public static void actualiseInteractProgress(Player p, Material type)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof InteractQuestClass quest)
            {
                if (quest.getMaterial() == type && !playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).addProgress(1);
                    if (quest.getAmount() <= playerQuest.get(p.getUniqueId()).getQuest(i).getProgress())
                    {
                        playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                    }
                }
            }
        }
    }

    public static void actualiseInteractSpecificProgress(Player p, Location loc)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof InteractSpecificQuestClass quest)
            {
                if (quest.getLocation().getX() == loc.getX() && quest.getLocation().getY() == loc.getY() &&
                        quest.getLocation().getZ() == loc.getZ() && quest.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())
                        && !playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                }
            }
        }
    }

    public static void actualiseCraftProgress(Player p, int amount)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof CraftQuestClass quest)
            {
                if (!playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).addProgress(amount);
                    if (quest.getAmount() <= playerQuest.get(p.getUniqueId()).getQuest(i).getProgress())
                    {
                        playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                    }
                }
            }
        }
    }

    public static void actualiseBuyProgress(Player p, int amount)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof BuyQuestClass quest)
            {
                if (!playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).addProgress(amount);
                    if (quest.getAmount() <= playerQuest.get(p.getUniqueId()).getQuest(i).getProgress())
                    {
                        playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                    }
                }
            }
        }
    }

    public static void actualiseSellProgress(Player p, int amount)
    {
        for (int i = 1; i < 5; i++)
        {
            if (availableQuests.get(playerQuest.get(p.getUniqueId()).getQuest(i).getId()) instanceof SellQuestClass quest)
            {
                if (!playerQuest.get(p.getUniqueId()).isQuestFinished(i))
                {
                    playerQuest.get(p.getUniqueId()).getQuest(i).addProgress(amount);
                    if (quest.getAmount() <= playerQuest.get(p.getUniqueId()).getQuest(i).getProgress())
                    {
                        playerQuest.get(p.getUniqueId()).getQuest(i).finish(p);
                    }
                }
            }
        }
    }

    public static void claimRewards(Player p)
    {
        for (int i = 0; i < 5; i++)
        {
            if (playerQuest.get(p.getUniqueId()).getQuest(i).getProgress() != -1)
            {
                InGameUtilities.sendPlayerError(p, "Vous n'avez pas terminé les missions.");
                return;
            }
        }
        Fireland.eco.depositPlayer(p, 200);
        JetonManager.addJetonsPlayer(p.getUniqueId(), 1);
        InGameUtilities.sendPlayerSucces(p, "Vous avez récupéré les récompenses de missions.");
        playerQuest.get(p.getUniqueId()).setClaimed(true);
    }

    public void Init()
    {
        if (isNewDay())
        {
            config.getConfig().set("date", new Timestamp(System.currentTimeMillis()).toString());
            if (main.cfgm.getPlayerDB().getConfigurationSection("quest") == null)
            {
                return;
            }
            for (String uuid : main.cfgm.getPlayerDB().getConfigurationSection("quest").getKeys(false))
            {
                PlayerQuests pq = new PlayerQuests(UUID.fromString(uuid));
                if (config.getConfig().get("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id") + ".next") != null && main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.progression") == -1)
                {
                    pq.set(new ProgressQuest(config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id") + ".next"), 0));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".1.id", config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id") + ".next"));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".1.progress", 0);
                }
                else
                {
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".1", null);
                }
                if (config.getConfig().get("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".2.id") + ".next") != null && main.cfgm.getPlayerDB().getInt("quest." + uuid + ".2.progression") == -1)
                {
                    pq.set(new ProgressQuest(config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".2.id") + ".next"), 0));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".2.id", config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id") + ".next"));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".2.progress", 0);
                }
                else
                {
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".2", null);
                }

                if (config.getConfig().get("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".3.id") + ".next") != null && main.cfgm.getPlayerDB().getInt("quest." + uuid + ".3.progression") == -1)
                {
                    pq.set(new ProgressQuest(config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".3.id") + ".next"), 0));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".3.id", config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id") + ".next"));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".3.progress", 0);
                }
                else
                {
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".3", null);
                }
                if (config.getConfig().get("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".4.id") + ".next") != null && main.cfgm.getPlayerDB().getInt("quest." + uuid + ".4.progression") == -1)
                {
                    pq.set(new ProgressQuest(config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".4.id") + ".next"), 0));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".4.id", config.getConfig().getInt("quest." + main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id") + ".next"));
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".4.progress", 0);
                }
                else
                {
                    main.cfgm.getPlayerDB().set("quest." + uuid + ".claimed", false);
                }
                main.cfgm.getPlayerDB().set("quest." + uuid + ".4", null);
                playerQuest.put(UUID.fromString(uuid), pq);
            }
            config.save();
            main.cfgm.savePlayerDB();
        }
        else
        {
            if (main.cfgm.getPlayerDB().contains("quest."))
            {
                for (String uuid : main.cfgm.getPlayerDB().getConfigurationSection("quest").getKeys(false))
                {
                    PlayerQuests pq = new PlayerQuests(UUID.fromString(uuid));
                    pq.setFirst(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.id"), main.cfgm.getPlayerDB().getInt("quest." + uuid + ".1.progress")));
                    pq.setSecond(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + uuid + ".2.id"), main.cfgm.getPlayerDB().getInt("quest." + uuid + ".2.progress")));
                    pq.setThird(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + uuid + ".3.id"), main.cfgm.getPlayerDB().getInt("quest." + uuid + ".3.progress")));
                    pq.setFourth(new ProgressQuest(main.cfgm.getPlayerDB().getInt("quest." + uuid + ".4.id"), main.cfgm.getPlayerDB().getInt("quest." + uuid + ".4.progress")));
                    pq.setClaimed(main.cfgm.getPlayerDB().getBoolean("quest." + uuid + ".claimed"));
                    playerQuest.put(UUID.fromString(uuid), pq);
                }
            }

        }
        for (Player p : Bukkit.getOnlinePlayers())
        {
            fillQuests(p);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
    }

    private void QuestSeek()
    {
        availableQuests = new HashMap<Integer, QuestClass>();

        for (String i : config.getConfig().getConfigurationSection("quest").getKeys(false))
        {
            QuestClass quest = null;
            String objective = config.getConfig().getString("quest." + i + ".objective");
            String desc = config.getConfig().getString("quest." + i + ".desc");
            desc = desc.replaceAll("&","§");
            if (config.getConfig().contains("quest." + i + ".amount"))
            {
                desc = desc.replaceAll("<amount>", String.valueOf(config.getConfig().getInt("quest." + i + ".amount")));
            }
            if (config.getConfig().contains("quest." + i + ".location"))
            {
                desc = desc.replaceAll("<location.x>", String.valueOf(config.getConfig().getInt("quest." + i + ".location.x")));
                desc = desc.replaceAll("<location.y>", String.valueOf(config.getConfig().getInt("quest." + i + ".location.y")));
                desc = desc.replaceAll("<location.z>", String.valueOf(config.getConfig().getInt("quest." + i + ".location.z")));
            }
            if (objective.equalsIgnoreCase("KILL"))
            {
                quest = new KillQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc , config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        config.getConfig().getString("quest." + i + ".objective"),
                        config.getConfig().getString("quest." + i + ".type"),
                        config.getConfig().getInt("quest." + i + ".amount"));
            }
            else if (objective.equalsIgnoreCase("INTERACT_SPECIFIC"))
            {
                quest = new InteractSpecificQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc, config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        objective,
                        new Location(Bukkit.getWorld(config.getConfig().getString("quest." + i + ".location.world")),
                                config.getConfig().getInt("quest." + i + ".location.x"),
                                config.getConfig().getInt("quest." + i + ".location.y"),
                                config.getConfig().getInt("quest." + i + ".location.z")));
            }
            else if (objective.equalsIgnoreCase("ENTER_REGION"))
            {
                quest = new EnterRegionQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc, config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        objective, config.getConfig().getString("quest." + i + ".region"));
            }
            else if (config.getConfig().getString("quest." + i + ".objective").equalsIgnoreCase("INTERACT"))
            {
                quest = new InteractQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc, config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        config.getConfig().getString("quest." + i + ".objective"), Material.getMaterial(config.getConfig().getString("quest." + i + ".type")),
                        config.getConfig().getInt("quest." + i + ".amount"));
            }
            else if (config.getConfig().getString("quest." + i + ".objective").equalsIgnoreCase("CRAFT"))
            {
                quest = new CraftQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc, config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        config.getConfig().getString("quest." + i + ".objective"),
                        config.getConfig().getInt("quest." + i + ".amount"));
            }
            else if (config.getConfig().getString("quest." + i + ".objective").equalsIgnoreCase("BUY"))
            {
                quest = new BuyQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc, config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        config.getConfig().getString("quest." + i + ".objective"),
                        config.getConfig().getInt("quest." + i + ".amount"));
            }
            else if (config.getConfig().getString("quest." + i + ".objective").equalsIgnoreCase("SELL"))
            {
                quest = new SellQuestClass(Integer.parseInt(i), config.getConfig().getString("quest." + i + ".name"),
                        desc, config.getConfig().getInt("quest." + i + ".reward"),
                        config.getConfig().getInt("quest." + i + ".jetons"),
                        config.getConfig().getString("quest." + i + ".objective"),
                        config.getConfig().getInt("quest." + i + ".amount"));
            }
            if (quest != null)
                availableQuests.put(Integer.parseInt(i), quest);
        }
    }
}
