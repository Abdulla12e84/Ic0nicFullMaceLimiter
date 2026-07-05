package me.Ic0nic.ic0nicFullMaceLimiter;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;

import org.bukkit.event.player.*;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EventListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;
    public EventListener(Ic0nicFullMaceLimiter plugin) {
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


    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        if (plugin.isCraftedMace(event.getEntity().getItemStack())) {
            plugin.broadcastMessage("A MACE (ITEM) HAS SPAWNED",false);
            if (plugin.configManager.glowingMace())
                event.getEntity().setGlowing(true);
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
            plugin.broadcastMessage("BROKING MACE",true);
            plugin.dataManager.decrementMaceCount();
        }
    }

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        if (plugin.isCraftedMace(event.getEntity().getItemStack())) {
            plugin.broadcastMessage("MACE DESPAWNED",true);
            if (plugin.configManager.undestructableMaces())
                event.setCancelled(true);
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
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {

            if (plugin.isCraftedMace(item.getItemStack())) {
                event.setCancelled(true);
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

    /*
    ========================================
    OTHER EVENTS
    ========================================
     */


    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (newItem == null) return;

        if (newItem.getType() == Material.MACE) {
            plugin.enforceMaceLimit(newItem);
            if (plugin.isCraftedMace(newItem)) {
                player.sendMessage("YOU'RE HOLDING A CRAFTED MACE");
            } else
                player.sendMessage("YOU'RE HOLDING AN UNTRACKED MACE");
        }
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() == Material.MACE) {
            plugin.enforceMaceLimit(event.getItem().getItemStack());
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem().getType() == Material.MACE) {
            plugin.enforceMaceLimit(event.getMainHandItem());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.sendMessage("View type " + event.getView().getType().toString());
        if (event.getCurrentItem() != null) {
            player.sendMessage("Item is " + event.getCurrentItem().getType().toString());
        }
        player.sendMessage("slot num : " + event.getSlot());
        player.sendMessage("slot type : " + event.getSlotType().toString());
        switch (event.getView().getType()) {
            case InventoryType.CHEST, InventoryType.ENDER_CHEST, InventoryType.BARREL, InventoryType.CRAFTER, InventoryType.DECORATED_POT, InventoryType.HOPPER,
                 InventoryType.DISPENSER,InventoryType.DROPPER, InventoryType.SHULKER_BOX:
                if (!plugin.configManager.stashMace()) {
                    if (event.getCurrentItem() != null) {
                        if (plugin.isCraftedMace(event.getCurrentItem())) {
                            event.setCancelled(true);
                        } else if (event.getCurrentItem().getType() == Material.MACE) {
                            plugin.enforceMaceLimit(event.getCurrentItem());
                        }
                    }
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (!plugin.configManager.cooldownEnabled()) return;
            if (plugin.isCraftedMace(player.getActiveItem())) {
                player.setCooldown(Material.MACE, plugin.configManager.cooldownTime());
            }
        }
    }
    @EventHandler
    public void onPlayerSwing(PlayerArmSwingEvent event) {
        if (event.getPlayer().hasCooldown(Material.MACE)) {
            if (plugin.isCraftedMace(event.getPlayer().getActiveItem())) {
                event.setCancelled(true);
            }
        }
    }
}
