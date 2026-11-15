package me.voper.slimeframe.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.voper.slimeframe.SlimeFrame;
import me.voper.slimeframe.core.managers.SettingsManager;
import me.voper.slimeframe.implementation.listeners.WarnOperatorsListener;

import lombok.Getter;
import org.apache.maven.artifact.versioning.ComparableVersion;

public final class AutoUpdater implements Runnable {

    public static final String LATEST_RELEASE = "https://api.github.com/repos/VoperAD/SlimeFrame/releases/latest";
    public static final String RELEASES = "https://github.com/VoperAD/SlimeFrame/releases";
    public static final String CHANGELOG = "https://github.com/VoperAD/SlimeFrame/blob/main/CHANGELOG.md";

    private final SlimeFrame plugin;
    private final Logger logger;
    private final File updateFolder;
    private final File pluginFile;
    private final String userAgent;

    private static final int TIMEOUT_MS = 10_000;

    private boolean isMajorUpdate = false;

    public AutoUpdater(@Nonnull SlimeFrame plugin, File pluginFile) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.pluginFile = pluginFile;
        this.userAgent = " (" + plugin.getName() + "/" + plugin.getDescription().getVersion() + ", " +
                "ServerVersion/" + plugin.getServer().getVersion() + ", " +
                "BukkitVersion/" + plugin.getServer().getBukkitVersion() +
                ")";
    }

    @Override
    public void run() {
        Release latestRelease = getLatestRelease();
        if (latestRelease == null || !latestRelease.isValid()) {
            plugin.getLogger().severe("Failed to check for updates");
            return;
        }

        if (isLatestVersionNewer(latestRelease.getCleanVersion())) {
            if (isAutoUpdateEnabled()) {
                if (isMajorUpdate && !isAutoUpdateMajorEnabled()) {
                    handleMajorUpdate(latestRelease.getCleanVersion());
                    return;
                }
                info("There is a new version of SlimeFrame available: " + latestRelease.getTagName());
                info("Downloading...");
                downloadUpdate(latestRelease.getDownloadUrl());
            } else {
                info("There is a new version of SlimeFrame available: " + latestRelease.getTagName());
                info("Download it here: " + RELEASES);
            }
        } else {
            info("You are running the latest version");
        }
    }

    private boolean isAutoUpdateEnabled() {
        return SlimeFrame.getSettingsManager().getBoolean(SettingsManager.ConfigField.AUTO_UPDATE);
    }

    private boolean isAutoUpdateMajorEnabled() {
        return SlimeFrame.getSettingsManager().getBoolean(SettingsManager.ConfigField.AUTO_UPDATE_MAJOR);
    }

    private void handleMajorUpdate(String version) {
        logger.warning("There is a new MAJOR version of SlimeFrame available!");
        logger.warning("Download it here: " + RELEASES);
        logger.warning("Check the changelog for more information: " + CHANGELOG);
        // This runs on a separate thread, listeners must be registered on the main thread
        plugin.getServer().getScheduler().runTask(plugin, () ->
                plugin.getServer().getPluginManager().registerEvents(new WarnOperatorsListener(plugin, version), plugin));
    }

    private @Nullable Release getLatestRelease() {
        try {
            URL url = new URL(LATEST_RELEASE);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MS);
            urlConnection.setReadTimeout(TIMEOUT_MS);
            urlConnection.addRequestProperty("User-Agent", userAgent);
            urlConnection.addRequestProperty("Accept", "application/vnd.github+json");

            try (InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                return new Release(jsonElement);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to check for updates: " + e.getMessage());
        }
        return null;
    }

    private boolean isLatestVersionNewer(String latest) {
        ComparableVersion latestVersion = new ComparableVersion(latest);
        ComparableVersion currentVersion = new ComparableVersion(plugin.getDescription().getVersion());

        int latestMajor = parseMajor(latest);
        int currentMajor = parseMajor(plugin.getDescription().getVersion());
        if (latestMajor > currentMajor) {
            isMajorUpdate = true;
            return true;
        }

        return latestVersion.compareTo(currentVersion) > 0;
    }

    // Reads the leading number of a version string ("1.7.0" -> 1, "v12.0" -> 12)
    private static int parseMajor(String version) {
        String digits = version.replaceFirst("^[^0-9]*", "").replaceFirst("[^0-9].*$", "");
        try {
            return digits.isEmpty() ? 0 : Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void downloadUpdate(String downloadUrl) {
        if (!updateFolder.exists() && !updateFolder.mkdirs()) {
            plugin.getLogger().severe("Failed to create the update folder: " + updateFolder.getAbsolutePath());
            return;
        }

        File target = new File(updateFolder, pluginFile.getName());
        // Download to a temporary file first, so a failed download never leaves a corrupt jar behind
        File temp = new File(updateFolder, pluginFile.getName() + ".part");

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.addRequestProperty("User-Agent", userAgent);

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream out = new FileOutputStream(temp)) {
                final byte[] data = new byte[4096];
                int count;
                while ((count = in.read(data, 0, data.length)) != -1) {
                    out.write(data, 0, count);
                }
            }

            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            info("The update has been downloaded and will be installed on the next restart");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to download update: " + e.getMessage());
            if (temp.exists() && !temp.delete()) {
                temp.deleteOnExit();
            }
        }
    }

    private void info(String message) {
        logger.info(message);
    }

    private static class Release {

        @Getter
        private final String tagName;
        private final String jarName;

        public Release(@Nonnull JsonElement element) {
            JsonObject release = element.getAsJsonObject();
            this.tagName = release.has("tag_name") ? release.get("tag_name").getAsString() : null;

            String jar = null;
            if (release.has("assets")) {
                // Pick the first .jar asset instead of blindly taking the first asset
                for (JsonElement asset : release.getAsJsonArray("assets")) {
                    String name = asset.getAsJsonObject().get("name").getAsString();
                    if (name.endsWith(".jar")) {
                        jar = name;
                        break;
                    }
                }
            }
            this.jarName = jar;
        }

        @Nonnull
        public String getDownloadUrl() {
            return "https://github.com/VoperAD/SlimeFrame/releases/download/" + tagName + "/" + jarName;
        }

        @Nonnull
        public String getCleanVersion() {
            return tagName.startsWith("v") ? tagName.substring(1) : tagName;
        }

        public boolean isValid() {
            return tagName != null && jarName != null;
        }

    }

}
