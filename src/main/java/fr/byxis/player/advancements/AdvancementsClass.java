package fr.byxis.player.advancements;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AdvancementsClass {

    private final AdvancementsManager m_manager;
    private final String m_id;
    private final String m_title;
    private final String m_desc;
    private final AdvancementsManager.Success m_type;
    private final Material m_material;
    private final int m_money;
    private final int m_jetons;
    private final String m_permission;

    public AdvancementsClass(AdvancementsManager _manager, String _id) {
        this.m_manager = _manager;
        m_id = _id;

        m_title = _manager.getConfig().getString("success." + m_id + ".title");
        m_desc = _manager.getConfig().getString("success." + m_id + ".desc");
        m_type = AdvancementsManager.Success.valueOf(_manager.getConfig().getString("success." + m_id + ".type"));
        m_material = Material.getMaterial(_manager.getConfig().getString("success." + m_id + ".item"));
        m_money = _manager.getConfig().getInt("success." + m_id + ".reward.money");
        m_jetons = _manager.getConfig().getInt("success." + m_id + ".reward.jetons");
        m_permission = _manager.getConfig().getString("success." + m_id + ".reward.permission");
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

    public void showSuccess(Player p)
    {

    }

    public AdvancementsManager getManager()
    {
        return m_manager;
    }
}

class KillAdvancements extends AdvancementsClass
{
    private final MythicEntityType m_mobType;
    private final int m_amount;
    public KillAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_mobType = MythicEntityType.get(_manager.getConfig().getString("success." + getId() + ".objective.type"));
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, MythicMob mob, int amount)
    {
        if (mob.getEntityType() == m_mobType)
        {
            int current = getManager().getSuccessInt(getPath(p));
            if (current + amount >= m_amount)
            {
                getManager().finishSuccess(getPath(p));
                showSuccess(p);
            }
            else
            {
                getManager().addSuccessInt(getPath(p), amount);
            }
        }
    }
}

class PlayTimeAdvancements extends AdvancementsClass
{
    private final int m_amount;
    public PlayTimeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        if (amount >= m_amount)
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
    }
}

class LearnRecipeAdvancements extends AdvancementsClass
{
    private final int m_amount;
    public LearnRecipeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = getManager().getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
        else
        {
            getManager().addSuccessInt(getPath(p), amount);
        }
    }
}

class CraftAdvancements extends AdvancementsClass
{
    private final int m_amount;
    public CraftAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = getManager().getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
        else
        {
            getManager().addSuccessInt(getPath(p), amount);
        }
    }
}

class CraftSuccessAdvancements extends AdvancementsClass
{
    private final int m_amount;
    public CraftSuccessAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = getManager().getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
        else
        {
            getManager().addSuccessInt(getPath(p), amount);
        }
    }
}

class CraftBreakAdvancements extends AdvancementsClass
{
    private final int m_amount;
    public CraftBreakAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        int current = getManager().getSuccessInt(getPath(p));
        if (current + amount >= m_amount)
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
        else
        {
            getManager().addSuccessInt(getPath(p), amount);
        }
    }
}

class GradeChangeAdvancements extends AdvancementsClass
{
    private final String m_grade;
    public GradeChangeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_grade = _manager.getConfig().getString("success." + getId() + ".objective.grade");
    }

    public void updatePlayer(Player p, String grade)
    {
        if (grade.equalsIgnoreCase(m_grade))
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
    }
}

class RankChangeAdvancements extends AdvancementsClass
{
    private final String m_rank;
    public RankChangeAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_rank = _manager.getConfig().getString("success." + getId() + ".objective.rank");
    }

    public void updatePlayer(Player p, String rank)
    {
        if (rank.equalsIgnoreCase(m_rank))
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
    }
}

class SkinEquipAdvancements extends AdvancementsClass
{
    private final String m_skin;
    public SkinEquipAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_skin = _manager.getConfig().getString("success." + getId() + ".objective.skin");
    }

    public void updatePlayer(Player p, String skin)
    {
        if (skin.equalsIgnoreCase(m_skin) || Objects.equals(m_skin, ""))
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
    }
}

class UseBoosterAdvancements extends AdvancementsClass
{
    private final int m_duration;
    private final int m_level;
    private final int m_amount;
    public UseBoosterAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_duration = _manager.getConfig().getInt("success." + getId() + ".objective.duration");
        m_level = _manager.getConfig().getInt("success." + getId() + ".objective.level");
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int duration, int level, int amount)
    {
        if ((m_level == level && duration == -1) || (m_level == -1 && duration == m_duration) || (m_level == level && duration == m_duration))
        {
            int current = getManager().getSuccessInt(getPath(p));
            if (current + amount >= m_amount)
            {
                getManager().finishSuccess(getPath(p));
                showSuccess(p);
            }
            else
            {
                getManager().addSuccessInt(getPath(p), amount);
            }
        }
    }
}

class PossessJetonsAdvancements extends AdvancementsClass
{
    private final int m_amount;
    public PossessJetonsAdvancements(AdvancementsManager _manager, String _id) {
        super(_manager, _id);
        m_amount = _manager.getConfig().getInt("success." + getId() + ".objective.amount");
    }

    public void updatePlayer(Player p, int amount)
    {
        if (amount >= m_amount)
        {
            getManager().finishSuccess(getPath(p));
            showSuccess(p);
        }
    }
}
