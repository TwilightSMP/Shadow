package com.shadow;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NickCommand {
    private static final List<String> RANDOM_NAMES = List.of(
            "Shadowblade", "Nightfall", "Eclipse", "Starlight", "Moonshadow",
            "Darkfang", "Silverclaw", "Stormrider", "Frostbane", "Emberflame",
            "Duskwing", "Brightspark", "Ironheart", "Goldenshield", "Swiftarrow",
            "Thunderstrike", "Windwhisper", "Flamecaster", "Crystalshade", "Voidwalker",
            "Starborn", "Lunaris", "Solaris", "Mystic", "Phantom"
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("nick")
                .then(argument("nickname", StringArgumentType.word())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            String nickname = StringArgumentType.getString(context, "nickname");
                            if (nickname.matches("[a-zA-Z0-9_-]{1,16}")) {
                                NickManager.setNickname(player, nickname);
                                context.getSource().getServer().getPlayerManager()
                                        .broadcast(Text.literal("§5✦︱§r " + player.getEntityName() + " is now known as " + nickname), false);
                                return 1;
                            } else {
                                context.getSource().sendError(Text.literal("§5✦︱§r Invalid nickname!"));
                                return 0;
                            }
                        }))
                .then(literal("random")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            String randomNick = RANDOM_NAMES.get((int) (Math.random() * RANDOM_NAMES.size()));
                            NickManager.setNickname(player, randomNick);
                            context.getSource().getServer().getPlayerManager()
                                    .broadcast(Text.literal("§5✦︱§r " + player.getEntityName() + " is now known as " + randomNick), false);
                            return 1;
                        })));
    }
}