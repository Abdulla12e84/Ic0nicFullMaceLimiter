package me.Ic0nic.ic0nicFullMaceLimiter;

import me.Ic0nic.ic0nicFullMaceLimiter.listeners.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


public final class Ic0nicFullMaceLimiter extends JavaPlugin {
    private MaceLimiterCommandExecuter commandExecuter;
    public ConfigManager configManager;
    public DataManager dataManager = new DataManager(this);

    public CooldownListener cooldownListener;
    public GlowingMaceListener glowingMaceListener;
    public LimitEnforcerListener limitEnforcerListener;
    public MaceCountLimiterListener maceCountLimiterListener;
    public StashMaceListener stashMaceListener;
    public EnchantListener enchantListener;
    public UndestructableMaceListener undestructableMaceListener;

    public final NamespacedKey craftedMaceKey = new NamespacedKey(this, "CraftedMace");
    public final NamespacedKey eventItemDisplayKey = new NamespacedKey(this, "EventItemDisplay");

    @Override
    public void onEnable() {
        // Plugin startup logic
        configManager = new ConfigManager(this);
        commandExecuter = new MaceLimiterCommandExecuter(this);
        dataManager.start();

        cooldownListener = new CooldownListener(this);
        glowingMaceListener = new GlowingMaceListener(this);
        limitEnforcerListener = new LimitEnforcerListener(this);
        maceCountLimiterListener = new MaceCountLimiterListener(this);
        stashMaceListener = new StashMaceListener(this);
        enchantListener = new EnchantListener(this);
        undestructableMaceListener = new UndestructableMaceListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataManager.end();
        configManager.onEnd();
    }



    public void markCraftedMace(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(craftedMaceKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
    }

    public boolean isCraftedMace(ItemStack item) {
        if (item.getType() == Material.MACE) {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().has(craftedMaceKey);
        }
        return false;
    }

}
