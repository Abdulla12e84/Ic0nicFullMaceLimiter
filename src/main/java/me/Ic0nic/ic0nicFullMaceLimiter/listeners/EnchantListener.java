package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class EnchantListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;
    public EnchantListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        EnchantmentOffer[] offers = event.getOffers();
        if (!plugin.configManager.enchantable()) {
            event.setCancelled(true);
            //Arrays.fill(offers, null);
            return;
        }
        for (int i = 0; i < offers.length; i++) {
            EnchantmentOffer offer = offers[i];
            int newLevel = Math.min(offer.getEnchantmentLevel(), plugin.configManager.getEnchantLimit(offer.getEnchantment()));
            if (newLevel > 0)
                offer.setEnchantmentLevel(newLevel);
            else
                offers[i] = null;
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!plugin.isCraftedMace(event.getItem())) return;
        Map<Enchantment, Integer> map = event.getEnchantsToAdd();
        plugin.limitEnforcerListener.enforceEnchantments(event.getItem());
        if (!plugin.configManager.enchantable()) {
            map.clear();
            return;
        }

        Iterator<Map.Entry<Enchantment, Integer>> iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Enchantment, Integer> entry = iterator.next();
            int limit = plugin.configManager.getEnchantLimit(entry.getKey());

            if (limit > 0) {
                map.put(entry.getKey(), Math.min(entry.getValue(), limit));
            } else
                iterator.remove();

        }

    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory view = event.getInventory();
        if (view.getFirstItem() == null || view.getSecondItem() == null) return;
        if (!plugin.isCraftedMace(view.getFirstItem())) return;

        if (view.getSecondItem().getType() != Material.ENCHANTED_BOOK) return;
        if (!plugin.configManager.enchantable()) {
            event.setResult(null);
            return;
        }
        ItemStack result  = event.getResult();
        if (result == null) return;
        plugin.limitEnforcerListener.enforceEnchantments(result);
        event.setResult(result);

    }

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void onAnvilEndPrepare(PrepareAnvilEvent event) {
        AnvilInventory view = event.getInventory();
        plugin.broadcastMessage("triggered event",false);
        if (view.getFirstItem() != null) {
            plugin.broadcastMessage("first item = " + view.getFirstItem().toString(),false);
        }
        if (view.getSecondItem() != null) {
            plugin.broadcastMessage("second item = " + view.getSecondItem().toString(),false);
        }
        if (view.getResult() != null) {
            plugin.broadcastMessage("result item = " + view.getResult().toString(),false);
        }

        if (event.getResult() != null) {
            plugin.broadcastMessage("E-result item = " + event.getResult().toString(),false);
        }


    }*/


}
