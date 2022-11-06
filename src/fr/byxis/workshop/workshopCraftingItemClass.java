package fr.byxis.workshop;

import org.bukkit.Material;

import java.sql.Date;

public class workshopCraftingItemClass
{
    public String itemName;
    public String type;
    public Material mat;
    public short dura;
    public String command;
    public Date creationDate;
    public Date finishDate;


    public workshopCraftingItemClass(String _itemName, String _type, Material _mat, short _dura, String _command, Date _creationDate, Date _finishDate)
    {
        this.itemName = _itemName;
        this.type = _type;
        this.mat = _mat;
        this.dura = _dura;
        this.command = _command;
        this.creationDate = _creationDate;
        this.finishDate = _finishDate;
    }
}
