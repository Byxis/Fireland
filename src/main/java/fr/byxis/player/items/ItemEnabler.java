package fr.byxis.player.items;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.backpack.BackPack;
import fr.byxis.player.items.compass.Compass;
import fr.byxis.player.items.lamp.Lamp;
import fr.byxis.player.items.notes.OpenNotes;
import fr.byxis.player.items.parachute.Parachute;
import fr.byxis.player.items.serum.Serum;
import fr.byxis.player.items.toxic.InfectedPlayer;
import fr.byxis.player.items.toxic.Mask;
import fr.byxis.player.items.toxic.SporeDamage;
import fr.byxis.player.items.water.BottleFilled;
import fr.byxis.player.items.water.Thirst;

public class ItemEnabler
{

    private final Fireland main;
    private SporeDamage sporeDamage;

    public ItemEnabler(Fireland main)
    {
        this.main = main;

        //Enable
        Toxic();
        BackPack();
        Lamp();
        Parachute();
        Water();
        Boussole();
        Notes();
        main.getServer().getPluginManager().registerEvents(new Serum(), main);
    }

    private void Toxic()
    {
        //Infection
        main.getCommand("cure").setExecutor(new InfectedPlayer(main));
        main.getCommand("infect").setExecutor(new InfectedPlayer(main));
        main.getServer().getPluginManager().registerEvents(new InfectedPlayer(main), main);

        //Mask
        main.getServer().getPluginManager().registerEvents(new Mask(), main);
        sporeDamage = new SporeDamage(main);
    }

    private void BackPack()
    {
        main.getCommand("backpack").setExecutor(new BackPack(main));
        main.getServer().getPluginManager().registerEvents(new BackPack(main), main);
    }

    private void Lamp()
    {
        main.getServer().getPluginManager().registerEvents(new Lamp(main), main);
    }

    private void Parachute()
    {
        main.getServer().getPluginManager().registerEvents(new Parachute(main), main);
    }

    private void Water()
    {
        main.getServer().getPluginManager().registerEvents(new BottleFilled(main), main);
        main.getCommand("thirst").setExecutor(new Thirst(main));
        main.getServer().getPluginManager().registerEvents(new Thirst(main), main);
    }

    private void Boussole()
    {
        main.getServer().getPluginManager().registerEvents(new Compass(main), main);
    }

    private void Notes()
    {
        OpenNotes openNotes = new OpenNotes(main);
        main.getServer().getPluginManager().registerEvents(openNotes, main);
        main.getCommand("fnote").setExecutor(openNotes);

    }

}
