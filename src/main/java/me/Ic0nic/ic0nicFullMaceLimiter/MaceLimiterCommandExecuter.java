package me.Ic0nic.ic0nicFullMaceLimiter;


import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MaceLimiterCommandExecuter implements CommandExecutor, TabCompleter {
    private final Ic0nicFullMaceLimiter plugin;

    public MaceLimiterCommandExecuter(Ic0nicFullMaceLimiter plugin) {
        this.plugin = plugin;
        plugin.getCommand("iconicFML").setExecutor(this);
        plugin.getCommand("iconicFML").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (args[0].toLowerCase()) {
            case "macecount":
                if (sender.hasPermission("iconicfml.view"))
                    sender.sendMessage("THERE ARE "+plugin.dataManager.getMaceCount()+" MACES");
                else
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command."));
                break;
            case "setmacecount":
                if (sender.hasPermission("iconicfml.set")) {
                    if (args.length >= 2) {
                        plugin.dataManager.setMaceCount(Integer.parseInt(args[1]));
                        sender.sendMessage("Mace count is set to " + Integer.toString(plugin.dataManager.getMaceCount()));
                    }
                }else
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command."));
                break;
            case "reload":
                if (sender.hasPermission("iconicfml.reload")) {
                    plugin.configManager.reload();
                    sender.sendMessage("Reloaded config");
                } else
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to use this command."));
                break;


        }

        return true;
    }

    @Override
    public List<String>  onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("iconicfml.reload"))
                list.add("reload");
            if (sender.hasPermission("iconicfml.set"))
                list.add("setMaceCount");
            if (sender.hasPermission("iconicfml.view"))
                list.add("maceCount");
           return list;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("setmaceCount") && sender.hasPermission("iconicfml.set")) {
                return List.of("<Integer>");
            }
        }
        return list;
    }

}
