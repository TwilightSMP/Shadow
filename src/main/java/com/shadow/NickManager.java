package com.shadow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickManager {
    private static final Path NICKNAMES_PATH = Path.of("config/shadow/nicknames.json");
    private static final Path ORIGINALS_PATH = Path.of("config/shadow/originals.json");
    private static final Gson GSON = new Gson();
    private static final Map<UUID, String> nicknames = new HashMap<>();
    private static final Map<UUID, String> originals = new HashMap<>();

    public static void initialize() {
        try {
            if (Files.exists(NICKNAMES_PATH)) {
                Type type = new TypeToken<Map<UUID, String>>() {}.getType();
                nicknames.putAll(GSON.fromJson(Files.readString(NICKNAMES_PATH), type));
            }
            if (Files.exists(ORIGINALS_PATH)) {
                Type type = new TypeToken<Map<UUID, String>>() {}.getType();
                originals.putAll(GSON.fromJson(Files.readString(ORIGINALS_PATH), type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveNicknames() {
        try {
            Files.createDirectories(NICKNAMES_PATH.getParent());
            Files.writeString(NICKNAMES_PATH, GSON.toJson(nicknames));
            Files.writeString(ORIGINALS_PATH, GSON.toJson(originals));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setNickname(ServerPlayerEntity player, String nickname) {
        UUID uuid = player.getUuid();
        nicknames.put(uuid, nickname);
        originals.putIfAbsent(uuid, player.getEntityName());
        applyNickname(player);
    }

    public static void applyNickname(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        String nickname = nicknames.get(uuid);
        if (nickname != null) {
            player.setCustomName(Text.literal(nickname));
            player.setCustomNameVisible(true);
            player.setPlayerListName(Text.literal(nickname));
        }
    }

    public static String getOriginalName(ServerPlayerEntity player) {
        return originals.get(player.getUuid());
    }

    public static String getOriginalName(String nickname) {
        return originals.entrySet().stream()
                .filter(entry -> nicknames.get(entry.getKey()).equals(nickname))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}