package dev.akyawen.boshyrpc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import dev.akyawen.boshyrpc.util.Difficulties;
import dev.akyawen.boshyrpc.util.Frames;
import dev.akyawen.boshyrpc.util.IniParser;
import dev.akyawen.boshyrpc.util.MemoryReader;
import dev.akyawen.boshyrpc.util.RC4;

public class Main {

    private static final String CLIENT_ID = "1363968302677885140";
    private static int lastFrame = -1;
    private static File saveFile = null;
    private static File lastSaveFile = null;
    private static int deaths;
    private static int difficulty;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("WAITING FOR BOSHY.EXE");

        while (MemoryReader.getProcessId("Boshy.exe") == -1 || MemoryReader.getProcessId("Boshy.exe") == 0) {
            Thread.sleep(1000);
        }

        System.out.println("BOSHY.EXE FOUND, STARTING DISCORD RPC");

        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();

        handlers.ready = () -> System.out.println("DISCORD RPC READY, YOU CAN MINIMIZE THIS WINDOW <3");
        handlers.disconnected = (errorCode, message) -> System.out.println("Disconnected: " + message);
        handlers.errored = (errorCode, message) -> System.out.println("Error: " + message);

        lib.Discord_Initialize(CLIENT_ID, handlers, true, "");

        DiscordRichPresence presence = new DiscordRichPresence();
        presence.largeImageKey = "boshy";
        presence.largeImageText = "I Wanna Be The Boshy";
        presence.smallImageKey = "dark_boshy";
        presence.smallImageText = "Difficulty: Unknown";
        presence.details = "Currently on: null";
        presence.state = "Deaths: 0";
        presence.startTimestamp = System.currentTimeMillis() / 1000L;

        lib.Discord_UpdatePresence(presence);

        Thread callbackThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "RPC-Callback-Handler");
        callbackThread.start();

        while (true) {
            int pid = MemoryReader.getProcessId("Boshy.exe");

            if (pid == -1 || pid == 0) {
                System.out.println("BOSHY.EXE CLOSED, EXITING...");
                break;
            }

            int currentFrame = getCurrentFrame();
            if (currentFrame != lastFrame) {
                lastFrame = currentFrame;
                String frameName = Frames.fromFrameNumber(lastFrame).getName();
                presence.details = "Currently on: " + frameName;
                lib.Discord_UpdatePresence(presence);
            }

            if (lastFrame != 0 && lastFrame != 1 && lastFrame != 2) {
                File currentSaveFile = getCurrentSaveFile();
                if (currentSaveFile != null && !currentSaveFile.equals(lastSaveFile)) {
                    saveFile = currentSaveFile;
                    lastSaveFile = saveFile;

                    try {
                        String saveFileDecrypted = new RC4("BLOB".getBytes()).processFile(saveFile.toPath());
                        IniParser parse = new IniParser(saveFileDecrypted);
                        String deathsString = parse.getValue("Deaths");
                        String difficultyString = parse.getValue("Difficulty");

                        if (deathsString != null && difficultyString != null) {
                            deaths = Integer.parseInt(deathsString);
                            difficulty = Integer.parseInt(difficultyString);
                            presence.state = "Deaths: " + deaths;
                            presence.smallImageText = "Difficulty: " + Difficulties.fromDiffNumber(difficulty).getName();
                            lib.Discord_UpdatePresence(presence);
                        }
                    } catch (IOException | NumberFormatException e) {
                        System.out.println("Ошибка при обработке файла сохранения: " + e.getMessage());
                    }
                }
            } else {
                presence.smallImageText = "Difficulty: Unknown";
                presence.state = "Deaths: 0";
                lib.Discord_UpdatePresence(presence);
            }

            Thread.sleep(1000);
        }

        lib.Discord_Shutdown();
        System.exit(0);
    }

    private static int getCurrentFrame() {
        int pid = MemoryReader.getProcessId("I Wanna Be The Boshy.exe");
        long base = MemoryReader.getModuleBaseAddress(pid, "I Wanna Be The Boshy.exe");
        long pointerBase = base + 0x59A94;
        long finalAddress = MemoryReader.readPointerChain(pid, pointerBase, 0x268, 0xA8);
        if (finalAddress == -1) return -1;
        return MemoryReader.readMemoryInt(pid, finalAddress);
    }

    private static File getCurrentSaveFile() {
        try {
            String path = Paths.get(MemoryReader.getProcessPath(MemoryReader.getProcessId("I Wanna Be The Boshy.exe")))
                    .toFile().getParentFile().toString();
            List<File> saveFiles = getSaveFiles(path);
            return saveFiles.isEmpty() ? null : getLastModifiedSaveFile(saveFiles);
        } catch (Exception e) {
            System.out.println("Ошибка получения пути сохранения: " + e.getMessage());
            return null;
        }
    }

    public static List<File> getSaveFiles(String directoryPath) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.startsWith("SaveFile") && name.endsWith(".ini"));
        return files == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(files));
    }

    public static File getLastModifiedSaveFile(List<File> saveFiles) throws IOException {
        File lastModifiedFile = null;
        long lastModifiedTime = 0;

        for (File saveFile : saveFiles) {
            long modified = Files.readAttributes(saveFile.toPath(), BasicFileAttributes.class)
                    .lastModifiedTime().toMillis();
            if (modified > lastModifiedTime) {
                lastModifiedTime = modified;
                lastModifiedFile = saveFile;
            }
        }

        return lastModifiedFile;
    }
}
