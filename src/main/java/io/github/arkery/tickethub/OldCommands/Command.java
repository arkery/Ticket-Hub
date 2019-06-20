package io.github.arkery.tickethub.OldCommands;

import io.github.arkery.tickethub.OldCommands.EditTicketConv.TicketToEdit;
import io.github.arkery.tickethub.OldCommands.FilterTicketsConv.FilterMenu;
import io.github.arkery.tickethub.OldCommands.NewTicketConv.titleNewTicket;
import io.github.arkery.tickethub.CustomUtils.DisplayTickets;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.CustomUtils.TicketPageView;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

public class Command extends ClickCommands implements CommandExecutor {
    
    private ConversationFactory conversationFactory;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public Command(TicketHub plugin){
        super(plugin);
        this.conversationFactory = new ConversationFactory(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] args) {

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
                //Past beyond here is for click commands
                    case "cedittitle":
                        super.clickEditTitle(player, args);
                        return false;
                    case "ceditstatus":
                        super.clickEditStatus(player, args);
                        return false;
                    case "ccloseticket":
                        super.clickCloseTicket(player, args);
                        return false;
                    case "ceditpriority":
                        super.clickEditPriority(player, args);
                        return false;
                    case "ceditcategory":
                        super.clickEditCategory(player, args);
                        return false;
                    case "ceditcontacts":
                        super.clickEditContacts(player, args);
                        return false;
                    case "ceditdescription":
                        super.clickEditDescription(player, args);
                    case "ceditassignedto":
                        super.clickEditAssignedTo(player, args);
                    case "cviewcomments":
                        super.clickViewComments(player, args);
                        return false;
                    case "caddcomment":
                        super.clickAddComment(player, args);
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
        player.sendMessage("\n" + ChatColor.AQUA + "TicketHub by arkery");
        player.sendMessage(ChatColor.GREEN + "OldCommands");
        player.sendMessage(ChatColor.GOLD + "   /th new " + ChatColor.GRAY + "Create a new ticket");
        player.sendMessage(ChatColor.GOLD + "   /th mytickets " + ChatColor.GRAY + "See all your tickets");
        player.sendMessage(ChatColor.GOLD + "   /th ticketdetails " + ChatColor.GRAY + "See an individual ticket");

        if(player.hasPermission("tickethub.staff")){
            player.sendMessage(ChatColor.GREEN + "Staff OldCommands");
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
                    new TicketPageView().ticketPageView(player, 1, playerTickets);
                }
                else{
                    new TicketPageView().ticketPageView(player, Integer.parseInt(args[1]), playerTickets);
                }
            }catch(NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th allmytickets <page> <created/updated>");
            }catch(NullPointerException e){
                player.sendMessage(ChatColor.RED + "There are no tickets!");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    /**
     * View all details of an individual ticket belonging to the player who invoked this method - usable by everyone
     * If player has staff permissions, they can access other tickets belonging to other players
     * /th ticketdetails <ticketid>
     *
     * @param player the player who's sending this command
     * @param args   the command input
     */
    private void ticketDetails(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{

                Ticket displayTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                //If the ticket doesn't belong to them, they must be staff to view it.
                if(!displayTicket.getTicketCreator().equals(super.plugin.getTicketSystem().getUserUUID(player.getName())) &&
                !player.hasPermission("tickethub.staff")){
                    player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                    return;
                }

                String ticketContactsAsString = "";
                for(UUID i: displayTicket.getTicketContacts()){
                    ticketContactsAsString += " " + super.plugin.getTicketSystem().getUserName(i);
                }

                String categoryAsString = "";
                for(String i : this.plugin.getCustomCategories()){
                    categoryAsString += " " +  i;
                }

            //SetUp

                //Title
                TextComponent ticketTitle = new TextComponent(displayTicket.getTicketTitle());
                ticketTitle.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketTitle.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Title").create()));
                ticketTitle.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th cedittitle " + displayTicket.getTicketID() + " " + displayTicket.getTicketTitle()));

                TextComponent messageTitle = new TextComponent("\n   Title: ");
                messageTitle.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageTitle.addExtra(ticketTitle);

                //Status
                TextComponent ticketStatus = new TextComponent(displayTicket.getTicketStatus().toString());
                ticketStatus.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketStatus.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Status | Options: Opened InProgress Resolved").create()));
                ticketStatus.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ceditstatus " + displayTicket.getTicketID() + " " + displayTicket.getTicketStatus().toString()));

                TextComponent messageStatus = new TextComponent("   Status: ");
                messageStatus.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageStatus.addExtra(ticketStatus);

                //Priority
                TextComponent ticketPriority = new TextComponent(displayTicket.getTicketPriority().toString());
                ticketPriority.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketPriority.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Priority | Options: Low Medium High").create()));
                ticketPriority.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ceditpriority " + displayTicket.getTicketID() + " " + displayTicket.getTicketPriority().toString()));

                TextComponent messagePriority = new TextComponent("   Priority: ");
                messagePriority.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messagePriority.addExtra(ticketPriority);

                //Category
                TextComponent ticketCategory = new TextComponent(displayTicket.getTicketCategory());
                ticketCategory.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketCategory.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Category | Options: " + categoryAsString).create()));
                ticketCategory.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ceditcategory " + displayTicket.getTicketID() + " " + displayTicket.getTicketPriority().toString()));

                TextComponent messageCategory = new TextComponent("   Category: ");
                messageCategory.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageCategory.addExtra(ticketCategory);

                //Contacts
                TextComponent ticketContacts = new TextComponent(ticketContactsAsString);
                ticketContacts.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketContacts.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Contacts").create()));
                ticketContacts.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ceditcontacts " + displayTicket.getTicketID() + " " + ticketContactsAsString));

                TextComponent messageContacts = new TextComponent("\n   Contacts: ");
                messageContacts.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageContacts.addExtra(ticketContacts);

                //Description
                TextComponent ticketDescription = new TextComponent(displayTicket.getTicketDescription());
                ticketDescription.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketDescription.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Description").create()));
                ticketDescription.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ceditdescription " + displayTicket.getTicketID() + " " + displayTicket.getTicketDescription()));

                TextComponent messageDescription = new TextComponent("\n   Description: ");
                messageDescription.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageDescription.addExtra(ticketDescription);

                //Assigned To
                TextComponent ticketAssignedTo = new TextComponent(super.plugin.getTicketSystem().getUserName(displayTicket.getTicketAssignedTo()));
                ticketAssignedTo.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketAssignedTo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to edit Description").create()));
                ticketAssignedTo.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ceditdescription " + displayTicket.getTicketID() + " " + displayTicket.getTicketDescription()));

                TextComponent messageAssignedTo = new TextComponent("\n   Assigned To: ");
                messageAssignedTo.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageAssignedTo.addExtra(ticketAssignedTo);

                //Creator
                TextComponent ticketCreator = new TextComponent(super.plugin.getTicketSystem().getUserName(displayTicket.getTicketCreator()));
                ticketCreator.setColor(net.md_5.bungee.api.ChatColor.BLUE);

                TextComponent messageCreator = new TextComponent("\n   Creator: ");
                messageCreator.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageCreator.addExtra(ticketCreator);

                //Last Updated
                TextComponent ticketLastUpdated = new TextComponent(dateFormat.format(displayTicket.getTicketDateLastUpdated()));
                ticketLastUpdated.setColor(net.md_5.bungee.api.ChatColor.BLUE);

                TextComponent messageLastUpdated = new TextComponent("\n   Last Updated: ");
                messageLastUpdated.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageLastUpdated.addExtra(ticketLastUpdated);

                //Created
                TextComponent ticketDateCreated = new TextComponent(dateFormat.format(displayTicket.getTicketDateCreated()));
                ticketDateCreated.setColor(net.md_5.bungee.api.ChatColor.BLUE);

                TextComponent messageDateCreated = new TextComponent("   Created On: ");
                messageDateCreated.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                messageDateCreated.addExtra(ticketLastUpdated);

                //Add Comments
                TextComponent ticketAddComment = new TextComponent("   Add A Comment");
                ticketAddComment.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                ticketAddComment.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click Here To Add A Comment").create()));
                ticketAddComment.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/th caddcomment "+ displayTicket.getTicketID()));

                //View Comments
                TextComponent ticketViewComments = new TextComponent("\n   View Comments");
                ticketViewComments.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                ticketViewComments.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click Here To View Comments").create()));
                ticketViewComments.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/th cviewcomments "+ displayTicket.getTicketID() + " 1"));
                ticketViewComments.addExtra(ticketAddComment);

                //Close Ticket
                TextComponent ticketClose = new TextComponent("   Click here to Close Ticket");
                ticketClose.setColor(net.md_5.bungee.api.ChatColor.RED);
                ticketClose.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Warning: Closing a ticket essentially deletes it").create()));
                ticketClose.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,"/th ccloseticket " + displayTicket.getTicketID()));

                //Displaying
                player.sendMessage(ChatColor.AQUA + "\nDetails for Ticket " + displayTicket.getTicketID() + ": ");

                player.spigot().sendMessage(messageTitle);
                player.spigot().sendMessage(messageStatus);
                player.spigot().sendMessage(messagePriority);
                player.spigot().sendMessage(messageCategory);
                player.spigot().sendMessage(messageContacts);
                player.spigot().sendMessage(messageDescription);
                player.spigot().sendMessage(messageAssignedTo);
                player.spigot().sendMessage(messageCreator);
                player.spigot().sendMessage(messageLastUpdated);
                player.spigot().sendMessage(messageDateCreated);
                player.spigot().sendMessage(ticketViewComments);
                player.spigot().sendMessage(ticketClose);

            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th ticketdetails <ticketid>");
            }catch(TicketNotFoundException e){
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
                if(displayList.isEmpty()|| displayList == null){
                    player.sendMessage(ChatColor.RED + "There are no tickets!");
                    return;
                }

                Conversation conv = conversationFactory
                        .withFirstPrompt(new DisplayTickets(plugin, displayList, player))
                        .withLocalEcho(false)
                        .buildConversation(player);
                conv.begin();

                /*
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
                    new TicketPageView().ticketPageView(player, 1, displayList);
                }
                else{
                    new TicketPageView().ticketPageView(player, Integer.parseInt(args[1]), displayList);
                }
                */

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
     * @param args   args0 = assigned, args1 = page Number
     */
    private void myAssignedTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
                conditions.put(Options.ASSIGNEDTO, player.getUniqueId());

                int page = 1;

                if(args.length > 1){
                    page = Integer.parseInt(args[1]);
                }

                new TicketPageView().ticketPageView(player, page, this.plugin.getTicketSystem().filterTickets(conditions));
            }catch(NumberFormatException e){
                player.sendMessage(ChatColor.RED + "Invalid Page Number!");
            }
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
