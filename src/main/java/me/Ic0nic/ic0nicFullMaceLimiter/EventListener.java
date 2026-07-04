package me.Ic0nic.ic0nicFullMaceLimiter;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

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
        if (plugin.isMaceOnLimit() && item.getType() == Material.MACE) {
            plugin.getLogger().info("PREVENTING MACE CRAFT");
            item.setAmount(0);
            plugin.broadcastMessage("Preventing MACE CRAFT",false);
        }
    }

    @EventHandler
    public void onCraftingMace(CraftItemEvent event) {
        ItemStack item = event.getRecipe().getResult();

        if (item.getType() == Material.MACE) {
            if (plugin.isMaceOnLimit()) {
                plugin.getLogger().info("ur not sneaky");
                item.setAmount(0);
            } else{
                plugin.getLogger().info("CRAFTING MACE");
                plugin.broadcastMessage("MACE HAS BEEN CRAFTED",false);
                plugin.incrementMaceCount();
            }
        }
    }

    @EventHandler
    public void onPickCreativeItem(InventoryCreativeEvent event) {
        ItemStack item = event.getCursor();
        if (item.getType() == Material.MACE) {
            plugin.broadcastMessage("PICKING MACE FROM CREATIVE ",false);
        }

    }

    @EventHandler
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (item.getItemStack().getType() == Material.MACE ) {
                plugin.broadcastMessage("A MACE HAS SPAWNED",false);
            }
        }
    }

    @EventHandler
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.MACE ) {
            plugin.broadcastMessage("A MACE (ITEM) HAS SPAWNED",false);
        }
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        for (ItemStack item : event.getLoot())
            if (item.getType() == Material.MACE) {
                plugin.broadcastMessage("LOOTING GENERATE MACE",false);
            }


    }

    @EventHandler
    public void onGiveCommand(PlayerCommandPreprocessEvent event) {
        String[] cmd = event.getMessage().toLowerCase().split(" ");
        if (cmd[0].equalsIgnoreCase("/give")) {
            if (cmd.length == 3) {
                if (cmd[2].contains("mace")) {
                    plugin.broadcastMessage("AN ADMIN ISGIVING MACE",false);
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
        if (event.getBrokenItem().getType() == Material.MACE) {
            plugin.broadcastMessage("BROKING MACE",true);
        }
    }

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        if (event.getEntity().getItemStack().getType() == Material.MACE) {
            plugin.broadcastMessage("MACE DESPAWNED",true);
        }

    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (item.getItemStack().getType() == Material.MACE) {
                plugin.broadcastMessage("<yellow>MACE IS BURNING",true);
            }
        }
    }


    @EventHandler
    public void onClearCommand(PlayerCommandPreprocessEvent event) {
        String[] cmd = event.getMessage().toLowerCase().split(" ");
        if (cmd[0].equalsIgnoreCase("/clear")) {
            Player player;
            if (cmd.length == 2) {
                player = Bukkit.getPlayer(cmd[1]);
            } else player = event.getPlayer();
            if (player == null) return;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item.getType() == Material.MACE) {
                    plugin.broadcastMessage("MACE HAS BEEN CLEARED",true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (item.getItemStack().getType() == Material.MACE) {
                plugin.broadcastMessage("MACE IS GETTING DAMAGED",true);
            }
        }
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (event.getEntity() instanceof Item item) {
            if (item.getItemStack().getType() == Material.MACE) {
                plugin.broadcastMessage("MACE HAS BEEN REMOVED",true);
            }
        }
    }
}
