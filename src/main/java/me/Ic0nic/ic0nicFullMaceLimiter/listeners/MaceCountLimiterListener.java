package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MaceCountLimiterListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;
    public MaceCountLimiterListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    /*
    ========================================
    MACE CREATION EVENTS
    ========================================
     */
    @EventHandler
    public void onViewingRecipe(PrepareItemCraftEvent event) {
        ItemStack item = event.getInventory().getResult();
        if (item == null) return;
        if (plugin.dataManager.isMaceOnLimit() && item.getType() == Material.MACE) {
            plugin.getLogger().info("PREVENTING MACE CRAFT");
            item.setAmount(0);
            plugin.broadcastMessage("Preventing MACE CRAFT",false);
        }
    }

    @EventHandler
    public void onCraftingMace(CraftItemEvent event) {
        ItemStack item = event.getRecipe().getResult();

        if (item.getType() == Material.MACE) {
            if (plugin.dataManager.isMaceOnLimit()) {
                plugin.getLogger().info("ur not sneaky");
                item.setAmount(0);
            } else{
                plugin.getLogger().info("CRAFTING MACE");
                plugin.broadcastMessage("MACE HAS BEEN CRAFTED",false);
                if (event.getCurrentItem() != null) {
                    plugin.markCraftedMace(event.getCurrentItem());
                }
                plugin.dataManager.incrementMaceCount();
            }
        }
    }


        /*
    ========================================
    MACE DESTRUCTION EVENTS
    ========================================
     */

    @EventHandler
    public void onItemBreakEvent(PlayerItemBreakEvent event) {
        if (plugin.isCraftedMace(event.getBrokenItem())) {
            plugin.broadcastMessage("BROKEN MACE",true);
            plugin.dataManager.decrementMaceCount();
        }
    }

    @EventHandler(priority =  EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        if (plugin.isCraftedMace(event.getEntity().getItemStack())) {
            plugin.broadcastMessage("MACE DESPAWNED",true);
            plugin.dataManager.decrementMaceCount();
        }

    }


    @EventHandler
    public void onClearCommand(PlayerCommandPreprocessEvent event) {
        String[] cmd = event.getMessage().toLowerCase().split(" ");
        if (cmd[0].equalsIgnoreCase("/clear")) {
            if (cmd.length == 3) {
                if (!cmd[2].contains("mace")) {
                    return;
                }
            }
            Player player;
            if (cmd.length == 2) {
                player = Bukkit.getPlayer(cmd[1]);
            } else player = event.getPlayer();
            if (player == null) return;
            for (ItemStack item : player.getInventory().getContents()) {
                if (plugin.isCraftedMace(item)) {
                    plugin.broadcastMessage("MACE HAS BEEN CLEARED",true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntity() instanceof Item item) {

            if (plugin.isCraftedMace(item.getItemStack())) {
                plugin.dataManager.decrementMaceCount();
                plugin.broadcastMessage("MACE HAS BEEN REMOVED",true);
                if (item.getLocation().getY() <= -64) {
                    plugin.broadcastMessage("MACE HAS FELL OUTSIDE THE WORLD",true);
                }
            }
        }
    }
}
