package com.shadow;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Shadow extends JavaPlugin {

    private NickManager nickManager;

    @Override
    public void onEnable() {
        nickManager = new NickManager(this);

        getCommand("nick").setExecutor(new NickCommand(nickManager));
        getCommand("realnick").setExecutor(new RealNickCommand(nickManager));
        getCommand("skin").setExecutor(new SkinCommand(nickManager));

        getServer().getScheduler().runTaskAsynchronously(this, this::fetchAndLogMinecraftVersion);

        getLogger().info("§d✦︱Shadow Ultra Deluxe Legendary enabled! The ultimate SMP nickname plugin!");
    }

    @Override
    public void onDisable() {
        if (nickManager != null) {
            nickManager.saveAll();
        }
        getLogger().info("✦︱Shadow disabled. All SMP data persisted.");
    }

    private void fetchAndLogMinecraftVersion() {
        try {
            URL url = new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Shadow-Plugin/1.0");

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                JsonObject manifest = JsonParser.parseReader(reader).getAsJsonObject();
                String latest = manifest.getAsJsonObject("latest").get("release").getAsString();
                getLogger().info("✦︱Shadow enabled! Targeting Minecraft version " + latest);
            }

            conn.disconnect();
        } catch (Exception e) {
            getLogger().warning("✦︱Could not resolve latest Minecraft version: " + e.getMessage());
            getLogger().info("✦︱Shadow enabled! Targeting Minecraft version 1.21 (fallback)");
        }
    }

    public NickManager getNickManager() {
        return nickManager;
    }
}
