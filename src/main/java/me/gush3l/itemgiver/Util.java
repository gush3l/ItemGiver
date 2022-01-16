package me.gush3l.itemgiver;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Util {

    public static File itemsFile;
    public static FileConfiguration items;

    public static String color(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void createItemsStorage() {
        itemsFile = new File(ItemGiver.getInstance().getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            itemsFile.getParentFile().mkdirs();
            ItemGiver.getInstance().saveResource("items.yml", false);
        }

        items = new YamlConfiguration();
        try {
            log("Loading items storage file...",Level.INFO);
            items.load(itemsFile);
            log("Items storage file loaded successfully!",Level.INFO);
        } catch (IOException | InvalidConfigurationException e) {
            log("Failed loading items storage file!",Level.SEVERE);
            e.printStackTrace();
        }
    }

    public static void itemStorageSave(){
        try{
            items.save(itemsFile);
            items.load(itemsFile);
        }catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }

    public static String itemName(ItemStack itemStack){
        String itemName = "";
        if (itemStack.hasItemMeta()){
            if (itemStack.getItemMeta().hasDisplayName()) itemName = itemStack.getItemMeta().getDisplayName();
        }
        else{
            String itemMaterial = String.valueOf(itemStack.getType()).replace("_"," ");
            itemName = capitalizeFully(itemMaterial, null);
        }
        return itemName;
    }

    public static void log(String message, Level level){
        ItemGiver.getInstance().getLogger().log(level,message);
    }

    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuilder buffer = new StringBuilder(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (isDelimiter(ch, delimiters)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    public static String capitalizeFully(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        str = str.toLowerCase();
        return capitalize(str, delimiters);
    }

}
