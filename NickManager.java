package com.shadow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class NickManager {

    private static final String[] RANDOM_NICK_POOL = {
        "VoidWalker", "Eclipsed", "Phantom", "RavenClaw", "Specter",
        "AbyssKeeper", "Wraith", "DuskBlade", "EmberFang", "FrostVeil",
        "StormBreaker", "Vexis", "Cipher", "NullShift", "Nova",
        "ArcaneOne", "Driftborn", "Gloomfall", "Sable", "TwilightEdge",
        "Reliquary", "Silhouette", "Vantablack", "Nihilis", "Solstice"
    };

    private static final String FLAIR_RELIC   = "✦︱Relic ";
    private static final String FLAIR_CURSED  = "✦︱Cursed ";
    private static final String FLAIR_DEAD    = "✦︱Dead ";

    private final Shadow plugin;
    private final Logger log;
    private final Gson gson;

    private final File nicknamesFile;
    private final File originalsFile;
    private final File skinsFile;
    private final File dataDir;

    private final Map<UUID, String> nicknames  = new HashMap<>();
    private final Map<UUID, String> originals  = new HashMap<>();
    private final Map<UUID, String> playerSkins = new HashMap<>();

    private final List<JsonObject> skinPool = new ArrayList<>();

    public NickManager(Shadow plugin) {
        this.plugin = plugin;
        this.log    = plugin.getLogger();
        this.gson   = new GsonBuilder().setPrettyPrinting().create();

        this.dataDir       = new File(plugin.getDataFolder(), "data");
        this.nicknamesFile = new File(dataDir, "nicknames.json");
        this.originalsFile = new File(dataDir, "originals.json");
        this.skinsFile     = new File(dataDir, "skins.json");

        initDataDirectory();
        load();
    }

    private void initDataDirectory() {
        if (!dataDir.exists()) dataDir.mkdirs();

        createIfAbsent(nicknamesFile, "{}");
        createIfAbsent(originalsFile, "{}");
        createIfAbsent(skinsFile, buildDefaultSkinsJson());
    }

    private void createIfAbsent(File file, String defaultContent) {
        if (!file.exists()) {
            try (FileWriter w = new FileWriter(file)) {
                w.write(defaultContent);
            } catch (IOException e) {
                log.warning("✦︱Failed to create " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private String buildDefaultSkinsJson() {
        return "{\n" +
            "  \"random_skins\": [\n" +
            "    {\"name\": \"Shadow Wanderer\",  \"url\": \"https://textures.minecraft.net/texture/abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890\"},\n" +
            "    {\"name\": \"Solar Knight\",      \"url\": \"https://textures.minecraft.net/texture/123456abcdef123456abcdef123456abcdef123456abcdef123456abcdef123456\"},\n" +
            "    {\"name\": \"Twilight Specter\",  \"url\": \"https://textures.minecraft.net/texture/fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210\"},\n" +
            "    {\"name\": \"Golden Sentinel\",   \"url\": \"https://textures.minecraft.net/texture/0f1e2d3c4b5a69788766554433221100aabbccddeeff00112233445566778899\"},\n" +
            "    {\"name\": \"Lunar Phantom\",     \"url\": \"https://textures.minecraft.net/texture/11223344556677889900aabbccddeeff00112233445566778899aabbccddeeff\"}\n" +
            "  ]\n" +
            "}";
    }

    private void load() {
        loadStringMap(nicknamesFile, nicknames);
        loadStringMap(originalsFile, originals);
        loadSkinPool();
    }

    private void loadStringMap(File file, Map<UUID, String> target) {
        if (!file.exists()) return;
        try (FileReader r = new FileReader(file)) {
            JsonObject obj = JsonParser.parseReader(r).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                target.put(UUID.fromString(entry.getKey()), entry.getValue().getAsString());
            }
        } catch (Exception e) {
            log.warning("✦︱Failed to load " + file.getName() + ": " + e.getMessage());
        }
    }

    private void loadSkinPool() {
        skinPool.clear();
        if (!skinsFile.exists()) return;
        try (FileReader r = new FileReader(skinsFile)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            JsonArray arr = root.getAsJsonArray("random_skins");
            for (JsonElement el : arr) {
                skinPool.add(el.getAsJsonObject());
            }
        } catch (Exception e) {
            log.warning("✦︱Failed to load skins.json: " + e.getMessage());
        }
    }

    public void saveAll() {
        saveMap(nicknamesFile, nicknames);
        saveMap(originalsFile, originals);
    }

    private void saveMap(File file, Map<UUID, String> source) {
        JsonObject obj = new JsonObject();
        source.forEach((uuid, val) -> obj.addProperty(uuid.toString(), val));
        try (FileWriter w = new FileWriter(file)) {
            gson.toJson(obj, w);
        } catch (IOException e) {
            log.warning("✦︱Failed to save " + file.getName() + ": " + e.getMessage());
        }
    }

    public void setNick(Player player, String rawNick) {
        originals.putIfAbsent(player.getUniqueId(), player.getName());

        String flair = resolveFlair(player);
        String colorized = colorize(rawNick);
        String display = flair + colorized;

        nicknames.put(player.getUniqueId(), rawNick);
        player.setDisplayName(display);
        player.setPlayerListName(display);

        saveMap(nicknamesFile, nicknames);
        saveMap(originalsFile, originals);
    }

    public void setRandomNick(Player player) {
        String base = RANDOM_NICK_POOL[(int) (Math.random() * RANDOM_NICK_POOL.length)];
        setNick(player, base);
    }

    public String getNick(Player player) {
        return nicknames.getOrDefault(player.getUniqueId(), player.getName());
    }

    public String getOriginal(Player player) {
        return originals.getOrDefault(player.getUniqueId(), player.getName());
    }

    public String getOriginalByName(String name) {
        for (Map.Entry<UUID, String> entry : originals.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null && nicknames.getOrDefault(entry.getKey(), "").equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        Player direct = Bukkit.getPlayerExact(name);
        if (direct != null) return originals.getOrDefault(direct.getUniqueId(), direct.getName());
        return null;
    }

    public boolean setRandomSkin(Player player) {
        if (skinPool.isEmpty()) return false;
        JsonObject skin = skinPool.get((int) (Math.random() * skinPool.size()));
        String skinName = skin.get("name").getAsString();
        String skinUrl  = skin.get("url").getAsString();
        return applySkin(player, skinName, skinUrl);
    }

    public boolean applySkin(Player player, String skinName, String skinUrl) {
        try {
            PlayerProfile profile = player.getPlayerProfile();
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(skinUrl));
            profile.setTextures(textures);
            player.setPlayerProfile(profile);

            playerSkins.put(player.getUniqueId(), skinName);
            refreshPlayerForAll(player);
            return true;
        } catch (Exception e) {
            log.warning("✦︱Failed to apply skin for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    private void refreshPlayerForAll(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (!viewer.equals(player)) {
                    viewer.hidePlayer(plugin, player);
                    viewer.showPlayer(plugin, player);
                }
            }
            player.teleport(player.getLocation());
        });
    }

    public String getSkinName(Player player) {
        return playerSkins.getOrDefault(player.getUniqueId(), "Default");
    }

    private String resolveFlair(Player player) {
        if (player.hasPermission("shadow.role.relic"))  return colorize("&5") + FLAIR_RELIC;
        if (player.hasPermission("shadow.role.cursed")) return colorize("&4") + FLAIR_CURSED;
        if (player.hasPermission("shadow.role.dead"))   return colorize("&8") + FLAIR_DEAD;
        return "";
    }

    public static String colorize(String text) {
        return text.replace('&', '§');
    }

    public List<JsonObject> getSkinPool() {
        return skinPool;
    }
}
