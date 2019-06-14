package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.Commands.NewTicketConv.titleNewTicket;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
                        this.createNewTicket(player);
                        return false;
                    case "allmytickets":
                        this.myTicketsAll(player, args);
                        return false;
                    case "ticketdetails":
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

     /**
     *  Create a new ticket  - this is usable by everyone
     *  /th new
     *
     * @param player the player who's sending this command
     */
    public void createNewTicket(Player player){
        if(player.hasPermission("tickethub.player")){
            Conversation conv = conversationFactory
                    .withFirstPrompt(new titleNewTicket(plugin))
                    .withLocalEcho(false)
                    .withEscapeSequence("cancel")
                    .withTimeout(120)
                    .buildConversation(player);
            conv.begin();
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * View all tickets belonging to the player who invoked this method - usable by everyone
     * /th allmytickets <page> <created/updated>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    public void myTicketsAll(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                //Check if player has tickets
                if(!plugin.getTicketSystem().getStoredTickets().getAllTickets().containsKey(player.getUniqueId())){
                    player.sendMessage(ChatColor.RED + "You have no tickets!");
                    return;
                }

                List<Ticket> playerTickets = plugin.getTicketSystem().getStoredTickets().getAllTickets().get(player.getUniqueId());

                //Check if player stated if they wanted it sorted by date created
                if(args.length == 3 && args[2].equalsIgnoreCase("created")){
                    playerTickets.sort(Comparator.comparing(Ticket::getTicketDateCreated));
                }

                //By Default, sort by date Updated
                playerTickets.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
                Collections.reverse(playerTickets);

                //Second check if player has no tickets
                if(playerTickets.isEmpty()){
                    player.sendMessage(ChatColor.RED + "You have no tickets!");
                    return;
                }

                //Check if player inputted a page
                if(args.length == 1){
                    this.ticketPageView(player, 1, playerTickets);
                }
                else{
                    this.ticketPageView(player, Integer.parseInt(args[1]), playerTickets);
                }
            }catch(NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th allmytickets <page> <created/updated>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * View all details of an individual ticket belonging to the player who invoked this method - usable by everyone
     * If player has staff permissions, they can access other tickets belonging to other players
     * /th ticketdetails <ticketid> <page>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    public void ticketFullDetails(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                int page = 0;

                //If they forgot to enter the ticketid when calling the command
                if(args.length == 1){
                    player.sendMessage(ChatColor.RED + "Please enter in the format of "
                            + ChatColor.DARK_GREEN + "/th ticketdetails <ticketid> <page>");
                    return;
                }
                //If they only entered the ticket ID
                else if(args.length == 2){
                    page = 1;

                }
                //If they also included a page number
                else if(args.length == 3){
                    page = Integer.parseInt(args[2]);
                }

                //Get the ticket
                Ticket displayTicket = this.plugin.getTicketSystem().getSingleTicket(args[1]);

                //If the ticket doesn't belong to them, they must be staff to view it.
                if(displayTicket.getTicketCreator().equals(Bukkit.getOfflinePlayer(player.getUniqueId())) &&
                player.hasPermission("tickethub.staff")){
                    player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                    return;
                }

                //Combine contacts to single line
                String ticketContacts = "";
                for(UUID i: displayTicket.getTicketContacts()){
                    ticketContacts += " " + Bukkit.getOfflinePlayer(i).getName();
                }

                if(page == 1) {
                    player.sendMessage(ChatColor.AQUA + "Details for Ticket " + displayTicket.getTicketID() + ": ");

                    player.sendMessage(ChatColor.GOLD + "     Title        " + displayTicket.getTicketTitle());
                    player.sendMessage(ChatColor.GOLD + "     Status       " + displayTicket.getTicketStatus().toString());
                    player.sendMessage(ChatColor.GOLD + "     Category     " + displayTicket.getTicketCategory());
                    player.sendMessage(ChatColor.GOLD + "     Priority     " + displayTicket.getTicketPriority().toString());
                    player.sendMessage(ChatColor.GOLD + "     Contacts     " + ticketContacts);
                    player.sendMessage(ChatColor.GOLD + "     Description  " + displayTicket.getTicketDescription());
                    player.sendMessage(ChatColor.GOLD + "     Assigned To  " + Bukkit.getOfflinePlayer(displayTicket.getTicketAssignedTo()).getName());
                    player.sendMessage(ChatColor.GOLD + "     Creator      " + Bukkit.getOfflinePlayer(displayTicket.getTicketCreator()).getName());
                    player.sendMessage(ChatColor.GOLD + "     Last Updated " + dateFormat.format(displayTicket.getTicketDateLastUpdated()));
                    player.sendMessage(ChatColor.GOLD + "     Date Created " + dateFormat.format(displayTicket.getTicketDateCreated()));
                }
                else{

                    page--;

                    if(displayTicket.getTicketComments().isEmpty()){
                        player.sendMessage(ChatColor.GOLD + "This ticket doesn't have any comments!");
                        return;
                    }

                    //9 entries per page
                    int totalPages = (int) Math.ceil((double) displayTicket.getTicketComments().size() / 9);
                    int topOfPage = (page - 1) * 9;
                    int bottomOfPage = 9 * page - 1;

                    if (page > 0 && page <= totalPages) {
                        player.sendMessage(ChatColor.GOLD + "Page: [" + page + "/" + totalPages + "]");
                        if (displayTicket.getTicketComments().size() < topOfPage + 9) {
                            bottomOfPage = displayTicket.getTicketComments().size();
                        }

                        //Reverse it so it shows the latest comments on top
                        Collections.reverse(displayTicket.getTicketComments());

                        player.sendMessage(ChatColor.BLUE + "Comments");
                        for (int i = topOfPage; i < bottomOfPage; i++) {
                            player.sendMessage(ChatColor.GRAY + displayTicket.getTicketComments().get(i));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid Page");
                    }
                }

            }catch(NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th ticketdetails <ticketid> <page>");
            }catch(IllegalArgumentException e){
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Could not find Ticket!");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }


    /**
     * Add a comment to a ticket that belongs to the player who invoked this method - usable by everyone
     * If player has staff permissions, they can add comments to all tickets
     * /th addcomment <ticketid> <comment>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    public void ticketAddComment(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){

            //If they forgot to add the comment
            if(args.length <= 2){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th addcomment <ticketid> <comment>");
                return;
            }

            String argsPlayerName = args[1].substring(0, args[1].length() - 12);
            Player argsGetPlayer = Bukkit.getOfflinePlayer(argsPlayerName).getPlayer();

            if(!this.plugin.getTicketSystem().getStoredTickets().getAllTickets().containsKey(argsGetPlayer.getUniqueId())){
                player.sendMessage(ChatColor.RED + "Could not find Ticket!");
                return;
            }
            else if(this.plugin.getTicketSystem().getStoredTickets().getAllTickets().get(argsGetPlayer.getUniqueId()).isEmpty()){
                player.sendMessage(ChatColor.RED + "Could not find Ticket!");
                return;
            }

            //If their comment has spaces in it
            String commentToAdd = player.getName() + ": ";
            for(int i = 2; i < args.length; i++){
                commentToAdd += " " + args[i];
            }
            
            for(Ticket i: this.plugin.getTicketSystem().getStoredTickets().getAllTickets().get(argsGetPlayer.getUniqueId())){
                if(i.getTicketID().equals(args[1])){
                    i.getTicketComments().add(commentToAdd);
                    i.setTicketDateLastUpdated(new Date());
                    player.sendMessage(ChatColor.GREEN + "Comment added to ticket " + i.getTicketID());
                    return;
                }
            }

            //By the end of the loop, if it doesn't find anything...
            player.sendMessage(ChatColor.RED + "Could not find Ticket!");
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * Show current statistics about all tickets saved - usable by staff
     * /th stats
     *
     * @param player the player who's sending this command
     */
    public void statistics(Player player){

        if(player.hasPermission("tickethub.staff")){
            player.sendMessage(ChatColor.AQUA   + "PRIORITY");
            player.sendMessage(ChatColor.GOLD   + "     " + this.plugin.getTicketSystem().getStoredTickets().getHighPriority() + "  High " );
            player.sendMessage(ChatColor.YELLOW + "     " + this.plugin.getTicketSystem().getStoredTickets().getMediumPriority() + "  Medium ");
            player.sendMessage(ChatColor.GREEN  + "     " + this.plugin.getTicketSystem().getStoredTickets().getLowPriority() + "  Low ");
            player.sendMessage(ChatColor.AQUA   + "STATUS");
            player.sendMessage(ChatColor.RED    + "     " + this.plugin.getTicketSystem().getStoredTickets().getOpened() + "  Open ");
            player.sendMessage(ChatColor.YELLOW + "     " + this.plugin.getTicketSystem().getStoredTickets().getInProgress() + "  In Progress ");
            player.sendMessage(ChatColor.GREEN  + "     " + this.plugin.getTicketSystem().getStoredTickets().getResolved() + "  Resolved ");
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }

    }

    /**
     * edit an existing ticket - usable by staff
     * /th edit
     *
     * @param player the player who's sending this command
     */
    public void editTicket(Player player){
        if(player.hasPermission("tickethub.player")){

        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * Shows all tickets that are filed - usable by staff
     * /th all <page> <updated/created> <ascending/descending>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    public void listAllTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                List<Ticket> displayList = this.plugin.getTicketSystem().getStoredTickets().convertTicketDataMapToList();

                //Check if player stated if they wanted it sorted by date created
                if(args.length == 3 && args[2].equalsIgnoreCase("created")){
                    displayList.sort(Comparator.comparing(Ticket::getTicketDateCreated));
                }

                //By Default, sort by date Updated
                displayList.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));

                //Check if player stated if they wanted to show it in ascending order
                //If descending then don't reverse order
                if(args.length == 4 && args[3].equalsIgnoreCase("ascending")){
                    Collections.reverse(displayList);
                }

                //Second check if there are no tickets
                if(displayList.isEmpty()){
                    player.sendMessage(ChatColor.RED + "There are no tickets!");
                    return;
                }

                //Check if player inputted a page
                if(args.length == 1){
                    this.ticketPageView(player, 1, displayList);
                }
                else{
                    this.ticketPageView(player, Integer.parseInt(args[1]), displayList);
                }
            }catch(NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th all <page> <updated/created> <ascending/descending>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * filter all tickets based on user input - usable by staff
     *
     * @param player the player who's sending this command
     */
    public void filterAllTickets(Player player){
        if(player.hasPermission("tickethub.player")){

        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * display All tickets that are assigned to the player who calls this command - usable by staff
     * /th assigned <page>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    public void myAssignedTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
            conditions.put(Options.TICKETASSIGNEDTO, player.getUniqueId());

            int page = 0;

            if(args.length == 1){
                page = 1;
            }
            else if(args.length == 2){
                page = Integer.parseInt(args[1]);
            }

            ticketPageView(player, page, this.plugin.getTicketSystem().filterTickets(conditions));

        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * Save all tickets into a .json file with player inputting the name of the file - usable by staff
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    public void saveAllTickets(Player player, String[] args){

        if(player.hasPermission("tickethub.staff")){
            //If they didn't add a name to save it as
            if(args.length == 1){
                player.sendMessage(ChatColor.DARK_GREEN + "/th save <name>");
                player.sendMessage(ChatColor.DARK_GREEN + "Please enter a name to save the ticket file as");
                return;
            }
            else{
                player.sendMessage(ChatColor.GRAY + "Saving tickets as: " + args[1]);
                this.plugin.getTicketSystem().saveTickets(args[1]);
                player.sendMessage(ChatColor.GREEN + "Tickets saved!");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * Displays list in player friendly page format
     * To be used by other methods
     *
     * @param player         the player who's sending this command
     * @param page           the command input
     * @param displayTickets list to display as
     */
    public void ticketPageView(Player player, int page, List<Ticket> displayTickets) {

        //9 entries per page
        int totalPages = (int) Math.ceil((double) displayTickets.size() / 9);
        int topOfPage = (page - 1) * 9;
        int bottomOfPage = 9 * page - 1;

        if (page > 0 && page <= totalPages) {
            player.sendMessage(ChatColor.GOLD + "Page: [" + page + "/" + totalPages + "]");
            if (displayTickets.size() < topOfPage + 9) {
                bottomOfPage = displayTickets.size();
            }

            player.sendMessage(ChatColor.BLUE + "Ticket ID - Date Updated");
            for (int i = topOfPage; i < bottomOfPage; i++) {
                player.sendMessage(ChatColor.GRAY + displayTickets.get(i).getTicketID() + " | " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()));
            }
        } else {
            player.sendMessage(ChatColor.RED + "Invalid Page");
        }
    }

}
