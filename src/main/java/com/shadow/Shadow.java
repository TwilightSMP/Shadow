package com.shadow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class Shadow implements ModInitializer {
    @Override
    public void onInitialize() {
        NickManager.initialize();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            NickCommand.register(dispatcher);
            RealNickCommand.register(dispatcher);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            NickManager.applyNickname(handler.getPlayer());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            NickManager.saveNicknames();
        });
    }
}