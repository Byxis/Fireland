package fr.byxis.player.workshop;

import org.bukkit.Material;

public class WorkshopItemClass
{
    private final String recipeName;
    private final String itemName;
    private final String type;
    private final int scrap;
    private final int gunPowder;
    private final int medicine;
    private final int duration;
    private final int customModelData;
    private final Material mat;
    private final short durability;
    private final String command;
    private final boolean know;

    public WorkshopItemClass(String _recipeName, String _itemName, String _type, int _scrap, int _gunpowder, int _medicine, int _duration,
            Material _mat, short _durability, String _command, boolean _know, int _customModelData)
    {
        this.recipeName = _recipeName;
        this.itemName = _itemName;
        this.scrap = _scrap;
        this.type = _type;
        this.gunPowder = _gunpowder;
        this.medicine = _medicine;
        this.duration = _duration;
        this.mat = _mat;
        this.durability = _durability;
        this.command = _command;
        this.know = _know;
        this.customModelData = _customModelData;
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

    public int getMedicine()
    {
        return medicine;
    }

    public int getDuration()
    {
        return duration;
    }

    @Override
    public String toString()
    {
        return "WorkshopItemClass {" + "\n  recipeName: '" + recipeName + '\'' + ",\n  itemName: '" + itemName + '\'' + ",\n  type: '"
                + type + '\'' + ",\n  scrap: " + scrap + ",\n  gunPowder: " + gunPowder + ",\n  medicine: " + medicine + ",\n  duration: "
                + duration + ",\n  customModelData: " + customModelData + ",\n  mat: " + (mat != null ? mat.name() : "null")
                + ",\n  durability: " + durability + ",\n  command: '" + command + '\'' + ",\n  know: " + know + "\n}";
    }

}
