package fr.byxis.player.workshop;

import java.sql.Timestamp;
import org.bukkit.Material;

public class WorkshopCraftingItemClass
{

    private final String itemName;
    private final String type;
    private final Material mat;
    private final short dura;
    private final String command;
    private final Timestamp creationDate;
    private final Timestamp finishDate;
    private final int customModelData;
    private final String planName;

    public WorkshopCraftingItemClass(String _itemName, String _type, Material _mat, short _dura, String _command, Timestamp _creationDate,
            Timestamp _finishDate, int _customModelData, String _planName)
    {
        this.itemName = _itemName;
        this.planName = _planName;
        this.type = _type;
        this.mat = _mat;
        this.dura = _dura;
        this.command = _command;
        this.creationDate = _creationDate;
        this.finishDate = _finishDate;
        this.customModelData = _customModelData;
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getType()
    {
        return type;
    }

    public Material getMat()
    {
        return mat;
    }

    public short getDura()
    {
        return dura;
    }

    public String getCommand()
    {
        return command;
    }

    public Timestamp getCreationDate()
    {
        return creationDate;
    }

    public Timestamp getFinishDate()
    {
        return finishDate;
    }

    public int getCustomModelData()
    {
        return customModelData;
    }

    public String getPlanName()
    {
        return planName;
    }
}
