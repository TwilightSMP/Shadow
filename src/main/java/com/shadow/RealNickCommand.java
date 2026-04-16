package com.shadow;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RealNickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("realnick")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    String originalName = NickManager.getOriginalName(player);
                    context.getSource().sendFeedback(Text.literal("§5✦︱§r Your original name is " + originalName), false);
                    return 1;
                })
                .then(argument("nickname", StringArgumentType.word())
                        .executes(context -> {
                            String nickname = StringArgumentType.getString(context, "nickname");
                            String originalName = NickManager.getOriginalName(nickname);
                            if (originalName != null) {
                                context.getSource().sendFeedback(Text.literal("§5✦︱§r " + nickname + "'s original name is " + originalName), false);
                                return 1;
                            } else {
                                context.getSource().sendError(Text.literal("§5✦︱§r No player found with that nickname!"));
                                return 0;
                            }
                        })));
    }
}