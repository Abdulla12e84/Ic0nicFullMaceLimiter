package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LimitEnforcerListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;
    public  LimitEnforcerListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }


    public void enforceEnchantments(@NotNull ItemStack mace) {
        if (!plugin.isCraftedMace(mace)) return;

        if (!plugin.configManager.enchantable()) {
            mace.removeEnchantments();
            return;
        }

        HashMap<Enchantment, Integer> result = new HashMap<>();
        for (Map.Entry<Enchantment, Integer> entry : mace.getEnchantments().entrySet()) {
            int limit =  plugin.configManager.getEnchantLimit(entry.getKey());
            if (limit < 1) continue;
            result.put(entry.getKey(), Math.min(entry.getValue(),plugin.configManager.getEnchantLimit(entry.getKey())));

        }
        mace.removeEnchantments();
        mace.addEnchantments(result);


    }

    public boolean enforceMaceLimit(ItemStack mace) {
        if (!plugin.configManager.enforceMaceLimit()) return false;
        return mace.getType() == Material.MACE && !plugin.isCraftedMace(mace);


    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (newItem == null) return;

        if (newItem.getType() == Material.MACE) {
            if (enforceMaceLimit(newItem))
                newItem.setAmount(0);
            if (plugin.isCraftedMace(newItem)) {
                enforceEnchantments(newItem);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() == Material.MACE) {
            if (enforceMaceLimit(event.getItem().getItemStack())) {
                event.setCancelled(true);
                event.getItem().remove();
                return;
            }
            enforceEnchantments(event.getItem().getItemStack());
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem().getType() == Material.MACE) {
            if (!enforceMaceLimit(event.getMainHandItem()))
                enforceEnchantments(event.getMainHandItem());
        }
    }

}
