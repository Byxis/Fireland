package fr.byxis.player.workshop;

import org.bukkit.Material;

public class WorkshopItemClass
{
    private String recipeName;
    private String itemName;
    private String type;
    private int scrap;
    private int gunPowder;
    private int customModelData;
    private Material mat;
    private short durability;
    private String command;
    private boolean know;


    public WorkshopItemClass(String _recipeName, String _itemName, String _type, int _scrap, int _gunpowder, Material _mat, short _durability, String _command, boolean _know, int customModelData)
    {
        this.recipeName = _recipeName;
        this.itemName = _itemName;
        this.scrap = _scrap;
        this.type = _type;
        this.gunPowder = _gunpowder;
        this.mat = _mat;
        this.durability = _durability;
        this.command = _command;
        this.know = _know;
        this.customModelData = customModelData;
    }


    public String getRecipeName()
    {
        return recipeName;
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getType()
    {
        return type;
    }

    public int getScrap()
    {
        return scrap;
    }

    public int getGunPowder()
    {
        return gunPowder;
    }

    public int getCustomModelData()
    {
        return customModelData;
    }

    public Material getMat()
    {
        return mat;
    }

    public short getDurability()
    {
        return durability;
    }

    public String getCommand()
    {
        return command;
    }

    public boolean isKnown()
    {
        return know;
    }
}
