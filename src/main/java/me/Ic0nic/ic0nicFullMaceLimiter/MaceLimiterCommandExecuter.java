package me.Ic0nic.ic0nicFullMaceLimiter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

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
                sender.sendMessage("THERE ARE "+plugin.dataManager.getMaceCount()+" MACES");
                break;
            case "setmacecount":
                if (args.length > 2) {
                    plugin.dataManager.setMaceCount(Integer.parseInt(args[1]));
                    sender.sendMessage("Mace count is set to " + Integer.toString(plugin.dataManager.getMaceCount()));
                }
                break;
            case "reload":
                plugin.configManager.reload();
                break;


        }

        return true;
    }

    @Override
    public List<String>  onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
           return List.of("maceCount","setMaceCount", "reload");
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("setmaceCount")) {
                return List.of("<Integer>");
            }
        }
        return List.of();
    }

}
