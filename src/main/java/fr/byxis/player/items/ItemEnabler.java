package fr.byxis.player.items;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.backpack.BackPack;
import fr.byxis.player.items.compass.Compass;
import fr.byxis.player.items.lamp.Lamp;
import fr.byxis.player.items.notes.OpenNotes;
import fr.byxis.player.items.parachute.Parachute;
import fr.byxis.player.items.serum.Serum;
import fr.byxis.player.items.infection.InfectedPlayer;
import fr.byxis.player.items.infection.Mask;
import fr.byxis.player.items.infection.SporeDamage;
import fr.byxis.player.items.water.BottleFilled;
import fr.byxis.player.items.water.Thirst;

public class ItemEnabler
{

    private final Fireland main;
    private SporeDamage sporeDamage;

    public ItemEnabler(Fireland _main)
    {
        this.main = _main;

        //Enable
        toxic();
        backPack();
        lamp();
        parachute();
        water();
        boussole();
        notes();
        _main.getServer().getPluginManager().registerEvents(new Serum(), _main);
    }

    private void toxic()
    {
        //Infection
        main.getCommand("cure").setExecutor(new InfectedPlayer(main));
        main.getCommand("infect").setExecutor(new InfectedPlayer(main));
        main.getServer().getPluginManager().registerEvents(new InfectedPlayer(main), main);

        //Mask
        main.getServer().getPluginManager().registerEvents(new Mask(), main);
        sporeDamage = new SporeDamage(main);
    }

    private void backPack()
    {
        main.getCommand("backpack").setExecutor(new BackPack());
        main.getServer().getPluginManager().registerEvents(new BackPack(), main);
    }

    private void lamp()
    {
        main.getServer().getPluginManager().registerEvents(new Lamp(main), main);
    }

    private void parachute()
    {
        main.getServer().getPluginManager().registerEvents(new Parachute(main), main);
    }

    private void water()
    {
        main.getServer().getPluginManager().registerEvents(new BottleFilled(main), main);
        main.getCommand("thirst").setExecutor(new Thirst(main));
        main.getServer().getPluginManager().registerEvents(new Thirst(main), main);
    }

    private void boussole()
    {
        main.getServer().getPluginManager().registerEvents(new Compass(main), main);
    }

    private void notes()
    {
        OpenNotes openNotes = new OpenNotes(main);
        main.getServer().getPluginManager().registerEvents(openNotes, main);
        main.getCommand("fnote").setExecutor(openNotes);

    }

}
