package me.Ic0nic.ic0nicFullMaceLimiter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {
    private final Ic0nicFullMaceLimiter plugin;
    private File dataFile ;
    private YamlConfiguration data;

    private int MACE_COUNT=0;
    private final ArrayList<MaceCraftEvent> maceCraftEvents = new ArrayList<>();

    public  DataManager(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
    }

    public void start() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdir();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        data =  YamlConfiguration.loadConfiguration(dataFile);

        MACE_COUNT = data.getInt("MACE_COUNT");

        ConfigurationSection section = data.getConfigurationSection("mace-craft-events");

        if (section != null) {
            for (String key : section.getKeys(false)) {
                maceCraftEvents.add(new MaceCraftEvent(plugin, section.getString(key+".name"),section.getLocation(key+".loc"),section.getInt(key+".time"),section.getInt(key+".org"),key));
            }
        }


        Bukkit.getScheduler().runTaskTimer(this.plugin, () ->
                        saveData()
                ,6000L,6000L);
    }

    public void end() {
        saveData();
    }

    public void saveData() {
        data.set("MACE_COUNT", MACE_COUNT);

        data.set("mace-craft-events", null);
        ConfigurationSection section = data.createSection("mace-craft-events");
        for (MaceCraftEvent event : maceCraftEvents) {
            ConfigurationSection subsec = section.createSection(event.getUUID());
            subsec.set("name",event.getName());
            subsec.set("loc", event.getLocation());
            subsec.set("org",(int)event.getOrgTime());
            subsec.set("time",event.getTime());
        }
        try {
            this.data.save(this.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMaceOnLimit() {
        return MACE_COUNT >= plugin.configManager.maceLimit();
    }

    public void incrementMaceCount() {
        MACE_COUNT++;

    }

    public int getMaceCount() {return MACE_COUNT;}
    public void setMaceCount(int n) {MACE_COUNT = Math.clamp(n,0, plugin.configManager.maceLimit());}
    public void decrementMaceCount() {
        MACE_COUNT = Math.max(0, MACE_COUNT-1);
    }

    public MaceCraftEvent createMaceCraftEvent(Player player) {
        MaceCraftEvent mce = new MaceCraftEvent(plugin, player);
        maceCraftEvents.add(mce);
        return mce;
    }

    public void removeMaceCraftEvent(MaceCraftEvent mce) {
        maceCraftEvents.remove(mce);
    }

    public ArrayList<MaceCraftEvent>  getMaceCraftEvents() {return maceCraftEvents;}

}