package fr.byxis.player.advancements;

import com.google.common.collect.ArrayListMultimap;
import fr.byxis.fireland.Fireland;
import io.lumine.mythic.api.mobs.MythicMob;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AdvancementsManager {

    public enum Success
    {
        KILL,
        PLAY_TIME,
        LEARN_RECIPE,
        CRAFT,
        CRAFT_SUCCES,
        CRAFT_BREAK,
        GRADE_CHANGE,
        RANK_CHANGE,
        EQUIP_SKIN,
        USE_BOOSTER,
        POSSESS_JETONS,
        VOTE,
        STAY_NIGHT,
        STAY_BLOODY_NIGHT,
        ATTRACT_ENEMIES,
        FILL_BOTTLE,
        CREATE_FACTION,
        JOIN_FACTION,
        GIVE_MONEY_FACTION,
        REMOVE_MONEY_FACTION,
        CAPTURE_POINT,
        POSSESS_CAPTURE_POINTS,
        CHANGE_FACTION_COLOR,
        RANK_UP_FACTION,
        BUY_BUNKER,
        RANK_UP_BUNKER,
        OPEN_CHEST,
        SHOT_PLAYER,
        KILL_PLAYER,
        KILL_BANNI_WHILE_HERO,
        END_ESSAIM,
        ENTER_REGION,
        BUY,
        SELL,
        USE_PARACHUTE,
        USE_HELIPORT,
        USE,
        EQUIP,
        REFUSED_SF,
        RANK_UP_BANK,
        DIE,
        DIE_MONEY_LOOSE
    };

    private AdvancementsConfig m_config;
    private Fireland m_main;

    private ArrayListMultimap<Success, AdvancementsClass> m_success;

    private HashMap<String,Integer> m_changementList;

    public AdvancementsManager(Fireland _main)
    {
        m_main = _main;
        m_config = new AdvancementsConfig(_main);
        m_success = ArrayListMultimap.create();
        m_changementList = new HashMap<>();
        LoadSuccess();
    }

    public void LoadSuccess()
    {
        for (String success : m_config.getConfig().getConfigurationSection("success").getKeys(false))
        {
            Success type = Success.valueOf(m_config.getConfig().getString("success." + success + ".type"));
            switch(type)
            {
                case KILL -> m_success.put(type, new KillAdvancements(this, success));
                case PLAY_TIME -> m_success.put(type, new PlayTimeAdvancements(this, success));
                case LEARN_RECIPE -> m_success.put(type, new LearnRecipeAdvancements(this, success));
                case CRAFT -> m_success.put(type, new CraftAdvancements(this, success));
                case CRAFT_SUCCES -> m_success.put(type, new CraftSuccessAdvancements(this, success));
                case CRAFT_BREAK -> m_success.put(type, new CraftBreakAdvancements(this, success));
                case GRADE_CHANGE -> m_success.put(type, new GradeChangeAdvancements(this, success));
                case RANK_CHANGE -> m_success.put(type, new RankChangeAdvancements(this, success));
                case EQUIP_SKIN -> m_success.put(type, new SkinEquipAdvancements(this, success));
                case USE_BOOSTER -> m_success.put(type, new UseBoosterAdvancements(this, success));
                case POSSESS_JETONS -> m_success.put(type, new PossessJetonsAdvancements(this, success));
            }
        }
    }

    public void UpdateSucess(Success _sucess, Player p, Object... params)
    {
        switch(_sucess)
        {
            case KILL ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof KillAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (MythicMob) params[0], (Integer) params[1]);
                    }
                }
            }
            case PLAY_TIME ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof PlayTimeAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (Integer) params[0]);
                    }
                }
            }
            case LEARN_RECIPE ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof LearnRecipeAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (Integer) params[0]);
                    }
                }
            }
            case CRAFT ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof CraftAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (Integer) params[0]);
                    }
                }
            }
            case CRAFT_SUCCES ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof CraftSuccessAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (Integer) params[0]);
                    }
                }
            }
            case CRAFT_BREAK ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof CraftBreakAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (Integer) params[0]);
                    }
                }
            }
            case GRADE_CHANGE ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof GradeChangeAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (String) params[0]);
                    }
                }
            }
            case RANK_CHANGE ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof RankChangeAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (String) params[0]);
                    }
                }
            }
            case EQUIP_SKIN ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof SkinEquipAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (String) params[0]);
                    }
                }
            }
            case USE_BOOSTER ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof UseBoosterAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (int) params[0], (int) params[1], (int) params[2]);
                    }
                }
            }
            case POSSESS_JETONS ->
            {
                for (AdvancementsClass advancement : m_success.get(Success.KILL))
                {
                    if (advancement instanceof PossessJetonsAdvancements success && !isSuccessFinished(advancement, p))
                    {
                        success.updatePlayer(p, (int) params[0]);
                    }
                }
            }
        }
    }

    public int getSuccessInt(String path)
    {
        if (m_changementList.containsKey(path))
        {
            return m_changementList.get(path);
        }
        return m_main.cfgm.getPlayerDB().getInt(path);
    }

    public void addSuccessInt(String path, int value)
    {
        m_changementList.put(path, getSuccessInt(path) + value);
    }

    public void setSuccessInt(String path, int value)
    {
        if (m_changementList.containsKey(path))
        {
            m_changementList.replace(path, value);
        }
        else
        {
            m_changementList.put(path, value);
        }
    }

    public void finishSuccess(String path)
    {
        setSuccessInt(path, -1);
    }

    public FileConfiguration getConfig()
    {
        return m_config.getConfig();
    }

    public boolean isSuccessFinished(AdvancementsClass adv, Player p)
    {
        return getSuccessInt(adv.getPath(p)) == -1;
    }

}
