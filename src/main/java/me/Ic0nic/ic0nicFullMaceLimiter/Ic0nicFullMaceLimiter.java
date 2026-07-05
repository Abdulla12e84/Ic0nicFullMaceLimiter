package me.Ic0nic.ic0nicFullMaceLimiter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Ic0nicFullMaceLimiter extends JavaPlugin {
    public EventListener eventListener;
    private MaceLimiterCommandExecuter commandExecuter;
    public ConfigManager configManager;
    public DataManager dataManager = new DataManager(this);

    public final NamespacedKey craftedMaceKey = new NamespacedKey(this, "CraftedMace");

    @Override
    public void onEnable() {
        // Plugin startup logic
        configManager = new ConfigManager(this);
        eventListener = new EventListener(this);
        commandExecuter = new MaceLimiterCommandExecuter(this);
        dataManager.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataManager.end();
    }



    public void broadcastMessage(String message, boolean isRed) {
        getServer().sendMessage(MiniMessage.miniMessage().deserialize("<" +(isRed ? "red" : "green") + ">"+message));
    }

    public void markCraftedMace(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("CRAFTED MACE").decoration(TextDecoration.ITALIC, false));
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

    public void enforceMaceLimit(ItemStack mace) {
        if (isCraftedMace(mace)) {
            if (!configManager.enchantable()) {
                mace.removeEnchantments();
                return;
            }
            HashMap<Enchantment, Integer> result = new HashMap<>();
            for (Map.Entry<Enchantment, Integer> entry : mace.getEnchantments().entrySet()) {
                result.put(entry.getKey(), Math.min(entry.getValue(),configManager.getEnchantLimit(entry.getKey())));

            }
            mace.removeEnchantments();
            mace.addEnchantments(result);


        } else if (configManager.enforceMaceLimit())
            mace.setAmount(0);

    }
}
