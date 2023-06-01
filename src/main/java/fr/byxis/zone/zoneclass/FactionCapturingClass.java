package fr.byxis.zone.zoneclass;

import fr.byxis.fireland.utilities.BlockUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FactionCapturingClass {

    private String name;
    private final List<Player> playerList;
    private double progression;
    private String color;
    private Material material;

    public FactionCapturingClass(String name, List<Player> playerList, int progression, String... color)
    {
        this.name = name;
        this.playerList = playerList;
        this.progression = progression;
        if(color != null)
        {
            this.color = Arrays.toString(color);
        }
        else
        {
            this.color = "§7";
        }
        this.material = BlockUtilities.getGlassPaneColor(Arrays.toString(color));
    }


    public String getName() {
        return name;
    }

    public void changeName(String name)
    {
        this.name = name;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void addPlayerList(Player p) {
        this.playerList.add(p);
    }

    public void removePlayerList(Player p) {
        this.playerList.remove(p);
    }

    public double getProgression() {
        return progression;
    }

    public void addProgression(int totalProgressionNeeded, double seconds) {
        int TotalTime = totalProgressionNeeded*60;
        this.progression += (seconds/TotalTime)*100;
    }

    public void setProgression(int progression) {
        this.progression = progression;
    }

    public double getNextProgression(int totalProgressionNeeded, double seconds) {
        int TotalTime = totalProgressionNeeded*60;
        return this.progression + (seconds/TotalTime)*100;
    }

    public String getFormattedName()
    {
        return (this.name.substring(0, 1).toUpperCase() + this.name.substring(1)).replaceAll("-", " ");
    }
}
