package fr.byxis.jeton;

import fr.byxis.db.DbConnection;
import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.InGameUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class JetonManager
{
    private static Fireland main;
    private static JetonSql jt;

    public JetonManager(Fireland _main)
    {
        if (JetonManager.jt == null)
            JetonManager.jt = new JetonSql(_main);
        if (JetonManager.main == null)
            JetonManager.main = _main;
    }

    public static void updatePlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.getCfgm().getJetonsDB();
        if (!jetonDB.contains(_uuid.toString()))
        {
            jetonDB.set(_uuid.toString(), 0);
            main.getCfgm().saveJetonsDB();
        }
    }

    public static int getJetonsPlayer(UUID _uuid)
    {
        FileConfiguration jetonDB = main.getCfgm().getJetonsDB();
        return jetonDB.getInt(_uuid.toString());
    }

    public static void setJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.getCfgm().getJetonsDB();
        jetonDB.set(_uuid.toString(), amount);
        main.getCfgm().saveJetonsDB();
    }

    public static void addJetonsPlayer(UUID _uuid, int amount)
    {
        main.getCfgm().getJetonsDBcfg().set(_uuid.toString(), main.getCfgm().getJetonsDBcfg().getInt(_uuid.toString()) + amount);
        main.getCfgm().saveJetonsDB();
    }

    public static void removeJetonsPlayer(UUID _uuid, int amount)
    {
        FileConfiguration jetonDB = main.getCfgm().getJetonsDB();
        int jeton = jetonDB.getInt(_uuid.toString());
        jeton -= amount;
        jetonDB.set(_uuid.toString(), jeton);
        main.getCfgm().saveJetonsDB();
    }

    public static boolean payJetons(Player _p, int _amount, String _desc, boolean _update, boolean _doLog)
    {
        int jetons = getJetonsPlayer(_p.getUniqueId());
        if (jetons >= _amount)
        {
            int facture = jt.createFacture(_p.getUniqueId().toString(), _amount, _desc, _update);
            if (facture != -1)
            {
                removeJetonsPlayer(_p.getUniqueId(), _amount);
                InGameUtilities.sendPlayerInformation(_p, "Vous avez payé §b" + _amount + "§r⛁§7. " + "(Facture n°" + facture + ").");
                return true;
            }
            InGameUtilities.sendPlayerError(_p, "Une erreur est survenue pendant la création de la facture. "
                    + "Vous n'avez pas été débité. Merci de contacter le staff pour résoudre ce problème.");
        }
        else if (_doLog)
        {
            InGameUtilities.sendPlayerError(_p, "Vous n'avez pas assez de jetons.");
        }
        return false;
    }

    public static void sendPlayerFacture(Player consulter, Player buyer, int page)
    {
        final DbConnection firelandConnection = main.getDatabaseManager().getFirelandConnection();
        int pageSize = 5;
        try (Connection connection = firelandConnection.getConnection())
        {

            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT number, amount, description " + "FROM jeton_history WHERE player_uuid = ? ORDER BY date DESC;");
            preparedStatement.setString(1, buyer.getUniqueId().toString());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                ArrayList<String[]> list = new ArrayList<>();
                while (rs.next())
                    list.add(new String[]
                    {rs.getString(1), String.valueOf(rs.getInt(2)), rs.getString(3)});

                if (consulter.getName().equals(buyer.getName()))
                    consulter.sendMessage("§8------------- §7Vos factures §8-------------");
                // "§8 [<] ---------------------------------- [>] "
                else
                    consulter.sendMessage("§8----------- §7Facture de " + buyer.getName() + " §8-----------");

                for (int i = page * pageSize; i < page * pageSize + pageSize && i < list.size(); i++)
                {
                    String number = list.get(i)[0];
                    String price = list.get(i)[1];
                    String desc = list.get(i)[2];
                    consulter.sendMessage("§aFacture n°§d " + number + "§a - §7" + desc + " §a(§b " + price + "⛁§a)");
                }
                ComponentBuilder message = new ComponentBuilder();
                if (buyer.getName().equals(consulter.getName()))
                {
                    if (page > 0)
                    {
                        message.append("§2[<]")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Page précédente").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/facture " + (page - 1)));
                    }
                    else
                    {
                        message.append("§7[<]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
                    }
                    message.append("§8 ---------------------------------- ")
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
                    if (page * pageSize + pageSize < list.size())
                    {
                        message.append("§4[>]")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Page suivante").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/facture " + (page + 1)));
                    }
                    else
                    {
                        message.append("§7[>]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
                    }

                    consulter.spigot().sendMessage(message.create());
                }
                else
                {
                    if (page > 0)
                    {
                        message.append("§2[<]")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Page précédente").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/facture " + buyer.getName() + " " + (page - 1)));
                    }
                    else
                    {
                        message.append("§7[<]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
                    }
                    message.append("§8 ---------------------------------- ")
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
                    if (page * pageSize + pageSize < list.size())
                    {
                        message.append("§4[>]")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Page suivante").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/facture " + buyer.getName() + " " + (page + 1)));
                    }
                    else
                    {
                        message.append("§7[>]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
                    }

                    consulter.spigot().sendMessage(message.create());
                }
            }
            else
            {
                if (consulter.getName().equals(buyer.getName()))
                    InGameUtilities.sendPlayerError(consulter, "Vous n'avez aucune facture");
                else
                    InGameUtilities.sendPlayerError(consulter, "Le joueur " + buyer.getName() + " n'a aucune facture.");
            }
        }
        catch (SQLException e)
        {
            // Une erreur est survenue (Problème de connexion à la BD)
            InGameUtilities.sendPlayerError(consulter, "Une erreur est survenue.");
            e.printStackTrace();
        }
    }

}
