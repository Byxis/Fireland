package fr.byxis.faction.zone.zoneclass;

import fr.byxis.fireland.utilities.BlockUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FactionCapturingClass {

    private String name;
    private final List<Player> playerList;
    private double progression;
    private final String color;
    private final Material material;

    public FactionCapturingClass(String _name, List<Player> _playerList, int _progression, String... _color)
    {
        this.name = _name;
        this.playerList = _playerList;
        this.progression = _progression;
        if (_color != null)
        {
            this.color = Arrays.toString(_color);
        }
        else
        {
            this.color = "§7";
        }
        this.material = BlockUtilities.getGlassPaneColor(Arrays.toString(_color));
    }


    public String getName() {
        return name;
    }

    public void changeName(String _name)
    {
        this.name = _name;
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
        int totalTime = totalProgressionNeeded * 60;
        this.progression += (seconds / totalTime) * 100;
    }

    public void setProgression(int _progression) {
        this.progression = _progression;
    }

    public double getNextProgression(int totalProgressionNeeded, double seconds) {
        int totalTime = totalProgressionNeeded * 60;
        return this.progression + (seconds / totalTime) * 100;
    }

    public String getFormattedName()
    {
        return (this.name.substring(0, 1).toUpperCase() + this.name.substring(1)).replaceAll("-", " ");
    }
}
