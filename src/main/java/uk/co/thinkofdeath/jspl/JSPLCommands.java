package uk.co.thinkofdeath.jspl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import uk.co.thinkofdeath.parsing.bukkit.validators.HasPermission;
import uk.co.thinkofdeath.command.Command;
import uk.co.thinkofdeath.command.CommandHandler;

import javax.script.ScriptContext;
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
    public void exec(CommandSender sender, String command) {
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
    @HasPermission(value ="jspl.exec.plugin", wildcard = true)
    public void exec(CommandSender sender, String plugin, String command) {
        JavascriptPluginLoader loader = JavascriptPluginLoader.getInstance();
        ScriptContext context = loader.getContext(plugin);
        if (context == null) {
            sender.sendMessage(ChatColor.RED + "Unknown Javascript Plugin: " + plugin);
        }
        Object result = null;
        try {
            result = loader.getEngine().eval(command, context);
        } catch (ScriptException e) {
            sender.sendMessage(ChatColor.RED + "Failed to execute");
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        if (result != null) {
            sender.sendMessage(ChatColor.GREEN + result.toString());
        }
    }
}
