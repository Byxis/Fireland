package fr.byxis.player.shop;

import org.bukkit.Material;

public class ShopItemClass {

    private final String itemName;
    private final Material mat;
    private final short dura;
    private final String command;
    private final int price;
    private final int sell;
    private final int customModelData;
    private final boolean show;

    public ShopItemClass(String _itemName, Material _mat, short _dura, String _command, int _price, int _sell, int _customModelData, boolean _show)
    {
        this.itemName = _itemName;
        this.mat = _mat;
        this.dura = _dura;
        this.command = _command;
        this.price = _price;
        this.sell = _sell;
        this.customModelData = _customModelData;
        this.show = _show;
    }

    public String getItemName()
    {
        return itemName;
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

    public int getPrice()
    {
        return price;
    }

    public int getSell()
    {
        return sell;
    }

    public int getCustomModelData()
    {
        return customModelData;
    }

    public boolean isShow()
    {
        return show;
    }

}
