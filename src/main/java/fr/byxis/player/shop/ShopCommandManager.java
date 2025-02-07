package fr.byxis.player.shop;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class ShopCommandManager implements CommandExecutor, TabCompleter {

    private final Fireland main;

    public ShopCommandManager(Fireland _main)
    {
        this.main = _main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String arg, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (cmd.getName().equalsIgnoreCase("shop") && args.length == 1) {
                ShopFunction sf = new ShopFunction(main, player);
                String name = "";
                ArrayList<String> l = sf.getAllShop();
                for (String str : l) {
                    if (str.equalsIgnoreCase(args[0])) {
                        name = str;
                        break;
                    }
                }
                if (name.equalsIgnoreCase("")) {
                    player.sendMessage("§cCe shop n'existe pas.");
                    return true;
                }
                if (getPlayerLevel(player.getUniqueId()).hasAccesstoShop(name))
                    sf.openInv(player, name.replaceAll("_", " "), 1);
                else
                {
                    InGameUtilities.sendPlayerError(player, "Vous n'avez pas encore accès à ce shop. Pour y avoir accès, augmentez votre niveau.");
                }

            } else if (args[0].equalsIgnoreCase("newitem") && player.hasPermission("fireland.admin")) {
                if (player.getItemInHand() != null && player.getItemInHand().hasItemMeta()) {
                    String name = player.getItemInHand().getItemMeta().getDisplayName();
                        /*name = name.replaceAll("[?.{1}]", "");
                        name = name.replaceAll("[^a-zA-Z0-9]", " ");*/
                    name = name.replaceAll("§7", "").replaceAll("\\u25ab", "").replaceAll("\\u25aa", "").replaceAll("\\u02D7","");

                    name = name.replaceAll("_", " ");

                    String[] words = name.split(" ");
                    StringBuilder sbb = new StringBuilder();
                    for (int i = 0; i < words.length; i++) {
                        if (i + 1 != words.length)
                        {

                            if (words[i + 1].contains("«") || words[i + 1].contains("»")) {
                                sbb.append(words[i]);
                                break;
                            }//«
                            else {
                                sbb.append(words[i]).append(" ");
                            }
                        }
                        else {
                            sbb.append(words[i]);
                        }
                    }
                    name = sbb.toString().trim();


                    Material item = player.getItemInHand().getType();
                    short dura = player.getItemInHand().getDurability();
                    int price = Integer.parseInt(args[1]);
                    int sell = Integer.parseInt(args[2]);
                    String shop = args[3];

                    StringBuilder sb = new StringBuilder();
                    for (int i = 4; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    int custommodeldata = 0;
                    if (player.getItemInHand().getItemMeta().hasCustomModelData())
                    {
                        custommodeldata = player.getItemInHand().getItemMeta().getCustomModelData();
                    }

                    player.sendMessage("n:" + name);
                    player.sendMessage("i:" + item);
                    player.sendMessage("d:" + dura);
                    player.sendMessage("p:" + price);
                    player.sendMessage("se:" + sell);
                    player.sendMessage("sh:" + shop);

                    String command = sb.toString().trim();

                    player.sendMessage("c:" + command);
                    ShopFunction sf = new ShopFunction(main, player);
                    sf.addItemOnShop(name, item, dura, price, sell, shop, command, custommodeldata);
                    player.sendMessage("§aItem " + name + "§r§a mis en vente dans le shop " + shop + " !");
                }
                else
                {
                    player.sendMessage("L'item dans votre main est invalide.");
                }
            } else {
                player.sendMessage("§cUsage : /shop <shop/newitem>");
            }
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        ArrayList<String> l = new ArrayList<>();
        if (strings.length == 1 && commandSender.hasPermission("fireland.shop.admin"))
        {
            ShopFunction sf = new ShopFunction(main , null);
            l = sf.getAllShop();
            l.add("newitem");
        }
        else if (strings.length > 1 && commandSender.hasPermission("fireland.shop.admin"))
        {
            if (strings.length == 2)
            {
                l.add("--Prix");
                l.add("1");
            }
            else if (strings.length == 3)
            {
                l.add("--Vente");
                l.add("1");
            }
            else if (strings.length == 4)
            {
                ShopFunction sf = new ShopFunction(main , null);
                l = sf.getAllShop();
            }
            else if (strings.length == 5)
            {
                l.add("--Commande");
                l.add("mcgive");
                l.add("shot");
            }
            else if (strings.length == 6)
            {
                if (strings[4].equalsIgnoreCase("mcgive"))
                {
                    l.add("NE RIEN METTRE APRES mcgive");
                }
                else
                {
                    l.add("give");
                }
            }
            else if (strings.length == 7)
            {
                if (strings[4].equalsIgnoreCase("mcgive"))
                {
                    l.add("NE RIEN METTRE APRES mcgive");
                }
                else
                {
                    l.add("item");
                }
            }
            else if (strings.length == 8)
            {
                if (strings[4].equalsIgnoreCase("mcgive"))
                {
                    l.add("NE RIEN METTRE APRES mcgive");
                }
                else
                {
                    l.add("--METTRE QUE PLAYER");
                    l.add("Player");
                }
            }
        }
        return l;
    }
}
