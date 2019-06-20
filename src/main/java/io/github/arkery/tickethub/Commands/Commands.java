package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.TicketHub;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Commands implements CommandExecutor {

    private TicketHub plugin;
    private ConversationFactory conversationFactory;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


    public Commands(TicketHub plugin){
        this.plugin = plugin;
        this.conversationFactory = new ConversationFactory(plugin);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            if(args.length == 0){
                this.mainCommand(player);
                return false;
            }
            else{
                switch(args[0].toLowerCase()){
                    case "new":
                        return false;
                    case "mytickets":
                        return false;
                    case "ticketdetails":
                        return false;
                    case "stats":
                        return false;
                    case "edit":
                        return false;
                    case "all":
                        return false;
                    case "filter":
                        return false;
                    case "assigned":
                        return false;
                    case "save":
                        return false;
                    default:
                        this.mainCommand(player);
                        return false;
                }
            }

        }
        else{
            commandSender.sendMessage( "TicketHub: This command is only supported by players");
        }

        return false;
    }

    /**
     * Main command if player only does /th
     * Shows all available commands.
     *
     * @param player the player who's sending this command
     */
    private void mainCommand(Player player){
        if(player.hasPermission("tickethub.player")){
            player.spigot().sendMessage(new Clickable( ChatColor.AQUA,"TicketHub Menu").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   new").add(new Clickable(ChatColor.GOLD, " Create a new ticket").text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   mytickets ").add(new Clickable(ChatColor.GOLD, " See all your tickets").text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   ticketdetails ").add(new Clickable(ChatColor.GOLD, " See individual ticket").text()).text());

            if(player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.AQUA,"Staff Menu").text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   stats").add(new Clickable(ChatColor.GOLD, " See Hub Statistics").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   edit").add(new Clickable(ChatColor.GOLD, " Edit a ticket").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   all").add(new Clickable(ChatColor.GOLD, " See all Tickets").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   filter").add(new Clickable(ChatColor.GOLD, " Filter all Tickets").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   assigned").add(new Clickable(ChatColor.GOLD, " See all tickets assigned to you").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   save").add(new Clickable(ChatColor.GOLD, " Save all tickets manually").text()).text());
            }
        }else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }
}
