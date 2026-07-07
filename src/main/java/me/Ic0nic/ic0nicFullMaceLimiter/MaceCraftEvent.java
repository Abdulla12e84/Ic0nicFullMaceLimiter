package me.Ic0nic.ic0nicFullMaceLimiter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class MaceCraftEvent implements Listener {
    private final Ic0nicFullMaceLimiter plugin;

    private final Location location;
    private int time;
    public MaceCraftEvent(Ic0nicFullMaceLimiter plugin, Player player) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        location = player.getLocation();
        time = plugin.configManager.cooldownTime();

    }

    public static void broadCastCraftedMace(Ic0nicFullMaceLimiter plugin, String name, Location loc) {
        plugin.getServer().sendMessage(Component.text(name + " is Crafting the mace at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
    }
}
