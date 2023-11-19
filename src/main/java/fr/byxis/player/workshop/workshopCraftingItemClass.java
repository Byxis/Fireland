package fr.byxis.player.workshop;

import org.bukkit.Material;

import java.sql.Timestamp;

public class workshopCraftingItemClass
{
    public String itemName;
    public String type;
    public Material mat;
    public short dura;
    public String command;
    public Timestamp creationDate;
    public Timestamp finishDate;
    public int customModelData;
    public String planName;


    public workshopCraftingItemClass(String _itemName, String _type, Material _mat, short _dura, String _command, Timestamp _creationDate, Timestamp _finishDate, int customModelData, String planName)
    {
        this.itemName = _itemName;
        this.planName = planName;
        this.type = _type;
        this.mat = _mat;
        this.dura = _dura;
        this.command = _command;
        this.creationDate = _creationDate;
        this.finishDate = _finishDate;
        this.customModelData = customModelData;
    }
}
