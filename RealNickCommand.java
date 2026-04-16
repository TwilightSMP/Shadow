package com.shadow;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealNickCommand implements CommandExecutor {

    private static final String PREFIX     = "§5§l✦︱§r";
    private static final String ERR_PREFIX = "§c§l✦︱§r §c";

    private final NickManager nickManager;

    public RealNickCommand(NickManager nickManager) {
        this.nickManager = nickManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ERR_PREFIX + "Console must specify a player: /realnick <player>");
                return true;
            }
            Player self = (Player) sender;
            String original = nickManager.getOriginal(self);
            self.sendMessage(PREFIX + " §7Your true name is §f" + original + "§7.");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            String resolvedOriginal = nickManager.getOriginalByName(targetName);
            if (resolvedOriginal != null) {
                sender.sendMessage(PREFIX + " §7✦︱Original name of §f" + targetName + " §7is §f" + resolvedOriginal + "§7.");
            } else {
                sender.sendMessage(ERR_PREFIX + "No player found for §f" + targetName + "§c. They may be offline or unknown.");
            }
            return true;
        }

        String original = nickManager.getOriginal(target);
        sender.sendMessage(PREFIX + " §7✦︱Original name of §f" + target.getDisplayName() + " §7is §f" + original + "§7.");
        return true;
    }
}
