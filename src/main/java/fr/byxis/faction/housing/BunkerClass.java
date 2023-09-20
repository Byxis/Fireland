package fr.byxis.faction.housing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BunkerClass {

    private static String m_name;
    private Location m_location;
    private int m_level;
    private String m_skin;

    public HashMap<Player, Location> m_playerInsideOldLocation;
    private Multimap<Player, Player> m_invitations;
                  //Inviteur, invite
    private Fireland m_main;

    private int padding = 200;

    private BunkerStorage m_storage;

    public BunkerClass(String _name, Fireland _main)
    {
        m_name = _name;
        m_main = _main;
        m_playerInsideOldLocation = new HashMap<>();
        m_invitations = ArrayListMultimap.create();
        m_storage = new BunkerStorage(_main, _name);
        m_location = null;


        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT number, level, skin FROM faction_housing WHERE faction = ?");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                m_location = GetLocationFromNumber(result.getInt("number"));
                m_level = result.getInt(2);
                m_skin = result.getString(3);
            }
            else
            {
                Create();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void Upgrade(Fireland _main)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET level = ? WHERE faction = ?");
            m_level+=1;
            preparedStatement.setInt(1, m_level);
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();

            while(!m_playerInsideOldLocation.isEmpty())
            {
                Leave((Player) m_playerInsideOldLocation.keySet().toArray()[0]);
            }
            ResetClaimsFromUpgrade();
            if(!LoadFileAndPaste(m_skin+m_level+".schem"))
            {
                LoadFileAndPaste("default"+m_level+".schem");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Join(Player _p, boolean... silent)
    {
        if(silent.length > 0 && silent[0])
        {
            InGameUtilities.sendPlayerInformation(_p,"Vous avez rejoint le bunker de "+m_name);
            for(Player p : m_playerInsideOldLocation.keySet())
            {
                InGameUtilities.sendPlayerInformation(p, "Le joueur "+_p.getName()+" a rejoint le bunker");
            }
        }
        m_playerInsideOldLocation.put(_p, _p.getLocation());
        Location loc =m_location.clone();
        loc.add(-48.5, 14, -1.5);
        loc.setYaw(180);
        loc.setPitch(0);
        _p.teleport(loc);
    }

    void Invite(Player _inviteur, Player _invite)
    {
        m_invitations.put(_inviteur, _invite);
    }

    public void Leave(Player _p)
    {
        if(m_invitations.containsKey(_p))
        {
            for(Player p : m_invitations.get(_p))
            {
                InGameUtilities.sendPlayerError(p,"Vous avez quitté le bunker de "+m_name+" car la personne qui vous a invité est partie.");
                p.teleport(m_playerInsideOldLocation.get(p));
                m_playerInsideOldLocation.remove(p);
                for(Player players : m_playerInsideOldLocation.keySet())
                {
                    InGameUtilities.sendPlayerError(p, "Le joueur "+p.getName()+" a quitté le bunker");
                }
            }
            m_invitations.removeAll(_p);
            return;
        }
        else if(IsInvited(_p))
        {
            m_invitations.removeAll(_p);
        }
        _p.teleport(m_playerInsideOldLocation.get(_p));
        m_playerInsideOldLocation.remove(_p);
        for(Player p : m_playerInsideOldLocation.keySet())
        {
            InGameUtilities.sendPlayerError(p, "Le joueur "+_p.getName()+" a quitté le bunker");
        }
        InGameUtilities.sendPlayerError(_p,"Vous avez quitté le bunker de "+m_name);
    }

    void Create()
    {
        int number = 1;
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(number) FROM faction_housing;");
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                number = result.getInt(1)+1;
            }
            final PreparedStatement insert = connection.prepareStatement("INSERT INTO faction_housing(faction, number, level, skin) VALUES(?,?,?,?);");
            insert.setString(1, m_name);
            insert.setInt(2, number);
            insert.setInt(3,1);
            insert.setString(4, "default");
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        m_location = GetLocationFromNumber(number);
        m_skin = "default";
        m_level = 1;
        LoadFileAndPaste(m_skin+"1.schem");
    }

    public void Destroy()
    {
        if(m_location != null)
        {
            LoadFileAndPaste("emptyspace.schem");
        }
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement removeBk = connection.prepareStatement("DELETE FROM faction_housing WHERE faction = ?");
            removeBk.setString(1, m_name);
            removeBk.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean LoadFileAndPaste(String fileName)
    {
        File file = new File(m_main.getDataFolder(), "bunker/"+fileName);
        if(file.exists()) {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                Clipboard clipboard = reader.read();

                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(m_location.getWorld()), -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(m_location.getX(), m_location.getY(), m_location.getZ()))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                    return true;
                }
            } catch (IOException | WorldEditException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    Location GetLocationFromNumber(int _number)
    {
        if(_number <= 1)
        {
            return new Location(Bukkit.getWorld("bunker"), 0, 0, 0);
        }
        else {
            int number = 1;
            int x = padding;
            int maxX = padding;
            int z = 0;
            int maxZ = padding;
            while(number < _number)
            {
                if(x >= maxX)
                {
                    x = 0;
                    z += padding;
                }
                if(z > maxZ)
                {
                    x = maxX + padding;
                    maxX = x;
                    maxZ = maxX;
                    z = 0;
                }
                else {
                    x += padding;
                }
                number++;
            }
            return new Location(Bukkit.getWorld("bunker"), x, 0, z);
        }
    }

    public String GetName()
    {
        return m_name;
    }

    public boolean IsInvited(Player _p)
    {
        return m_invitations.containsValue(_p);
    }

    public int GetAmeliorationPriceMoney()
    {
        return switch (m_level) {
            case 8 -> 10000;
            case 9 -> 15000;
            case 1, 10 -> 20000;
            case 2, 11 -> 25000;
            case 3, 12 -> 30000;
            case 4 -> 35000;
            case 5 -> 40000;
            case 6 -> 45000;
            case 7 -> 50000;
            default -> -1;
        };
    }
    public int GetAmeliorationPriceJetons()
    {
        return switch (m_level) {
            case 1,2,3,4,5,6 -> 0;
            case 7 -> 800;
            case 8, 9, 10, 11, 12 -> 1000;
            default -> -1;
        };
    }
    public int GetAmeliorationFactionLevel()
    {
        return switch (m_level) {
            case 1, 2 -> 5;
            case 3 -> 6;
            case 4, 5 -> 7;
            default -> 8;
        };
    }

    public int GetBunkerLevel()
    {
        return m_level;
    }

    public void ResetClaimsFromUpgrade()
    {
        switch (m_level)
        {
            case 2 -> ClaimFood(null);
            case 5 ->
            {
                ClaimScrap(null);
                ClaimPowder(null);
                ClaimRepairKit(null);
            }
            case 7 ->
            {
                ClaimSerum(null);
                ClaimMeds(null);
                ClaimAntiDouleur(null);
            }
        }
    }

    public int GetFoodAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT food_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int foodAmount = (int) (( GetMaxFood()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/86400);
        if(foodAmount < 0)
        {
            foodAmount = 0;
        }
        return Math.min(foodAmount, GetMaxFood());
    }

    public int GetMaxFood()
    {
        return switch (m_level)
            {
                case 0,1 -> 0;
                case 2 -> 10;
                default -> 32;
            };
    }

    public void ClaimFood(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetFoodAmount()+" steaks !");
                _p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, GetFoodAmount()));
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET food_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int GetScrapAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT scrap_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) (( GetMaxScrap()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/86400);
        if(itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, GetMaxScrap());
    }

    public int GetMaxScrap()
    {
        return switch (m_level)
        {
            case 0,1,2,3,4 -> 0;
            case 5 -> 24;
            default -> 48;
        };
    }

    public void ClaimScrap(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetScrapAmount()+" scraps !");
                _p.getInventory().addItem(new ItemStack(Material.NETHERITE_SCRAP, GetScrapAmount()));
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET scrap_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int GetPowderAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT powder_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((GetMaxPowder()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/86400);
        if(itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, GetMaxPowder());
    }

    public int GetMaxPowder()
    {
        return switch (m_level)
        {
            case 0,1,2,3,4 -> 0;
            case 5 -> 5;
            default -> 15;
        };
    }

    public void ClaimPowder(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetPowderAmount()+" poudre !");
                _p.getInventory().addItem(new ItemStack(Material.GUNPOWDER, GetPowderAmount()));
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET powder_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetRepairKitAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT repairkit_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((GetMaxRepairKit()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/604800);
        if(itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, GetMaxRepairKit());
    }

    public int GetMaxRepairKit()
    {
        return switch (m_level)
        {
            case 0,1,2,3,4 -> 0;
            case 5 -> 1;
            default -> 3;
        };
    }

    public void ClaimRepairKit(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetRepairKitAmount()+" kit de réparations !");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wm give "+_p.getName()+" Kitreparation "+GetRepairKitAmount());
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET repairkit_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetMedsAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT meds_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((GetMaxMeds()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/86400);
        if(itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, GetMaxMeds());
    }

    public int GetMaxMeds()
    {
        return switch (m_level)
        {
            case 0,1,2,3,4,5,6 -> 0;
            default -> 48;
        };
    }

    public void ClaimMeds(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetMedsAmount()+" médicaments !");
                _p.getInventory().addItem(new ItemStack(Material.HONEYCOMB, GetMedsAmount()));
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET meds_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetAntiDouleurAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT antidouleur_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((GetMaxAntiDouleur()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/86400);
        if(itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, GetMaxAntiDouleur());
    }

    public int GetMaxAntiDouleur()
    {
        return switch (m_level)
        {
            case 0,1,2,3,4,5,6 -> 0;
            default -> 6;
        };
    }

    public void ClaimAntiDouleur(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetAntiDouleurAmount()+" Anti-Douleurs !");

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wm give "+_p.getName()+" Antidouleur "+GetAntiDouleurAmount());
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET antidouleur_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetSerumAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT serum_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if(result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((GetMaxSerum()*(new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000)/604800);
        if(itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, GetMaxSerum());
    }

    public int GetMaxSerum()
    {
        return switch (m_level)
        {
            case 0,1,2,3,4,5,6 -> 0;
            default -> 1;
        };
    }


    public void ClaimSerum(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if(_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré "+GetSerumAmount()+" Sérum du Berserker !");

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wm give "+_p.getName()+" Serumberserker "+GetSerumAmount());
            }
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET serum_claim = ? WHERE faction = ?");
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BunkerStorage GetStorage()
    {
        return m_storage;
    }

    public String GetSkin()
    {
        return m_skin;
    }

    public void ChangeSkin(Player p, String _skin)
    {
        m_skin = _skin;
        while(!m_playerInsideOldLocation.isEmpty())
        {
            Leave((Player) m_playerInsideOldLocation.keySet().toArray()[0]);
        }
        if(!LoadFileAndPaste(m_skin+m_level+".schem"))
        {
            InGameUtilities.sendPlayerError(p, "Le skin de bunker a été équipé. Cependant, il n'est pas disponible pour ce niveau. Le skin sera appliqué quand vous atteindrez le niveau requis");
        }
        else
        {
            InGameUtilities.sendPlayerSucces(p, "Le skin de bunker a été équipé et appliqué !");
        }
    }

}
