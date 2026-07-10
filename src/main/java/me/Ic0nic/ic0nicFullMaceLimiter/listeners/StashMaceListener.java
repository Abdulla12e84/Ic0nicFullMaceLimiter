package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

public class StashMaceListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;

    private HashSet<Material> shelves = new HashSet<>();

    public StashMaceListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        if (plugin.is1_21_11OrMore()) {
            shelves = new HashSet<>(Arrays.asList(Material.WARPED_SHELF
                    , Material.ACACIA_SHELF, Material.BAMBOO_SHELF, Material.BIRCH_SHELF,
                    Material.DARK_OAK_SHELF, Material.JUNGLE_SHELF, Material.CRIMSON_SHELF,
                    Material.CHERRY_SHELF,Material.MANGROVE_SHELF, Material.PALE_OAK_SHELF,
                    Material.SPRUCE_SHELF, Material.OAK_SHELF
            ));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.configManager.stashMace()) return;

        Inventory view = event.getView().getTopInventory();
        if (view.getType() == InventoryType.PLAYER) return;

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        ItemStack clickedItem = event.getCurrentItem();
        if (view.getType() != InventoryType.CHEST &&
                view.getType() != InventoryType.DISPENSER &&
                view.getType() != InventoryType.DROPPER &&
                view.getType() != InventoryType.FURNACE &&
                view.getType() != InventoryType.WORKBENCH &&
                view.getType() != InventoryType.ENDER_CHEST &&
                view.getType() != InventoryType.HOPPER &&
                view.getType() != InventoryType.SHULKER_BOX &&
                view.getType() != InventoryType.BARREL &&
                view.getType() != InventoryType.BLAST_FURNACE &&
                view.getType() != InventoryType.SMOKER &&
                view.getType() != InventoryType.CRAFTER
        ) return;
        switch (event.getAction()) {
            case InventoryAction.MOVE_TO_OTHER_INVENTORY:
                if (clickedItem ==  null) return;
                if (clickedInventory.getType() == InventoryType.PLAYER) {
                    if (plugin.isCraftedMace(clickedItem)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                break;
            case InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE:
                if (clickedInventory.getType() == InventoryType.PLAYER) {
                    return;}
                clickedItem = event.getCursor();
                if (plugin.isCraftedMace(clickedItem)) {
                    event.setCancelled(true);
                    return;
                }
                break;
            case InventoryAction.HOTBAR_SWAP:
                if (clickedInventory.getType() == InventoryType.PLAYER) {
                    return;
                }

                clickedItem = event.getView().getBottomInventory().getItem(event.getHotbarButton());

                if (clickedItem == null) {
                    return;}

                if (plugin.isCraftedMace(clickedItem)) {
                    event.setCancelled(true);
                }
                break;

        }
    }

    @EventHandler
    public void onItemDrag(InventoryPickupItemEvent event) {
        if (plugin.configManager.stashMace()) return;
        if (plugin.isCraftedMace(event.getItem().getItemStack()))
            event.setCancelled(true);

    }


    @EventHandler
    public void onItemPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();

        if (plugin.configManager.stashMace()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.DECORATED_POT && !shelves.contains(block.getType()) ) return;
        ItemStack clickedItem;

        if (block.getType() != Material.DECORATED_POT && block.getState().getBlockData() instanceof Powerable powerable) {
            if (powerable.isPowered()) {
                for (int i = 0; i < 9; i++) {
                    clickedItem = player.getInventory().getItem(i);
                    if (clickedItem == null) continue;
                    if (plugin.isCraftedMace(clickedItem)) {
                        event.setCancelled(true);
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Take the mace out of your hotbar"));
                        return;
                    }
                }
            }
        }
        clickedItem = event.getItem();
        if (clickedItem == null) return;
        if (!plugin.isCraftedMace(clickedItem)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemFramePlace(PlayerInteractEntityEvent event) {
        if (plugin.configManager.stashMace()) return;
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        if (plugin.isCraftedMace(event.getPlayer().getInventory().getItemInMainHand()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBundleStash(InventoryClickEvent event) {
        if (plugin.configManager.stashMace()) return;

        ItemStack currentItem = event.getCurrentItem(), cursorItem = event.getCursor();
        if (currentItem == null) return;

        switch (event.getAction()) {
            case InventoryAction.PICKUP_SOME_INTO_BUNDLE, InventoryAction.PICKUP_ALL_INTO_BUNDLE :
                if (plugin.isCraftedMace(currentItem))
                    event.setCancelled(true);
                break;
            case InventoryAction.PLACE_SOME_INTO_BUNDLE, InventoryAction.PLACE_ALL_INTO_BUNDLE :
                if (plugin.isCraftedMace(cursorItem))
                    event.setCancelled(true);



        }

    }

}
