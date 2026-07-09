package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import me.Ic0nic.ic0nicFullMaceLimiter.MaceCraftEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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
        if (event.isRepair()) return;
        if (plugin.dataManager.isMaceOnLimit() && item.getType() == Material.MACE) {
            item.setAmount(0);
        }
    }

    @EventHandler
    public void onCraftingMace(CraftItemEvent event) {
        ItemStack item = event.getRecipe().getResult();

        if (item.getType() == Material.MACE) {
            if (plugin.dataManager.isMaceOnLimit()) {
                item.setAmount(0);
            } else{
                if (event.isShiftClick()) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(Component.text("Craft the mace without shift clicking").color(TextColor.color(255,0,0)));
                    return;
                }

                plugin.dataManager.incrementMaceCount();
                if (plugin.configManager.maceCraftEventEnabled()) {
                    Player player = (Player) event.getWhoClicked();

                    if (plugin.configManager.maceCraftEventTime() > 0) {
                        CraftingInventory inv = event.getInventory();
                        inv.setResult(null);
                        for (ItemStack is : inv.getMatrix()) {
                            if (is == null) continue;
                            is.setAmount(is.getAmount() - 1);
                        }
                        player.closeInventory();
                        plugin.dataManager.createMaceCraftEvent(player);
                    } else {
                        MaceCraftEvent.broadCastCraftedMace(plugin, player.getName() , player.getLocation());
                        if (event.getCurrentItem() != null) {

                            plugin.markCraftedMace(event.getCurrentItem());
                        }
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                } else {
                    if (event.getCurrentItem() != null) {
                        plugin.markCraftedMace(event.getCurrentItem());
                    }
                }
            }
        } else {
            item = event.getInventory().getResult();
            if (item != null)  {
                   if (item.getType() == Material.MACE) {
                       plugin.markCraftedMace(item);
                       plugin.dataManager.decrementMaceCount();
                   }
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
            plugin.dataManager.decrementMaceCount();

        }
    }



    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
                if (item == null) continue;
                if (plugin.isCraftedMace(item)) {
                    plugin.dataManager.decrementMaceCount();
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRemove(EntityRemoveEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (!plugin.isCraftedMace(item.getItemStack())) return;
            switch (event.getCause()) {
                case DESPAWN,EXPLODE, DEATH, OUT_OF_WORLD:
                    plugin.dataManager.decrementMaceCount();
            }


        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ItemDisplay display) {
                String uuid = display.getPersistentDataContainer().get(plugin.eventItemDisplayKey, PersistentDataType.STRING);
                if (uuid == null) continue;
                for (MaceCraftEvent craftEvent : plugin.dataManager.getMaceCraftEvents()) {
                    if (craftEvent.getUUID().equals(uuid)) {
                        craftEvent.recreateDisplayItem();
                        return;
                    }
                }
                display.remove();
            }
        }
    }
}
