package fr.byxis.faction.bunker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.player.level.LevelStorage;
import fr.byxis.player.level.PlayerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Set;

import static fr.byxis.fireland.utilities.InGameUtilities.debugp;
import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class BunkerClass {

    private final String m_name;
    private Location m_location;
    private int m_level;
    private String m_skin;

    private final HashMap<Player, Location> m_playerInsideOldLocation;
    private final Multimap<Player, Player> m_invitations;
                  //Inviteur, invite
    private final Fireland m_main;

    private final int padding = 200;

    private final BunkerStorage m_storage;

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
            if (result.next())
            {
                m_location = getLocationFromNumber(result.getInt("number"));
                m_level = result.getInt(2);
                m_skin = result.getString(3);
            }
            else
            {
                create();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void upgrade()
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE faction_housing SET level = ? WHERE faction = ?");
            m_level += 1;
            preparedStatement.setInt(1, m_level);
            preparedStatement.setString(2, m_name);
            preparedStatement.executeUpdate();

            while (!m_playerInsideOldLocation.isEmpty())
            {
                leave((Player) m_playerInsideOldLocation.keySet().toArray()[0]);
            }
            resetClaimsFromUpgrade();
            if (!loadFileAndPaste(m_skin + m_level + ".schem"))
            {
                loadFileAndPaste("default " + m_level + ".schem");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void join(Player _p, boolean... silent)
    {
        if (silent.length > 0 && silent[0])
        {
            InGameUtilities.sendPlayerInformation(_p, "Vous avez rejoint le bunker de " + m_name);
            for (Player p : m_playerInsideOldLocation.keySet())
            {
                InGameUtilities.sendPlayerInformation(p, "Le joueur " + _p.getName() + " a rejoint le bunker");
            }
        }
        m_playerInsideOldLocation.put(_p, _p.getLocation());
        Location loc = m_location.clone();
        loc.add(-48.5, 14, -1.5);
        loc.setYaw(180);
        loc.setPitch(0);
        _p.teleport(loc);
    }

    void invite(Player _inviteur, Player _invite)
    {
        m_invitations.put(_inviteur, _invite);
    }

    public void leave(Player _p)
    {
        if (m_invitations.containsKey(_p))
        {
            for (Player p : m_invitations.get(_p))
            {
                InGameUtilities.sendPlayerError(p, "Vous avez quitté le bunker de " + m_name + " car la personne qui vous a invité est partie.");
                teleportBack(p, false);
            }
            m_invitations.removeAll(_p);
        }
        else if (isInvited(_p))
        {
            m_invitations.removeAll(_p);
        }
        teleportBack(_p, true);
        InGameUtilities.sendPlayerError(_p, "Vous avez quitté le bunker de " + m_name);
    }

    private void teleportBack(Player p, boolean doTpBackSpawn) {
        if (m_playerInsideOldLocation.containsKey(p))
        {
            debugp(6, "TpLoc");
            p.teleport(m_playerInsideOldLocation.get(p));
            m_playerInsideOldLocation.remove(p);
        }
        else if (doTpBackSpawn)
        {
            debugp(6, "TpBackSpawn");
            PlayerLevel pl = getPlayerLevel(p.getUniqueId());
            if (pl.getNation().equals(LevelStorage.Nation.Bannis))
                p.teleport(new Location(Bukkit.getWorld("world"), 341.5, 72, -209.5));
            else
                p.teleport(new Location(Bukkit.getWorld("world"), -448.5, 65, -448.5));
        }
        for (Player players : m_playerInsideOldLocation.keySet())
        {
            InGameUtilities.sendPlayerError(players, "Le joueur " + p.getName() + " a quitté le bunker");
        }
    }

    void create()
    {
        int number = 1;
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(number) FROM faction_housing;");
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                number = result.getInt(1) + 1;
            }
            final PreparedStatement insert = connection.prepareStatement("INSERT INTO faction_housing(faction, number, level, skin) VALUES(?,?,?,?);");
            insert.setString(1, m_name);
            insert.setInt(2, number);
            insert.setInt(3, 1);
            insert.setString(4, "default");
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        m_location = getLocationFromNumber(number);
        m_skin = "default";
        m_level = 1;
        loadFileAndPaste(m_skin + "1.schem");
    }

    public void destroy()
    {
        if (m_location != null)
        {
            loadFileAndPaste("emptyspace.schem");
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

    public boolean loadFileAndPaste(String fileName) {
        Path filePath = m_main.getDataFolder().toPath().resolve("bunker/" + fileName);
        if (Files.exists(filePath)) {
            ClipboardFormat format = ClipboardFormats.findByFile(filePath.toFile());
            try (InputStream inputStream = Files.newInputStream(filePath);
                ClipboardReader reader = format.getReader(inputStream))
            {
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
            }
            catch (IOException | WorldEditException e)
            {
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    /*
    boolean loadFileAndPaste2(String fileName)
    {
        File file = new File(m_main.getDataFolder(), "bunker/" + fileName);
        if (file.exists())
        {
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
    }*/

    Location getLocationFromNumber(int _number)
    {
        int n = (int) Math.ceil(Math.sqrt(_number)); // taille de la grille
        int p = n * n - _number; // position dans la grille
        int x, y;

        if (p < n) {
            x = p;
            y = n - 1;
        } else {
            x = n - 1;
            y = 2 * n - p - 2;
        }

        return new Location(Bukkit.getWorld("bunker"), x * 500, 0, y * 500);
    }

    public String getName()
    {
        return m_name;
    }

    public boolean isInvited(Player _p)
    {
        return m_invitations.containsValue(_p);
    }

    public int getAmeliorationPriceMoney()
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
    public int getAmeliorationPriceJetons()
    {
        return switch (m_level) {
            case 1, 2, 3, 4, 5, 6 -> 0;
            case 7 -> 250;
            case 8 -> 275;
            case 9 -> 300;
            case 10 -> 325;
            case 11 -> 350;
            case 12 -> 375;
            default -> -1;
        };
    }
    public int getAmeliorationFactionLevel()
    {
        return switch (m_level) {
            case 1, 2 -> 5;
            case 3 -> 6;
            case 4, 5 -> 7;
            default -> 8;
        };
    }

    public int getBunkerLevel()
    {
        return m_level;
    }

    public void resetClaimsFromUpgrade()
    {
        switch (m_level)
        {
            case 2 -> claimFood(null);
            case 5 ->
            {
                claimScrap(null);
                claimPowder(null);
                claimRepairKit(null);
            }
            case 7 ->
            {
                claimSerum(null);
                claimMeds(null);
                claimAntiDouleur(null);
            }
        }
    }

    public int getFoodAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT food_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int foodAmount = (int) ((getMaxFood() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 86400);
        if (foodAmount < 0)
        {
            foodAmount = 0;
        }
        return Math.min(foodAmount, getMaxFood());
    }

    public int getMaxFood()
    {
        return switch (m_level)
        {
            case 0, 1 -> 0;
            case 2 -> 32;
            default -> 64;
        };
    }

    public void claimFood(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getFoodAmount() + " steaks !");
                _p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, getFoodAmount()));
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

    public int getScrapAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT scrap_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((getMaxScrap() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 86400);
        if (itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, getMaxScrap());
    }

    public int getMaxScrap()
    {
        return switch (m_level)
        {
            case 0, 1, 2, 3, 4 -> 0;
            case 5 -> 32;
            default -> 64;
        };
    }

    public void claimScrap(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getScrapAmount() + " scraps !");
                _p.getInventory().addItem(new ItemStack(Material.NETHERITE_SCRAP, getScrapAmount()));
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

    public int getPowderAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT powder_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((getMaxPowder() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 86400);
        if (itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, getMaxPowder());
    }

    public int getMaxPowder()
    {
        return switch (m_level)
        {
            case 0, 1, 2, 3, 4 -> 0;
            case 5 -> 10;
            default -> 20;
        };
    }

    public void claimPowder(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getPowderAmount() + " poudre !");
                _p.getInventory().addItem(new ItemStack(Material.GUNPOWDER, getPowderAmount()));
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

    public int getRepairKitAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT repairkit_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((getMaxRepairKit() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 604800);
        if (itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, getMaxRepairKit());
    }

    public int getMaxRepairKit()
    {
        return switch (m_level)
        {
            case 0, 1, 2, 3, 4 -> 0;
            case 5 -> 1;
            default -> 3;
        };
    }

    public void claimRepairKit(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getRepairKitAmount() + " kit de réparations !");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wm give " + _p.getName() + " Kitreparation " + getRepairKitAmount());
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

    public int getMedsAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT meds_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((getMaxMeds() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 86400);
        if (itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, getMaxMeds());
    }

    public int getMaxMeds()
    {
        return switch (m_level)
        {
            case 0, 1, 2, 3, 4, 5, 6 -> 0;
            default -> 48;
        };
    }

    public void claimMeds(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getMedsAmount() + " médicaments !");
                _p.getInventory().addItem(new ItemStack(Material.HONEYCOMB, getMedsAmount()));
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

    public int getAntiDouleurAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT antidouleur_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((getMaxAntiDouleur() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 86400);
        if (itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, getMaxAntiDouleur());
    }

    public int getMaxAntiDouleur()
    {
        return switch (m_level)
        {
            case 0, 1, 2, 3, 4, 5, 6 -> 0;
            default -> 18;
        };
    }

    public void claimAntiDouleur(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getAntiDouleurAmount() + " Anti-Douleurs !");

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wm give " + _p.getName() + " Antidouleur " + getAntiDouleurAmount());
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

    public int getSerumAmount()
    {
        Timestamp claimed = new Timestamp(System.currentTimeMillis());
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            final Connection connection = firelandConnection.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT serum_claim FROM faction_housing WHERE faction = ?;");
            preparedStatement.setString(1, m_name);
            final ResultSet result = preparedStatement.executeQuery();
            if (result.next())
            {
                claimed = result.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int itemAmount = (int) ((getMaxSerum() * (new Timestamp(System.currentTimeMillis()).getTime() - claimed.getTime()) / 1000) / 604800);
        if (itemAmount < 0)
        {
            itemAmount = 0;
        }
        return Math.min(itemAmount, getMaxSerum());
    }

    public int getMaxSerum()
    {
        return switch (m_level)
        {
            case 0, 1, 2, 3, 4, 5, 6 -> 0;
            default -> 1;
        };
    }


    public void claimSerum(Player _p)
    {
        final DbConnection firelandConnection = m_main.getDatabaseManager().getFirelandConnection();
        try {
            if (_p != null)
            {
                InGameUtilities.sendPlayerSucces(_p, "Vous avez récupéré " + getSerumAmount() + " Sérum du Berserker !");

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wm give " + _p.getName() + " Serumberserker " + getSerumAmount());
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

    public BunkerStorage getStorage()
    {
        return m_storage;
    }

    public String getSkin()
    {
        return m_skin;
    }

    public void changeSkin(Player p, String _skin)
    {
        m_skin = _skin;
        while (!m_playerInsideOldLocation.isEmpty())
        {
            leave((Player) m_playerInsideOldLocation.keySet().toArray()[0]);
        }
        if (!loadFileAndPaste(m_skin + m_level + ".schem"))
        {
            InGameUtilities.sendPlayerError(p, "Le skin de bunker a été équipé. Cependant, il n'est pas disponible pour ce niveau. Le skin sera appliqué quand vous atteindrez le niveau requis");
        }
        else
        {
            InGameUtilities.sendPlayerSucces(p, "Le skin de bunker a été équipé et appliqué !");
        }
    }

    public int getPlayerInsideSize()
    {
        return m_playerInsideOldLocation.size();
    }

    public Set<Player> getPlayerInside()
    {
        return m_playerInsideOldLocation.keySet();
    }

    public Location getBunkerLocation()
    {
        return m_location;
    }


}
