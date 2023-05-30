package fr.byxis.fireland.utilities;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Date;

public class PermissionUtilities {

    public static void addPermission(Player p, String permission) {
        LuckPerms api = LuckPermsProvider.get();

        User user = api.getPlayerAdapter(Player.class).getUser(p);
        // Add the permission
        user.data().add(Node.builder(permission).build());

        // Now we need to save changes.
        api.getUserManager().saveUser(user);
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
        LuckPerms api = LuckPermsProvider.get();

        User user = api.getPlayerAdapter(Player.class).getUser(p);
        // Add the permission
        user.data().remove(Node.builder(permission).build());

        // Now we need to save changes.
        api.getUserManager().saveUser(user);
    }

    public void playSound(Player p, String sound)
    {
        p.playSound(p.getLocation(), sound, 1, 1);
    }



}
