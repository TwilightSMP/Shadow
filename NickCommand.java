package com.shadow;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {

    private static final int MAX_NICK_LENGTH = 16;

    private static final String PREFIX        = "§5§l✦︱§r";
    private static final String ERR_PREFIX    = "§c§l✦︱§r §c";
    private static final String BROADCAST_FMT = PREFIX + "§7%s §fis now §d%s§f.";
    private static final String RANDOM_FMT    = PREFIX + "§7%s §fhas embraced the identity of §d%s§f.";

    private final NickManager nickManager;

    public NickCommand(NickManager nickManager) {
        this.nickManager = nickManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ERR_PREFIX + "Only players can wield this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ERR_PREFIX + "Usage: /nick <name|random>");
            return true;
        }

        if (args[0].equalsIgnoreCase("random")) {
            handleRandom(player);
            return true;
        }

        String rawNick = args[0];

        if (rawNick.length() > MAX_NICK_LENGTH) {
            player.sendMessage(ERR_PREFIX + "Nickname too long. Maximum §f" + MAX_NICK_LENGTH + " §ccharacters.");
            return true;
        }

        if (!rawNick.matches("[a-zA-Z0-9&_\\-]+")) {
            player.sendMessage(ERR_PREFIX + "Invalid nickname. Use letters, numbers, & color codes, _ or -.");
            return true;
        }

        nickManager.setNick(player, rawNick);

        String displayedNick = player.getDisplayName();
        String broadcast = String.format(BROADCAST_FMT, player.getName(), displayedNick);
        Bukkit.broadcastMessage(broadcast);

        return true;
    }

    private void handleRandom(Player player) {
        nickManager.setRandomNick(player);
        String displayedNick = player.getDisplayName();
        String broadcast = String.format(RANDOM_FMT, player.getName(), displayedNick);
        Bukkit.broadcastMessage(broadcast);
    }
}
