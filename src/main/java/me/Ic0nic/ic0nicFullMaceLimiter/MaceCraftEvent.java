package me.Ic0nic.ic0nicFullMaceLimiter;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;

import java.util.UUID;


public class MaceCraftEvent implements Listener {
    private final Ic0nicFullMaceLimiter plugin;

    private final Location location;
    private final World world;
    private int time;
    private final String name;
    private final float orgTime;
    private BossBar bossBar;
    private BukkitTask task;
    private final String uuid;
    private ItemDisplay display;
    public MaceCraftEvent(Ic0nicFullMaceLimiter plugin, Player player) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        location = player.getLocation();
        time = plugin.configManager.maceCraftEventTime();

        orgTime = time;
        world = location.getWorld();
        this.name = player.getName();
        this.uuid = UUID.randomUUID().toString();
        this.start();
    }

    public MaceCraftEvent(Ic0nicFullMaceLimiter plugin, String nameP, Location loc, int timeP, int orgTimeP, String uuidP) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        location = loc;
        time = timeP;
        orgTime = orgTimeP;
        world = location.getWorld();
        this.name = nameP;
        this.uuid = uuidP;
        this.start();

    }

    public static void broadCastCraftedMace(Ic0nicFullMaceLimiter plugin, String name, Location loc) {
        String title = name+ "<#676767>'s Crafted the <light_purple>Mace</light_purple> at</#676767> ";
        switch (loc.getWorld().getEnvironment()) {
            case World.Environment.NORMAL -> title += "<green>";
            case World.Environment.NETHER -> title += "<red>";
            case World.Environment.THE_END -> title += "<#FFFDA8>";
        }


        title += loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
        broadCastCraftedMace(plugin,MiniMessage.miniMessage().deserialize(title));
    }
    public static void broadCastCraftedMace(Ic0nicFullMaceLimiter plugin, Component msg) {
        plugin.getServer().sendMessage(msg);
    }
    private void start() {
        BossBar.Color color = BossBar.Color.WHITE;
        String title = name + "<#676767>'s Crafting the <light_purple>Mace</light_purple> at</#676767> ";
        switch (world.getEnvironment()) {
            case World.Environment.NORMAL:
                color = BossBar.Color.GREEN;
                title += "<green>";
                break;
            case World.Environment.NETHER: color = BossBar.Color.RED;
                title += "<red>";
                break;
            case World.Environment.THE_END: color = BossBar.Color.YELLOW;
                title += "<#FFFDA8>";
                break;
        }

        title += location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();

        bossBar = BossBar.bossBar(MiniMessage.miniMessage().deserialize(title),1,color,BossBar.Overlay.PROGRESS);
        broadCastCraftedMace(plugin, bossBar.name() );
        for (Player p :  Bukkit.getOnlinePlayers())
            bossBar.addViewer(p);


        recreateDisplayItem();

        task = Bukkit.getScheduler().runTaskTimer(this.plugin,() -> {
            time--;

            if (time < 0) {
                task.cancel();

                for (Player p :  Bukkit.getOnlinePlayers())
                    bossBar.removeViewer(p);

                ItemStack item = new ItemStack(Material.MACE);
                plugin.markCraftedMace(item);
                world.dropItemNaturally(location.add(0,1,0), item);
                plugin.dataManager.removeMaceCraftEvent(this);
                PlayerJoinEvent.getHandlerList().unregister(this);
                for (Entity entity : world.getChunkAt(location).getEntities()) {
                    if (entity instanceof ItemDisplay itemDisplay) {
                        String duuid = itemDisplay.getPersistentDataContainer().get(plugin.eventItemDisplayKey, PersistentDataType.STRING);
                        if (duuid == null) continue;
                        if (!duuid.equals(uuid)) continue;
                        itemDisplay.remove();
                    }
                }
                plugin.getServer().sendMessage(MiniMessage.miniMessage().deserialize("<light_purple><bold>MACE HAS BEEN CRAFTED"));
                playGlobalSound(Sound.ENTITY_ENDER_DRAGON_DEATH);

                return;
            }
            float progress = time/orgTime;
            bossBar.progress(Math.clamp(progress,0,1));

            double radius = progress*10;
            double angle=0;
            for (int i=0; i<40; i++) {
                angle += (2 *Math.PI)/40;
                world.spawnParticle(Particle.END_ROD,
                        location.getX() + Math.cos(angle)*radius,
                        location.getY(),
                        location.getZ() + Math.sin(angle) *radius
                        ,1,0,0,0,0.02);
            }

        },0L,1L);
    }

    public void recreateDisplayItem() {
        display = world.spawn(location.clone().add(0,2,0),
                ItemDisplay.class, entity -> {
                    entity.setItemStack(new ItemStack(Material.MACE));
                    entity.setBillboard(Display.Billboard.CENTER);
                    Transformation transformation = entity.getTransformation();
                    transformation.getScale().set(2.5f,2.5f,2.5f);
                    entity.setTransformation(transformation);
                    entity.getPersistentDataContainer().set(plugin.eventItemDisplayKey, PersistentDataType.STRING,uuid);

                });
        display.setGlowing(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        bossBar.addViewer(e.getPlayer());
    }

    public Location  getLocation() {
        return location;
    }
    public int getTime() {return time;}
    public float getOrgTime() {return orgTime;}
    public String getName() {return name;}
    public String getUUID() {return uuid;}

    public static void playGlobalSound(Sound sound) {
        for (Player p : Bukkit.getOnlinePlayers())
            p.playSound(p.getLocation(), sound,1,1);

    }
}
