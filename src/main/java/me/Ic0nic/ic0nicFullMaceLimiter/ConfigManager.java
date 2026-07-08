package me.Ic0nic.ic0nicFullMaceLimiter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;


public class ConfigManager {
    private final Ic0nicFullMaceLimiter plugin;
    private FileConfiguration config;
    private int MACE_LIMIT,COOLDOWN_TIME, MACE_CRAFT_EVENT_TIME;
    private boolean GLOWING_MACE=true,ENFORCE_MACE_LIMIT=true,STASH_MACE=false, ENCHANTABLE=false, UNDESTRUCTABLE=false,COOLDOWN_ENABLED, PUNISH_MISSING, MACE_CRAFT_EVENT_ENABLED, FORGIVE_SHIELD_HIT;
    private final HashMap<Enchantment, Integer> enchantmentsLimit = new HashMap<>();
    public ConfigManager(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        this.start();
    }

    public void start() {

        plugin.saveDefaultConfig();

        config = plugin.getConfig();
        loadConfig();


    }

    private void loadConfig() {
        MACE_LIMIT = config.getInt("mace-limit",1);
        GLOWING_MACE = config.getBoolean("glowing-mace",true);
        ENFORCE_MACE_LIMIT = config.getBoolean("enforce-mace-limit",true);
        STASH_MACE = config.getBoolean("stash-mace",false);
        ENCHANTABLE = config.getBoolean("enchantments-limits.enchantable",false);
        UNDESTRUCTABLE = config.getBoolean("undestructable-maces",false);
        COOLDOWN_ENABLED = config.getBoolean("cooldown.enabled",true);
        COOLDOWN_TIME = config.getInt("cooldown.time",1200);
        PUNISH_MISSING = config.getBoolean("cooldown.punish-missing",false);
        MACE_CRAFT_EVENT_ENABLED = config.getBoolean("mace-craft-event.enabled",true);
        MACE_CRAFT_EVENT_TIME = config.getInt("mace-craft-event.time",1200);
        FORGIVE_SHIELD_HIT = config.getBoolean("cooldown.forgive-shield-hit",false);

        enchantmentsLimit.put(Enchantment.DENSITY,config.getInt("enchantments-limits.density",5));
        enchantmentsLimit.put(Enchantment.BREACH,config.getInt("enchantments-limits.breach",4));
        enchantmentsLimit.put(Enchantment.FIRE_ASPECT,config.getInt("enchantments-limits.fire-aspect",2));
        enchantmentsLimit.put(Enchantment.UNBREAKING,config.getInt("enchantments-limits.unbreaking",3));
        enchantmentsLimit.put(Enchantment.MENDING,config.getInt("enchantments-limits.mending",1));
        enchantmentsLimit.put(Enchantment.WIND_BURST,config.getInt("enchantments-limits.wind-burst",3));
        enchantmentsLimit.put(Enchantment.VANISHING_CURSE,config.getInt("enchantments-limits.vanishing-curse",1));
    }

    public int maceLimit() {return MACE_LIMIT;}
    public boolean glowingMace() { return GLOWING_MACE;}
    public boolean enforceMaceLimit() { return ENFORCE_MACE_LIMIT;}
    public boolean stashMace() { return STASH_MACE;}
    public boolean enchantable() { return ENCHANTABLE;}
    public boolean undestructableMaces() { return UNDESTRUCTABLE;}
    public boolean cooldownEnabled() {return COOLDOWN_ENABLED;}
    public int cooldownTime() {return COOLDOWN_TIME;}
    public int getEnchantLimit(Enchantment enchantment) { return enchantmentsLimit.getOrDefault(enchantment, 255) ;}
    public boolean punishMissing() {return PUNISH_MISSING;}
    public boolean maceCraftEventEnabled() {return MACE_CRAFT_EVENT_ENABLED;}
    public int maceCraftEventTime() {return MACE_CRAFT_EVENT_TIME;}
    public boolean forgiveShieldHit() {return FORGIVE_SHIELD_HIT;}

    public void end() {

    }
    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadConfig();
    }
}