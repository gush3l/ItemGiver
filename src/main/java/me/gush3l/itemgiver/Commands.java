package me.gush3l.itemgiver;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

@SuppressWarnings("all")
public class Commands implements CommandExecutor {

    private FileConfiguration config = ItemGiver.getInstance().getConfig();
    private FileConfiguration itemsStorage = Util.items;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender){
            if (args.length >= 1 && args[0].equals("set") || args[0].equals("create")){
                sender.sendMessage(Util.color(config.getString("Messages.not-for-console")));
                return true;
            }
        }
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length == 2){
                if (args[0].equals("create")){
                    if (!sender.hasPermission("itemgiver.create")){
                        sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                        return true;
                    }
                    if (itemsStorage.contains("Items."+args[1])){
                        player.sendMessage(Util.color(config.getString("Messages.create.already-exists")
                                .replace("%itemName%",args[1])));
                        return true;
                    }
                    ItemStack[] item = new ItemStack[1];
                    item[0] = player.getItemInHand();
                    itemsStorage.set("Items."+args[1],Util.itemStackArrayToBase64(item));
                    Util.itemStorageSave();
                    player.sendMessage(Util.color(config.getString("Messages.create.item-created")
                            .replace("%itemName%",args[1])));
                    return true;
                }
                if (args[0].equals("set")){
                    if (!sender.hasPermission("itemgiver.set")){
                        sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                        return true;
                    }
                    if (!itemsStorage.contains("Items."+args[1])){
                        player.sendMessage(Util.color(config.getString("Messages.set.item-non-existent")
                                .replace("%itemName%",args[1])));
                        return true;
                    }
                    ItemStack[] item = new ItemStack[1];
                    item[0] = player.getItemInHand();
                    itemsStorage.set("Items."+args[1],Util.itemStackArrayToBase64(item));
                    Util.itemStorageSave();
                    player.sendMessage(Util.color(config.getString("Messages.set.item-set")
                            .replace("%itemName%",args[1])));
                    return true;
                }
            }
        }
        if (args.length == 3){
            if (args[0].equals("all")){
                if (!sender.hasPermission("itemgiver.giveall")){
                    sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                    return true;
                }
                String alias = args[1];
                Integer amount = Integer.valueOf(args[2]);
                if (!itemsStorage.contains("Items."+alias)){
                    sender.sendMessage(Util.color(config.getString("Messages.give.item-non-existent")
                            .replace("%itemName%",args[1])));
                    return true;
                }
                try {
                    ItemStack itemStack = Util.itemStackArrayFromBase64(itemsStorage.getString("Items."+alias))[0];
                    for (int i = 0;i<amount;i++){
                        for (Player target : Bukkit.getOnlinePlayers()) target.getInventory().addItem(itemStack);
                    }
                    if (config.getBoolean("Messages.give.send-message-to-target")){
                        for (Player target : Bukkit.getOnlinePlayers()) target.sendMessage(Util.color(config.getString("Messages.give.success-target")
                                .replace("%itemName%",Util.itemName(itemStack))
                                .replace("%itemAmount%",String.valueOf(itemStack.getAmount()*amount))
                                .replace("%itemMaterial%",String.valueOf(itemStack.getType()))));
                    }
                    sender.sendMessage(Util.color(config.getString("Messages.give.success")
                            .replace("%itemName%",Util.itemName(itemStack))
                            .replace("%itemAmount%",String.valueOf(itemStack.getAmount()))
                            .replace("%itemMaterial%",String.valueOf(itemStack.getType()))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            else{
                if (!sender.hasPermission("itemgiver.giveall")){
                    sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                String alias = args[1];
                Integer amount = Integer.valueOf(args[2]);
                if (target == null) {
                    sender.sendMessage(Util.color(this.config.getString("Messages.give.player-not-found")
                            .replace("%player%", args[0])));
                    return true;
                }
                if (!itemsStorage.contains("Items."+alias)){
                    sender.sendMessage(Util.color(config.getString("Messages.give.item-non-existent")
                            .replace("%itemName%",args[1])));
                    return true;
                }
                try {
                    ItemStack itemStack = Util.itemStackArrayFromBase64(itemsStorage.getString("Items."+alias))[0];
                    for (int i = 0;i<amount;i++){
                        target.getInventory().addItem(itemStack);
                    }
                    if (config.getBoolean("Messages.give.send-message-to-target")){
                        target.sendMessage(Util.color(config.getString("Messages.give.success-target")
                                .replace("%itemName%",Util.itemName(itemStack))
                                .replace("%itemAmount%",String.valueOf(itemStack.getAmount()*amount))
                                .replace("%itemMaterial%",String.valueOf(itemStack.getType()))));
                    }
                    sender.sendMessage(Util.color(config.getString("Messages.give.success")
                            .replace("%itemName%",Util.itemName(itemStack))
                            .replace("%itemAmount%",String.valueOf(itemStack.getAmount()))
                            .replace("%itemMaterial%",String.valueOf(itemStack.getType()))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        if (args.length == 1){
            if (args[0].equals("reload")){
                if (!sender.hasPermission("itemgiver.reload")){
                    sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                    return true;
                }
                ItemGiver.getInstance().reloadConfig();
                Util.itemStorageSave();
                sender.sendMessage(Util.color(config.getString("Messages.plugin-reloaded")));
                return true;
            }
            if (args[0].equals("list")){
                if (!sender.hasPermission("itemgiver.list")){
                    sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                    return true;
                }
                ConfigurationSection section = itemsStorage.getConfigurationSection("Items");
                if (section == null){
                    sender.sendMessage(Util.color(config.getString("Messages.list.no-items")));
                    return true;
                }
                String items = String.join(" ",section.getKeys(false));
                sender.sendMessage(Util.color(config.getString("Messages.list.items-list").replace("%itemsList%",items)));
                return true;
            }
            if (args[0].equals("help")){
                if (!sender.hasPermission("itemgiver.help")){
                    sender.sendMessage(Util.color(config.getString("Messages.no-permission")));
                }
                for (String msg : config.getStringList("Messages.help")) sender.sendMessage(Util.color(msg));
                return true;
            }
        }
        sender.sendMessage(Util.color(config.getString("Messages.command-not-found")));
        return true;
    }
}
