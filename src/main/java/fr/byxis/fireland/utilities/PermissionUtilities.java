package fr.byxis.fireland.utilities;

import fr.byxis.fireland.Fireland;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PermissionUtilities {

    private static Fireland main;

    public PermissionUtilities(Fireland main)
    {
        PermissionUtilities.main = main;
    }

    public static void addPermission(Player p, String permission) {
        LuckPerms api = LuckPermsProvider.get();

        User user = api.getPlayerAdapter(Player.class).getUser(p);
        // Add the permission
        user.data().add(Node.builder(permission).build());

        // Now we need to save changes.
        api.getUserManager().saveUser(user);
    }

    public static boolean hasPermission(Player p, String permission)
    {
        LuckPerms api = LuckPermsProvider.get();

        User user = api.getPlayerAdapter(Player.class).getUser(p);
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public static void addTempPermission(Player p, String permission, Date finished) {
        LuckPerms api = LuckPermsProvider.get();

        User user = api.getPlayerAdapter(Player.class).getUser(p);
        // Add the permission
        Date current = new Date(System.currentTimeMillis());
        long secondes = (finished.getTime()-current.getTime())/1000;
        user.data().add(Node.builder(permission).expiry(Duration.ofSeconds(secondes)).build());

        // Now we need to save changes.
        api.getUserManager().saveUser(user);
    }

    public static void removePermission(Player p, String permission) {
        if(hasPermission(p, permission))
        {
            LuckPerms api = LuckPermsProvider.get();

            User user = api.getPlayerAdapter(Player.class).getUser(p);
            // Add the permission
            user.data().remove(Node.builder(permission).build());

            // Now we need to save changes.
            api.getUserManager().saveUser(user);
        }
    }


    public static void commandExecutor(Player p, String cmd, String perm)
    {
        HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();

        PermissionAttachment attachment = p.addAttachment(main);
        perms.put(p.getUniqueId(), attachment);

        PermissionAttachment permissions = perms.get(p.getUniqueId());

        try
        {
            permissions.setPermission(perm, true);
            Bukkit.dispatchCommand(p, cmd);
        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            perms.get(p.getUniqueId()).unsetPermission(perm);
        }
    }

}
