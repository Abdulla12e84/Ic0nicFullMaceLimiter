package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;

public class UndestructableMaceListener implements Listener {
    private Ic0nicFullMaceLimiter plugin;
    public UndestructableMaceListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (!plugin.configManager.undestructableMaces()) return;
        if (event.getEntity() instanceof Item item) {

            if (plugin.isCraftedMace(item.getItemStack())) {

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        if (!plugin.configManager.undestructableMaces()) return;
        if (plugin.isCraftedMace(event.getEntity().getItemStack())) {
            event.setCancelled(true);
        }

    }



}
