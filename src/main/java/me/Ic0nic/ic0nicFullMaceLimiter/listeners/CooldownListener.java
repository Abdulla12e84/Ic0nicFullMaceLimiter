package me.Ic0nic.ic0nicFullMaceLimiter.listeners;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.Ic0nic.ic0nicFullMaceLimiter.Ic0nicFullMaceLimiter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CooldownListener implements Listener {
    private final Ic0nicFullMaceLimiter plugin;

    private final HashMap<Player, AtomicInteger> rightClickingPlayers = new HashMap<>();

    public CooldownListener(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {

            if (!plugin.configManager.cooldownEnabled())
                return;


            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            if (plugin.isCraftedMace(mainHandItem))
                if (player.hasCooldown(Material.MACE))
                    event.setCancelled(true);

        }

    }


    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onPlayerHitMonitor(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player player) {
            if (!plugin.configManager.cooldownEnabled())
                return;

            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            if (plugin.isCraftedMace(mainHandItem)) {
                if (player.hasCooldown(Material.MACE))
                    return;


                if (plugin.configManager.forgiveShieldHit())
                    if (event.getEntity() instanceof Player victim) {
                        if (victim.isBlocking() && event.getFinalDamage() == 0) {
                            return;
                        }
                    }

                player.setCooldown(Material.MACE, plugin.configManager.cooldownTime());
            }
        }

    }

    @EventHandler
    public void onPlayerSwing(PlayerArmSwingEvent event) {
        if (!plugin.configManager.punishMissing()) return;
        if (plugin.isCraftedMace(event.getPlayer().getInventory().getItemInMainHand())) {
            AtomicInteger rightClicking = rightClickingPlayers.remove(event.getPlayer());
            if ( rightClicking != null && rightClicking.get() == Bukkit.getCurrentTick()) return;
            if (event.getPlayer().hasCooldown(Material.MACE)) return;
            event.getPlayer().setCooldown(Material.MACE, plugin.configManager.cooldownTime());
        }
    }

    @EventHandler
    public void onItemPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) return;
        rightClickingPlayers.put(event.getPlayer(),new AtomicInteger(Bukkit.getCurrentTick()));

    }


    @EventHandler
    public void onPlayerPreAttack(PrePlayerAttackEntityEvent event) {
        if (!plugin.configManager.cooldownEnabled()) return;
        Player player = event.getPlayer();

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (plugin.isCraftedMace(mainHandItem)) {
            if (player.hasCooldown(Material.MACE)) {
                event.setCancelled(true);

            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        switch (event.getAction()) {
            case InventoryAction.DROP_ALL_CURSOR, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ONE_SLOT :
                rightClickingPlayers.put((Player) (event.getWhoClicked()),new AtomicInteger(Bukkit.getCurrentTick()));
                break;
        }
    }
}

