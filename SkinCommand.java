package com.shadow;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandExecutor {

    private static final String PREFIX     = "§5§l✦︱§r";
    private static final String ERR_PREFIX = "§c§l✦︱§r §c";
    private static final String BROADCAST  = PREFIX + "§7%s §fhas adopted the §d%s§f appearance!";

    private final NickManager nickManager;

    public SkinCommand(NickManager nickManager) {
        this.nickManager = nickManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ERR_PREFIX + "Only players can shift their appearance.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || !args[0].equalsIgnoreCase("random")) {
            player.sendMessage(ERR_PREFIX + "Usage: /skin random");
            return true;
        }

        if (nickManager.getSkinPool().isEmpty()) {
            player.sendMessage(ERR_PREFIX + "No skins are available in skins.json.");
            return true;
        }

        boolean applied = nickManager.setRandomSkin(player);

        if (!applied) {
            player.sendMessage(ERR_PREFIX + "Failed to apply skin. Check that skins.json contains valid entries.");
            return true;
        }

        String skinName = nickManager.getSkinName(player);
        String broadcast = String.format(BROADCAST, player.getName(), skinName);
        Bukkit.broadcastMessage(broadcast);

        return true;
    }
}
