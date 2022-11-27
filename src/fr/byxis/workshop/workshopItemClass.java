package fr.byxis.workshop;

import org.bukkit.Material;

public class workshopItemClass
{
    public String recipeName;
    public String itemName;
    public String type;
    public int scrap;
    public int gunPowder;
    public int customModelData;
    public Material mat;
    public short dura;
    public String command;
    public boolean know;



    public workshopItemClass(String _recipeName, String _itemName, String _type, int _scrap, int _gunpowder, Material _mat, short _dura, String _command, boolean _know, int customModelData)
    {
        this.recipeName = _recipeName;
        this.itemName = _itemName;
        this.scrap = _scrap;
        this.type = _type;
        this.gunPowder = _gunpowder;
        this.mat = _mat;
        this.dura = _dura;
        this.command = _command;
        this.know = _know;
        this.customModelData = customModelData;
    }


}
