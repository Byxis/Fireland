package fr.byxis.player.advancements;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AdvancementsClass {

    public final AdvancementsManager m_manager;
    public String m_id;
    private String m_title;
    private String m_desc;
    private AdvancementsManager.Success m_type;
    private Material m_material;
    private int m_money;
    private int m_jetons;
    private String m_permission;

    public AdvancementsClass(AdvancementsManager m_manager, String _id) {
        this.m_manager = m_manager;
        m_id = _id;

        m_title = m_manager.getConfig().getString("success." + m_id + ".title");
        m_desc = m_manager.getConfig().getString("success." + m_id + ".desc");
        m_type = AdvancementsManager.Success.valueOf(m_manager.getConfig().getString("success." + m_id + ".type"));
        m_material = Material.getMaterial(m_manager.getConfig().getString("success." + m_id + ".item"));
        m_money = m_manager.getConfig().getInt("success." + m_id + ".reward.money");
        m_jetons = m_manager.getConfig().getInt("success." + m_id + ".reward.jetons");
        m_permission = m_manager.getConfig().getString("success." + m_id + ".reward.permission");
    }

    public String getTitle() {
        return m_title;
    }

    public String getDesc() {
        return m_desc;
    }

    public AdvancementsManager.Success getType() {
        return m_type;
    }

    public Material getMaterial() {
        return m_material;
    }

    public int getMoney() {
        return m_money;
    }

    public int getJetons() {
        return m_jetons;
    }

    public String getPermission() {
        return m_permission;
    }

    public String getId() {
        return m_id;
    }

    public String getPath(Player p)
    {
        return "success." + p.getUniqueId() + "." + m_id;
    }

    public void ShowSuccess(Player p)
    {

    }
}

class KillAdvancements extends AdvancementsClass
{
    public MythicEntityType m_mobType;
    public int m_amount;
    public KillAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_mobType = MythicEntityType.get(_manager.getConfig().getString("success." + m_id + ".objective.type"));
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, MythicMob mob, int amount)
    {
        if (mob.getEntityType() == m_mobType)
        {
            int current = m_manager.getSuccessInt(getPath(p));
            if (current + amount >= m_amount)
            {
                m_manager.finishSuccess(getPath(p));
                ShowSuccess(p);
            }
            else
            {
                m_manager.addSuccessInt(getPath(p), amount);
            }
        }
    }
}

class PlayTimeAdvancements extends AdvancementsClass
{
    public int m_amount;
    public PlayTimeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        if (amount >= m_amount)
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
    }
}

class LearnRecipeAdvancements extends AdvancementsClass
{
    public int m_amount;
    public LearnRecipeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = m_manager.getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
        else
        {
            m_manager.addSuccessInt(getPath(p), amount);
        }
    }
}

class CraftAdvancements extends AdvancementsClass
{
    public int m_amount;
    public CraftAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = m_manager.getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
        else
        {
            m_manager.addSuccessInt(getPath(p), amount);
        }
    }
}

class CraftSuccessAdvancements extends AdvancementsClass
{
    public int m_amount;
    public CraftSuccessAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = m_manager.getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
        else
        {
            m_manager.addSuccessInt(getPath(p), amount);
        }
    }
}

class CraftBreakAdvancements extends AdvancementsClass
{
    public int m_amount;
    public CraftBreakAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = m_manager.getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
        else
        {
            m_manager.addSuccessInt(getPath(p), amount);
        }
    }
}

class GradeChangeAdvancements extends AdvancementsClass
{
    public String m_grade;
    public GradeChangeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_grade = _manager.getConfig().getString("success." + m_id + ".objective.grade");
    }

    public void updatePlayer(Player p, String grade)
    {
        if (grade.equalsIgnoreCase(m_grade))
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
    }
}

class RankChangeAdvancements extends AdvancementsClass
{
    public String m_rank;
    public RankChangeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_rank = _manager.getConfig().getString("success." + m_id + ".objective.rank");
    }

    public void updatePlayer(Player p, String rank)
    {
        if (rank.equalsIgnoreCase(m_rank))
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
    }
}

class SkinEquipAdvancements extends AdvancementsClass
{
    public String m_skin;
    public SkinEquipAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_skin = _manager.getConfig().getString("success." + m_id + ".objective.skin");
    }

    public void updatePlayer(Player p, String skin)
    {
        if (skin.equalsIgnoreCase(m_skin) || Objects.equals(m_skin, ""))
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
    }
}

class UseBoosterAdvancements extends AdvancementsClass
{
    public int m_duration;
    public int m_level;
    public int m_amount;
    public UseBoosterAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_duration = _manager.getConfig().getInt("success." + m_id + ".objective.duration");
        m_level = _manager.getConfig().getInt("success." + m_id + ".objective.level");
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int duration, int level, int amount)
    {
        if ((m_level == level && duration == -1) || (m_level == -1 && duration == m_duration) || (m_level == level && duration == m_duration))
        {
            int current = m_manager.getSuccessInt(getPath(p));
            if (current + amount >= m_amount)
            {
                m_manager.finishSuccess(getPath(p));
                ShowSuccess(p);
            }
            else
            {
                m_manager.addSuccessInt(getPath(p), amount);
            }
        }
    }
}

class PossessJetonsAdvancements extends AdvancementsClass
{
    public int m_amount;
    public PossessJetonsAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + m_id + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        if (amount >= m_amount)
        {
            m_manager.finishSuccess(getPath(p));
            ShowSuccess(p);
        }
    }
}
