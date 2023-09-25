package fr.byxis.player.items;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.backpack.BackPack;
import fr.byxis.player.items.boussole.boussole;
import fr.byxis.player.items.lamp.lamp;
import fr.byxis.player.items.notes.OpenNotes;
import fr.byxis.player.items.parachute.parachute;
import fr.byxis.player.items.serum.serum;
import fr.byxis.player.items.toxic.SporeDamage;
import fr.byxis.player.items.toxic.infectedPlayer;
import fr.byxis.player.items.toxic.mask;
import fr.byxis.player.items.water.bottleFilled;
import fr.byxis.player.items.water.thirst;

public class itemEnabler {

    private final Fireland main;
    private SporeDamage sporeDamage;

    public itemEnabler(Fireland main)
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
        main.getServer().getPluginManager().registerEvents(new serum(), main);
    }

    private void Toxic()
    {
        //Infection
        main.getCommand("cure").setExecutor(new infectedPlayer(main));
        main.getCommand("infect").setExecutor(new infectedPlayer(main));
        main.getServer().getPluginManager().registerEvents(new infectedPlayer(main), main);

        //Mask
        main.getServer().getPluginManager().registerEvents(new mask(), main);
        sporeDamage = new SporeDamage(main);
    }

    private void BackPack()
    {
        main.getCommand("backpack").setExecutor(new BackPack(main));
        main.getServer().getPluginManager().registerEvents(new BackPack(main), main);
    }

    private void Lamp()
    {
        main.getServer().getPluginManager().registerEvents(new lamp(main), main);
    }

    private void Parachute()
    {
        main.getServer().getPluginManager().registerEvents(new parachute(main), main);
    }

    private void Water()
    {
        main.getServer().getPluginManager().registerEvents(new bottleFilled(main), main);
        main.getCommand("thirst").setExecutor(new thirst(main));
        main.getServer().getPluginManager().registerEvents(new thirst(main), main);
    }

    private void Boussole()
    {
        main.getServer().getPluginManager().registerEvents(new boussole(main), main);
    }

    private void Notes()
    {
        OpenNotes openNotes = new OpenNotes(main);
        main.getServer().getPluginManager().registerEvents(openNotes, main);
        main.getCommand("fnote").setExecutor(openNotes);

    }

}
