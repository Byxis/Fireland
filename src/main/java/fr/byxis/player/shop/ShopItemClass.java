package fr.byxis.player.shop;

import org.bukkit.Material;

public class ShopItemClass {

    private final String m_itemName;
    private final Material m_mat;
    private final short m_dura;
    private final String m_command;
    private final int m_price;
    private final int m_sell;
    private final int m_customModelData;
    private final boolean m_show;
    private final String m_currency;

    public ShopItemClass(String _itemName, Material _mat, short _dura, String _command, int _price, int _sell, int _customModelData, boolean _show, String _currency)
    {
        this.m_itemName = _itemName;
        this.m_mat = _mat;
        this.m_dura = _dura;
        this.m_command = _command;
        this.m_price = _price;
        this.m_sell = _sell;
        this.m_customModelData = _customModelData;
        this.m_show = _show;
        this.m_currency = _currency;
    }

    public String getItemName()
    {
        return m_itemName;
    }

    public Material getMat()
    {
        return m_mat;
    }

    public short getDura()
    {
        return m_dura;
    }

    public String getCommand()
    {
        return m_command;
    }

    public int getPrice()
    {
        return m_price;
    }

    public int getSell()
    {
        return m_sell;
    }

    public int getCustomModelData()
    {
        return m_customModelData;
    }

    public boolean isShow()
    {
        return m_show;
    }

    public String getCurrency() {
        return m_currency;
    }

}
