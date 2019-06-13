package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.TicketHub;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private TicketHub plugin;
    private ConversationFactory conversationFactory;

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
                        this.createNewTicket(player);
                        return false;
                    case "allmytickets":
                        this.myTicketsAll(player, args);
                        return false;
                    case "myticket":
                        this.ticketFullDetails(player, args);
                        return false;
                    case "addcomment":
                        this.ticketAddComment(player, args);
                        return false;
                    case "stats":
                        this.statistics(player);
                        return false;
                    case "edit":
                        this.editTicket(player);
                        return false;
                    case "all":
                        this.listAllTickets(player, args);
                        return false;
                    case "filter":
                        this.filterAllTickets(player);
                        return false;
                    case "assigned":
                        this.myAssignedTickets(player, args);
                        return false;
                    case "save":
                        this.saveAllTickets(player, args);
                        return false;
                }
            }

        }
        else{
            commandSender.sendMessage("TicketHub: This command is only supported by players");
        }

        return false;
    }

    /*
    Main command if player only does /th
    Shows all available commands.

    @param player the player who's sending this command
     */
    public void mainCommand(Player player){
        player.sendMessage(ChatColor.AQUA + "TicketHub by arkery");
        player.sendMessage(ChatColor.GREEN + "Commands");
        player.sendMessage(ChatColor.GOLD + "   /th new " + ChatColor.GRAY + "Create a new ticket");
        player.sendMessage(ChatColor.GOLD + "   /th allmytickets " + ChatColor.GRAY + "See all your tickets");
        player.sendMessage(ChatColor.GOLD + "   /th ticketdetails " + ChatColor.GRAY + "See an individual ticket");
        player.sendMessage(ChatColor.GOLD + "   /th addcomment " + ChatColor.GRAY + "Add a comment to a ticket");

        if(player.hasPermission("tickethub.staff")){
            player.sendMessage(ChatColor.GREEN + "Staff Commands");
            player.sendMessage(ChatColor.GOLD + "   /th stats " + ChatColor.GRAY + "Show Ticket Hub statistics");
            player.sendMessage(ChatColor.GOLD + "   /th edit " + ChatColor.GRAY + "Edit a ticket");
            player.sendMessage(ChatColor.GOLD + "   /th all " + ChatColor.GRAY + "Display All Tickets");
            player.sendMessage(ChatColor.GOLD + "   /th filter " + ChatColor.GRAY + "filter all tickets and display them");
            player.sendMessage(ChatColor.GOLD + "   /th assigned " + ChatColor.GRAY + "See all tickets assigned to you");
            player.sendMessage(ChatColor.GOLD + "   /th save " + ChatColor.GRAY + "Save all tickets");
        }
    }


    /*
    Create a new ticket
    - this is usable by everyone

     @param player the player who's sending this command
     */
    public void createNewTicket(Player player){

    }

    /*
    View all tickets belonging to the player who invoked this method
    - usable by everyone

     @param player the player who's sending this command
     @param args   the command input
     */
    public void myTicketsAll(Player player, String[] args){

    }

    /*
    View all details of an individual ticket belonging to the player who invoked this method
    - usable by everyone

    If player has staff permissions, they can access other tickets belonging to other players

     @param player the player who's sending this command
     @param args   the command input
     */
    public void ticketFullDetails(Player player, String[] args){

    }

    /*
    Add a comment to a ticket that belongs to the player who invoked this method
    - usable by everyone

    If player has staff permissions, they can add comments to all tickets

     @param player the player who's sending this command
     @param args   the command input
     */
    public void ticketAddComment(Player player, String[] args){

    }

    /*
    Show current statistics about all tickets saved
    - usable by staff

     @param player the player who's sending this command
     @param args   the command input
     */
    public void statistics(Player player){

    }

    /*
     Show current statistics about all tickets saved
     - usable by staff

      @param player the player who's sending this command
     */
    public void editTicket(Player player){

    }

    /*
    Show current statistics about all tickets saved
    - usable by staff

     @param player the player who's sending this command
     @param args   the command input
    */
    public void listAllTickets(Player player, String[] args){

    }

    /*
    Show current statistics about all tickets saved
    - usable by staff

     @param player the player who's sending this command
     @param args   the command input
    */
    public void filterAllTickets(Player player){

    }

    /*
    Show current statistics about all tickets saved
    - usable by staff

     @param player the player who's sending this command
     @param args   the command input
    */
    public void myAssignedTickets(Player player, String[] args){

    }

    /*
    Show current statistics about all tickets saved
    - usable by staff

     @param player the player who's sending this command
     @param args   the command input
    */
    public void saveAllTickets(Player player, String[] args){
        //If they didn't add a name to save it as
        if(args.length == 1){
            player.sendMessage(ChatColor.DARK_GREEN + "/th save <name>");
            player.sendMessage(ChatColor.DARK_GREEN + "Please enter a name to save the ticket file as");
            return;
        }
        else{
            player.sendMessage(ChatColor.GRAY + "Saving tickets as" + args[1]);
            this.plugin.getTicketSystem().saveTickets(args[1]);
            player.sendMessage(ChatColor.GREEN + "Tickets saved!");
        }
    }





}
