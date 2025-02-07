package fr.byxis.player.level;

import fr.byxis.fireland.Fireland;
import fr.byxis.fireland.utilities.BasicUtilities;
import fr.byxis.fireland.utilities.InGameUtilities;
import fr.byxis.jeton.JetonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static fr.byxis.player.level.LevelStorage.getPlayerLevel;

public class LevelCommand implements CommandExecutor {
    private final Fireland m_main;
    public LevelCommand(Fireland main) {
        m_main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player p)
        {
            PlayerLevel pl = null;
            if (args.length == 0)
            {
                pl = getPlayerLevel(p.getUniqueId());
                InGameUtilities.sendPlayerInformation(p, "Votre niveau : " + pl.getLevel() + " (" + pl.getXp() + "/" + pl.getRemainingXp() + ")");
                InGameUtilities.sendPlayerInformation(p, "Votre rang : " + pl.getStringRank() + " (" + pl.getNation().name() + ")");
                return true;
            }
            else if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("changeNation8484"))
                {
                    pl = getPlayerLevel(p.getUniqueId());
                    if (JetonManager.getJetonsPlayer(p.getUniqueId()) > pl.GetJetonPriceNationChange() && Fireland.eco.has(p, pl.GetMoneyPriceNationChange()))
                    {
                        InGameUtilities.sendPlayerSucces(p, "Vous avez acheté un changement de nation. Pour changer, votre nation, allez voir l'intendant");
                        JetonManager.payJetons(p, pl.GetJetonPriceNationChange(), "Changement de Nation", false, true);
                        Fireland.eco.withdrawPlayer(p, pl.GetMoneyPriceNationChange());
                        pl.setCanChange(true);
                        return true;
                    }
                    InGameUtilities.sendPlayerError(p, "Vous n'avez pas les fonds nécessaire.");
                    return false;
                }
                if (BasicUtilities.getUuid(args[0]) == null)
                {
                    InGameUtilities.sendPlayerError(p, "Player inconnu");
                    return false;
                }
                pl = getPlayerLevel(BasicUtilities.getUuid(args[0]));
                InGameUtilities.sendPlayerInformation(p, "Niveau de " + args[0] + " : " + pl.getLevel() + " (" + pl.getXp() + "/" + pl.getRemainingXp() + ")");
                InGameUtilities.sendPlayerInformation(p, "Rang de " + args[0] + " : " + pl.getStringRank() + " (" + pl.getNation().name() + ")");
                return true;
            }
            else if (p.hasPermission("fireland.admin.level"))
            {
                if (args.length != 4)
                {
                    InGameUtilities.sendPlayerError(p, "Utilisation : /level <set/add/remove> <level/xp/nation/rang/canchange> <amount/nation/true/false> <player>");
                    return false;
                }
                if (BasicUtilities.getUuid(args[3]) == null)
                {
                    InGameUtilities.sendPlayerError(p, "Player inconnu");
                    return false;
                }
                pl = getPlayerLevel(BasicUtilities.getUuid(args[3]));
                if (args[0].equalsIgnoreCase("set"))
                {
                    if (args[1].equalsIgnoreCase("level"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.setLevel(amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                    else if (args[1].equalsIgnoreCase("canchange"))
                    {
                        try{
                            boolean amount = Boolean.parseBoolean(args[2]);
                            pl.setCanChange(amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                    else if (args[1].equalsIgnoreCase("rang"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.setRang(amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                    else if (args[1].equalsIgnoreCase("xp"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.setXp(amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                    else if (args[1].equalsIgnoreCase("nation"))
                    {
                        try{
                            LevelStorage.Nation nation = LevelStorage.Nation.valueOf(args[2]);
                            pl.setNation(nation);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("add"))
                {
                    if (args[1].equalsIgnoreCase("level"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.addLevel(amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                    else if (args[1].equalsIgnoreCase("xp"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.addXp(amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("remove"))
                {
                    if (args[1].equalsIgnoreCase("level"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.addLevel(-amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                    else if (args[1].equalsIgnoreCase("xp"))
                    {
                        try{
                            int amount = Integer.parseInt(args[2]);
                            pl.addXp(-amount);
                            InGameUtilities.sendPlayerSucces(p, "Joueurs " + args[3] + " mis à jour avec succès");
                            return true;
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                }
                InGameUtilities.sendPlayerError(p, "Utilisation : /level <set/add/remove> <level/xp/nation> <amount/nation> <player>");
                return false;
            }
        }
        else if (commandSender instanceof ConsoleCommandSender)
        {
            if (args.length != 4 || BasicUtilities.getUuid(args[3]) == null)
            {
                return false;
            }
            PlayerLevel pl = getPlayerLevel(BasicUtilities.getUuid(args[3]));
            if (args[0].equalsIgnoreCase("set"))
            {
                if (args[1].equalsIgnoreCase("level"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.setLevel(amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("xp"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.setXp(amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("rang"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.setRang(amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("nation"))
                {
                    try{
                        LevelStorage.Nation nation = LevelStorage.Nation.valueOf(args[2]);
                        pl.setNation(nation);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
            }
            else if (args[0].equalsIgnoreCase("add"))
            {
                if (args[1].equalsIgnoreCase("level"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.addLevel(amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("xp"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.addXp(m_main, amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("rang"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.addRang(amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                if (args[1].equalsIgnoreCase("level"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.addLevel(-amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("xp"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.addXp(m_main, -amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
                else if (args[1].equalsIgnoreCase("rang"))
                {
                    try{
                        int amount = Integer.parseInt(args[2]);
                        pl.addRang(-amount);
                        return true;
                    }
                    catch (Exception ignored)
                    {

                    }
                }
            }
            return false;
        }
        return false;
    }
}
