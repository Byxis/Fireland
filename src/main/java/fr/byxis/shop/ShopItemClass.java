package fr.byxis.shop;

import org.bukkit.Material;

public class ShopItemClass {

    public String itemName;
    public Material mat;
    public short dura;
    public String command;
    public int price;
    public int sell;
    public int customModelData;
    public boolean show;

    public ShopItemClass(String _itemName, Material _mat, short _dura, String _command, int _price, int _sell, int customModelData, boolean show)
    {
        this.itemName = _itemName;
        this.mat = _mat;
        this.dura = _dura;
        this.command = _command;
        this.price = _price;
        this.sell = _sell;
        this.customModelData = customModelData;
        this.show = show;
    }

}
