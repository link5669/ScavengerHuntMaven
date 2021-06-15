package com.milesacq;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
public class SHPlayer {

    private Player player;
    private final Boolean[] achievementsFinished = new Boolean[11];

    public void setPlayerName(Player p) {
        this.player = p;
        for (int i = 0; i < this.achievementsFinished.length; i++) {
            achievementsFinished[i] = false;
        }
    }

    public void setAchievement(int place, Boolean bool) {
        this.achievementsFinished[place] = bool;
    }

    public String getPlayerName() {
        return this.player.getName();
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getFileName() {
        return "plugins" + File.separator + "huntSaves" + File.separator + this.getPlayerName() + ".txt";
    }

    public String[] getAchievementProgress(Path path) {
        try {
            String fileContent = Files.readString(path);
            for (int i = 0; i < fileContent.length(); i++) {
                if (fileContent.charAt(i) == 't') {
                    achievementsFinished[i] = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] stringArray = new String[11];
        for (int i = 0; i < stringArray.length; i++) {
            if (!achievementsFinished[i]) {
                stringArray[i] = ChatColor.RED + "[x]" + ChatColor.RESET;
            } else {
                stringArray[i] = ChatColor.GREEN + "[✓]" + ChatColor.RESET;
            }
        }
        return stringArray;
    }
}