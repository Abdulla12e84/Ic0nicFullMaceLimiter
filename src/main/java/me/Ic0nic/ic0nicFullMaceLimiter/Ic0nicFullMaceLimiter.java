package me.Ic0nic.ic0nicFullMaceLimiter;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ic0nicFullMaceLimiter extends JavaPlugin {
    public EventListener eventListener;

    private int MACE_COUNT = 0;
    @Override
    public void onEnable() {
        // Plugin startup logic
        eventListener = new EventListener(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean isMaceOnLimit() {
        return MACE_COUNT >0;
    }

    public void incrementMaceCount() {
        getLogger().info("MACE COUNT INCREMENTING");
        MACE_COUNT++;
    }
    public void decrementMaceCount() {
        MACE_COUNT = Math.max(0, MACE_COUNT-1);
    }

    public void broadcastMessage(String message, boolean isRed) {
        getServer().sendMessage(MiniMessage.miniMessage().deserialize("<" +(isRed ? "red" : "green") + ">"+message));
    }
}
