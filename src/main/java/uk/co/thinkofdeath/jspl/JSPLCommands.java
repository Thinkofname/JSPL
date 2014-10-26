package uk.co.thinkofdeath.jspl;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import uk.co.thinkofdeath.command.Command;
import uk.co.thinkofdeath.command.CommandHandler;
import uk.co.thinkofdeath.parsing.bukkit.validators.HasPermission;

import javax.script.ScriptException;

public class JSPLCommands implements CommandHandler {
    private final JSPLPlugin jsplPlugin;

    public JSPLCommands(JSPLPlugin jsplPlugin) {
        this.jsplPlugin = jsplPlugin;
    }

    @Command("jspl")
    @HasPermission(value = "jspl", wildcard = true)
    public void help(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "JSPL Help:");
        sender.sendMessage(ChatColor.AQUA + "/jspl exec global <command>" +
            "  - Execute javascript in the global scope");
        sender.sendMessage(ChatColor.AQUA + "/jspl exec <plugin> <command>" +
            "  - Execute javascript in the plugin's scope");
    }

    @Command("jspl exec global ?")
    @HasPermission(value = "jspl.exec.global", wildcard = true)
    public void exec(CommandSender sender, String... cmds) {
        String command = Joiner.on(' ').join(cmds);
        JavascriptPluginLoader loader = JavascriptPluginLoader.getInstance();
        Object result = null;
        try {
            result = loader.getEngine().eval(command, loader.getGlobal());
        } catch (ScriptException e) {
            sender.sendMessage(ChatColor.RED + "Failed to execute");
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        if (result != null) {
            sender.sendMessage(ChatColor.GREEN + result.toString());
        }
    }

    @Command("jspl exec ? ?")
    @HasPermission(value = "jspl.exec.plugin", wildcard = true)
    public void exec(CommandSender sender, String plugin, String... cmds) {
        String command = Joiner.on(' ').join(cmds);
        Plugin p = jsplPlugin.getServer().getPluginManager().getPlugin(plugin);
        if (p == null) {
            for (Plugin pl : jsplPlugin.getServer().getPluginManager().getPlugins()) {
                if (pl.getName().equalsIgnoreCase(plugin)) {
                    p = pl;
                    break;
                }
            }
        }
        if (p == null || !(p instanceof JSPlugin)) {
            sender.sendMessage(ChatColor.RED + "Unknown Javascript Plugin: " + plugin);
            return;
        }
        JSPlugin jsPlugin = (JSPlugin) p;
        Object result = null;
        try {
            result = jsPlugin.getLoader().getEngine().eval(command, jsPlugin.getContext());
        } catch (ScriptException e) {
            sender.sendMessage(ChatColor.RED + "Failed to execute");
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        if (result != null) {
            sender.sendMessage(ChatColor.GREEN + result.toString());
        }
    }
}
