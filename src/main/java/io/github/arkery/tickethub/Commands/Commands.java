package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.Commands.EditTicketConv.TicketToEdit;
import io.github.arkery.tickethub.Commands.FilterTicketsConv.FilterMenu;
import io.github.arkery.tickethub.Commands.NewTicketConv.titleNewTicket;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
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

public class Commands extends BackGroundCommandUntil implements CommandExecutor {
    
    private ConversationFactory conversationFactory;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public Commands(TicketHub plugin){
        super(plugin);
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
                    case "mytickets":
                        this.myTicketsAll(player, args);
                        return false;
                    case "ticketdetails":
                        this.ticketDetails(player, args);
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
    
//Individual Commands
    /**
     * Main command if player only does /th
     * Shows all available commands.
     *
     * @param player the player who's sending this command
     */
    private void mainCommand(Player player){
        player.sendMessage(ChatColor.AQUA + "TicketHub by arkery");
        player.sendMessage(ChatColor.GREEN + "Commands");
        player.sendMessage(ChatColor.GOLD + "   /th new " + ChatColor.GRAY + "Create a new ticket");
        player.sendMessage(ChatColor.GOLD + "   /th mytickets " + ChatColor.GRAY + "See all your tickets");
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
    private void createNewTicket(Player player){
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
    private void myTicketsAll(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                //Check if player has tickets
                if(plugin.getTicketSystem().getStoredData().getAllTickets().getAllX(player.getUniqueId()).isEmpty()){
                    player.sendMessage(ChatColor.RED + "You have no tickets!");
                    return;
                }

                List<Ticket> playerTickets = plugin.getTicketSystem().getStoredData().getAllTickets().getAllX(player.getUniqueId());

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
                    super.ticketPageView(player, 1, playerTickets);
                }
                else{
                    super.ticketPageView(player, Integer.parseInt(args[1]), playerTickets);
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
    private void ticketDetails(Player player, String[] args){
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
                Ticket displayTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                //If the ticket doesn't belong to them, they must be staff to view it.
                if(!displayTicket.getTicketCreator().equals(super.plugin.getTicketSystem().getUserUUID(player.getName())) &&
                !player.hasPermission("tickethub.staff")){
                    player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                    return;
                }

                //Combine contacts to single line
                String ticketContacts = "";
                for(UUID i: displayTicket.getTicketContacts()){
                    ticketContacts += " " + super.plugin.getTicketSystem().getUserName(i);
                }

                if(page == 1) {
                    player.sendMessage(ChatColor.AQUA + "\nDetails for Ticket " + displayTicket.getTicketID() + ": ");

                    player.sendMessage(ChatColor.GOLD + "\n   Title: " + ChatColor.BLUE + displayTicket.getTicketTitle());
                    player.sendMessage(ChatColor.GOLD + "\n   Status: " + ChatColor.BLUE + displayTicket.getTicketStatus().toString());
                    player.sendMessage(ChatColor.GOLD + "   Category: " + ChatColor.BLUE + displayTicket.getTicketCategory());
                    player.sendMessage(ChatColor.GOLD + "   Priority: " + ChatColor.BLUE + displayTicket.getTicketPriority().toString());
                    player.sendMessage(ChatColor.GOLD + "\n   Contacts: " + ChatColor.BLUE + ticketContacts);
                    player.sendMessage(ChatColor.GOLD + "\n   Description: " + ChatColor.BLUE + displayTicket.getTicketDescription());
                    player.sendMessage(ChatColor.GOLD + "\n   Assigned To: " + ChatColor.BLUE + super.plugin.getTicketSystem().getUserName(displayTicket.getTicketAssignedTo()));
                    player.sendMessage(ChatColor.GOLD + "   Creator: " + ChatColor.BLUE + super.plugin.getTicketSystem().getUserName(displayTicket.getTicketCreator()));
                    player.sendMessage(ChatColor.GOLD + "\n   Last Updated: " + ChatColor.BLUE + dateFormat.format(displayTicket.getTicketDateLastUpdated()));
                    player.sendMessage(ChatColor.GOLD + "   Date Created: " + ChatColor.BLUE + dateFormat.format(displayTicket.getTicketDateCreated()));
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

                        player.sendMessage(ChatColor.BLUE + "Comments:");
                        for (int i = topOfPage; i < bottomOfPage; i++) {
                            player.sendMessage(ChatColor.GOLD + displayTicket.getTicketComments().get(i));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid Page");
                    }
                }

            }catch(NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th ticketdetails <ticketid> <page>");
            }catch(TicketNotFoundException e){
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Could not find Ticket!");
            }catch(PlayerNotFoundException e){
                player.sendMessage(ChatColor.RED + "Unable to find player!");
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
            else{
                try{
                    Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]);

                    if(editingTicket.getTicketCreator() != player.getUniqueId() && player.hasPermission("tickethub.staff")){
                        player.sendMessage("You do not have permission to modify a ticket that isn't yours!");
                        return;
                    }

                    String commentToAdd = player.getName() + ": ";
                    for(int i = 2; i < args.length; i++){
                        commentToAdd += " " + args[i];
                    }
                    editingTicket.getTicketComments().add(commentToAdd);
                    this.plugin.getTicketSystem().updateTicket(editingTicket);


                }catch(TicketNotFoundException e){
                    player.sendMessage(ChatColor.RED + "Could not find Ticket!");
                    return;
                }
            }
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
    private void statistics(Player player){

        if(player.hasPermission("tickethub.staff")){
            player.sendMessage(ChatColor.AQUA   + "PRIORITY");
            player.sendMessage(ChatColor.GOLD   + "     " + this.plugin.getTicketSystem().getStoredData().getHighPriority() + "  High " );
            player.sendMessage(ChatColor.YELLOW + "     " + this.plugin.getTicketSystem().getStoredData().getMediumPriority() + "  Medium ");
            player.sendMessage(ChatColor.GREEN  + "     " + this.plugin.getTicketSystem().getStoredData().getLowPriority() + "  Low ");
            player.sendMessage(ChatColor.AQUA   + "STATUS");
            player.sendMessage(ChatColor.RED    + "     " + this.plugin.getTicketSystem().getStoredData().getOpened() + "  Open ");
            player.sendMessage(ChatColor.YELLOW + "     " + this.plugin.getTicketSystem().getStoredData().getInProgress() + "  In Progress ");
            player.sendMessage(ChatColor.GREEN  + "     " + this.plugin.getTicketSystem().getStoredData().getResolved() + "  Resolved ");
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
    private void editTicket(Player player){
        if(player.hasPermission("tickethub.staff")){
            Conversation conv = conversationFactory
                    .withFirstPrompt(new TicketToEdit(plugin))
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
     * Shows all tickets that are filed - usable by staff
     * /th all <page> <updated/created> <ascending/descending>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    private void listAllTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                List<Ticket> displayList = this.plugin.getTicketSystem().getStoredData().getAllTickets().getAll();

                //Check if there are no tickets
                if(displayList.isEmpty()){
                    player.sendMessage(ChatColor.RED + "There are no tickets!");
                    return;
                }

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

                //Check if player inputted a page
                if(args.length == 1){
                    super.ticketPageView(player, 1, displayList);
                }
                else{
                    super.ticketPageView(player, Integer.parseInt(args[1]), displayList);
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
    private void filterAllTickets(Player player){
        if(player.hasPermission("tickethub.staff")){
            Conversation conv = conversationFactory
                    .withFirstPrompt(new FilterMenu(plugin))
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
     * display All tickets that are assigned to the player who calls this command - usable by staff
     * /th assigned <page>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    private void myAssignedTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
            conditions.put(Options.ASSIGNEDTO, player.getUniqueId());

            int page = 0;

            if(args.length == 1){
                page = 1;
            }
            else if(args.length == 2){
                page = Integer.parseInt(args[1]);
            }

            super.ticketPageView(player, page, this.plugin.getTicketSystem().filterTickets(conditions));

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
    private void saveAllTickets(Player player, String[] args){

        if(player.hasPermission("tickethub.staff")){
            //If they didn't add a name to save it as
            if(args.length == 1){
                player.sendMessage(ChatColor.DARK_GREEN + "/th save <name>");
                player.sendMessage(ChatColor.DARK_GREEN + "Please enter a name to save the ticket file as");
                return;
            }
            else if(args.length == 2){
                player.sendMessage(ChatColor.GRAY + "Saving tickets as: " + args[1]);
                this.plugin.getTicketSystem().saveTickets(args[1]);
                player.sendMessage(ChatColor.GREEN + "Tickets saved!");
            }
            else{
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th save <file name> |"
                        + ChatColor.RED + "Note: You cannot have spaces in your file name");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }
}
