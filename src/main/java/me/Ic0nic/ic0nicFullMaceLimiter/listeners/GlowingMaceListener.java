package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class GlowingMaceListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;
    public GlowingMaceListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        if (plugin.isCraftedMace(event.getEntity().getItemStack())) {
            if (plugin.configManager.glowingMace())
                event.getEntity().setGlowing(true);
        }
    }
}
