package com.milesacq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.server.MapInitializeEvent;

import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
public class ScavengerHuntMaven extends JavaPlugin implements Listener, CommandExecutor {
    private final AdvancementNames[] gameAdvancements = new AdvancementNames[11];

    public AdvancementNames[] getAdvancements() {
        return this.gameAdvancements;
    }

    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked! Building advancement list");
        Iterator<Advancement> iter = Bukkit.getServer().advancementIterator();
        while (iter.hasNext()) {
            Advancement adv = iter.next();
            String caseKey = adv.getKey().toString();
            switch (caseKey) {
                case "minecraft:husbandry/wax_on" -> makeAdv(0, adv, "+ Wax On");
                case "minecraft:husbandry/wax_off" -> makeAdv(1, adv, "+ Wax Off");
                case "minecraft:husbandry/ride_a_boat_with_a_goat" -> makeAdv(2, adv, "+ Whatever Floats Your Goat");
                case "minecraft:husbandry/axolotl_in_a_bucket" -> makeAdv(3, adv, "+ The Cutest Predator");
                case "minecraft:husbandry/kill_axolotl_target" -> makeAdv(4, adv, "+ The Healing Power of Friendship");
                case "minecraft:husbandry/make_a_sign_glow" -> makeAdv(5, adv, "+ Glow and Behold");
                case "minecraft:adventure/walk_on_powder_snow_with_leather_boots" -> makeAdv(6, adv, "+ Light as a Rabbit");
                case "minecraft:adventure/lightning_rod_with_villager_no_fire" -> makeAdv(7, adv, "+ Surge Protector!");
                case "minecraft:adventure/spyglass_at_parrot" -> makeAdv(8, adv, "+ Is it a Bird?");
                case "minecraft:adventure/spyglass_at_ghast" -> makeAdv(9, adv, "+ Is it a Balloon?");
                case "minecraft:adventure/spyglass_at_dragon" -> makeAdv(10, adv, "+ Is it a Plane?");
            }
        }
        for (AdvancementNames gameAdvancement : this.gameAdvancements) {
            getLogger().info(gameAdvancement.getName() + "");
        }
        String location = "plugins" + File.separator + "huntSaves" + File.separator;
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            try {
                File saveFile = new File("plugins" + File.separator + "huntSaves" + File.separator);
                saveFile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void makeAdv(int i, Advancement adv, String name) {
        AdvancementNames advance = new AdvancementNames();
        this.gameAdvancements[i] = advance;
        this.gameAdvancements[i].setAdvancement(adv);
        this.gameAdvancements[i].setName(name);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hunt") && args.length == 2) {
            SHPlayer bPlayer = new SHPlayer();
            bPlayer.setPlayerName(Bukkit.getPlayer(args[1]));
            String location = bPlayer.getFileName();
            Path path = Paths.get(location);
            if (bPlayer.getPlayer() == null) {
                getLogger().info(args[1] + " is not currently online.");
                return false;
            }
            if ((Files.exists(path)) && args[0].equalsIgnoreCase("check")) {
                String[] progress = bPlayer.getAchievementProgress(path);
                bPlayer.getPlayer().sendMessage(progress[0] +  progress[1] + progress[2] + progress[3] + progress[4]);
                bPlayer.getPlayer().sendMessage(progress[5] +  progress[6] + progress[7] + progress[8] + progress[9]);
                bPlayer.getPlayer().sendMessage(progress[10] +  progress[11]);

                String fileContent;
                return true;
            } else if (args[0].equalsIgnoreCase("add")) {
                if (Files.exists(path)) {
                    bPlayer.getPlayer().sendMessage("Already in registry");
                    getLogger().info("Already in registry");
                    return false;
                }
                createNewSave(bPlayer);
                ItemStack newMap = new ItemStack(Material.MAP);
                bPlayer.getPlayer().getInventory().addItem(newMap);
                return true;
            } else {
                bPlayer.getPlayer().sendMessage("Please add player to registry");
                getLogger().info("Please add player to registry");
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void finishAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement targetAdv = event.getAdvancement();
        AdvancementProgress avp = event.getPlayer().getAdvancementProgress(targetAdv);
        SHPlayer bPlayer = new SHPlayer();
        bPlayer.setPlayerName(event.getPlayer());
        String location = bPlayer.getFileName();
        Path path = Paths.get(location);
        if (Files.exists(path)) {
            if (avp != null && avp.isDone() && containsAdvancement(targetAdv)) {
                int achIndex = 1000;
                for (int i = 0; i < this.gameAdvancements.length; i++) {
                    if (this.gameAdvancements[i].getAdvancement() == targetAdv) {
                        achIndex = i;
                        break;
                    }
                }
                try {
                    String fileContent = Files.readString(path);
                    FileWriter myWriter = new FileWriter(location);
                    fileContent = fileContent.substring(0, achIndex) + 't' + fileContent.substring(achIndex + 1);
                    myWriter.write(fileContent);
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                getLogger().info("incompatible advancement");
            }
        } else {
            getLogger().info("player isnt registered");
        }
    }
    public Boolean containsAdvancement(Advancement advancement) {
        for (int i = 0; i < this.gameAdvancements.length; i++) {
            if ( this.gameAdvancements[i].getAdvancement() == advancement) {
                return true;
            }
        }
        return false;
    }
    public void createNewSave(SHPlayer player) {
        String name = player.getFileName();
        Path path = Paths.get(name);
        if (!Files.exists(path)) {
            File myObj = new File(name);
            try {
                System.out.println("get");
                myObj.createNewFile();
                getLogger().info("File created: " + myObj.getName());
                FileWriter myWriter = new FileWriter(name);
                myWriter.write("fffffffffff");
                myWriter.flush();
                myWriter.close();
                getLogger().info("Successfully added player!");
                player.getPlayer().sendMessage("Sucessfully added player " + player.getPlayerName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File already exists.");
        }
    }
}