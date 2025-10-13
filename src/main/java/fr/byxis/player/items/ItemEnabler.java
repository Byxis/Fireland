package fr.byxis.player.items;

import fr.byxis.fireland.Fireland;
import fr.byxis.player.items.backpack.BackPack;
import fr.byxis.player.items.compass.Compass;
import fr.byxis.player.items.infection.virus.*;
import fr.byxis.player.items.infection.virus.InfectionTickSystem;
import fr.byxis.player.items.lamp.Lamp;
import fr.byxis.player.items.notes.OpenNotes;
import fr.byxis.player.items.parachute.Parachute;
import fr.byxis.player.items.infection.Mask;
import fr.byxis.player.items.infection.SporeDamage;
import fr.byxis.player.items.water.BottleFilled;
import fr.byxis.player.items.water.Thirst;

public class ItemEnabler
{

    private final Fireland main;
    private SporeDamage sporeDamage;
    private InfectionManager infectionManager;

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
    }

    private void toxic()
    {
        infectionManager = new InfectionManager(main, new InfectionRepository(main.getConfig()));
        infectionManager.loadAll();
        InfectionCommands infectionCommands = new InfectionCommands(infectionManager);
        main.getCommand("cure").setExecutor(infectionCommands);
        main.getCommand("cure").setTabCompleter(infectionCommands);
        main.getCommand("infect").setExecutor(infectionCommands);
        main.getCommand("infect").setTabCompleter(infectionCommands);
        main.getServer().getPluginManager().registerEvents(new InfectionListeners(infectionManager), main);
        InfectionTickSystem infectionTickSystem = new InfectionTickSystem(main, infectionManager);
        infectionTickSystem.start();

        //Mask
        main.getServer().getPluginManager().registerEvents(new Mask(), main);
        sporeDamage = new SporeDamage(main, infectionManager);
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

    public void SaveAll()
    {
        if (infectionManager != null)
        {
            infectionManager.saveAll();
        }
    }

    public InfectionManager getInfectionManager()
    {
        return infectionManager;
    }
}
