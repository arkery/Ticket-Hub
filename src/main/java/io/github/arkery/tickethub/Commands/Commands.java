package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.Commands.FilterTicketsConv.FilterMenu;
import io.github.arkery.tickethub.Commands.NewTicketConv.titleNewTicket;
import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class Commands extends CommandTemplates implements CommandExecutor {

    private ConversationFactory conversationFactory;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM.dd.yy");


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
            }
            else{
                switch(args[0].toLowerCase()){
                    case "new":
                        this.createNewTicket(player);
                        break;
                    case "mytickets":
                        this.myTickets(player, args);
                        break;
                    case "details":
                        this.ticketDetails(player, args);
                        break;
                    case "stats":
                        this.statistics(player);
                        break;
                    case "all":
                        this.allTickets(player, args);
                        break;
                    case "filter":
                        this.filterTickets(player);
                        break;
                    case "assigned":
                        this.assignedTickets(player, args);
                        break;
                    case "save":
                        this.manualBackUpTickets(player, args);
                        break;
                    case "close":
                        this.closeTicket(player, args);
                        break;
                    case "edittitle":
                        this.ticketEditTitle(player, args);
                        this.ticketDetails(player, args);
                        break;
                    case "editstatus":
                        this.ticketEditStatus(player, args);
                        this.ticketDetails(player, args);
                        break;
                    case "editpriority":
                        this.ticketEditPriority(player, args);
                        this.ticketDetails(player, args);
                        break;
                    case "editcategory":
                        this.ticketEditCategory(player, args);
                        this.ticketDetails(player, args);
                        break;
                    case "editcontacts":
                        this.ticketEditContacts(player, args);
                        this.ticketDetails(player, args);
                        break;
                    case "editdesc":
                        this.ticketEditDescription(player, args);
                        this.ticketDetails(player, args);
                        break;
                    case "editassigned":
                        this.ticketEditAssignedTo(player, args);
                        this.ticketDetails(player, args);
                        break;
                    default:
                        this.mainCommand(player);
                        break;
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
            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"\nTicketHub Menu").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   new", "Click here to create a new ticket", "/th new ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " create a new ticket")).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   mytickets", "Click here to see your current tickets", "/th mytickets ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " See your tickets")).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   details", "Click here to see individual ticket details", "/th details ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See individual ticket details")).text());

            if(player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.AQUA,"Staff Menu").text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   stats", "Click here to see ticket stats", "/th stats", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " See Hub Statistics")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   all", "Click here to all tickets", "/th all ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " See all tickets")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   filter", "Click here to start filter view", "/th filter ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " filter all tickets")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   assigned", "Click here to your assigned tickets", "/th assigned ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " See all your assigned tickets")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   close", "Click here to close a ticket", "/th close ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " Close a ticket")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   save", "Click here to see ticket stats", "/th save ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " Save Tickets Manually")).text());
            }
        }else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

    /**
     * Create a new ticket.
     * Player must have permission: "tickethub.player".
     * /th new
     * 
     * @param player the creator of the ticket
     */
    private void createNewTicket(Player player){
        if(player.hasPermission("tickethub.player")){
            Conversation conv = conversationFactory
                    .withFirstPrompt(new titleNewTicket(plugin))
                    .withLocalEcho(false)
                    .withTimeout(120)
                    .buildConversation(player);
            conv.begin();
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    /**
     * View tickets in filter mode
     * Player must have permission: "tickethub.staff".
     * /th new
     * 
     * @param player the creator of the ticket
     */
    private void filterTickets(Player player){
        if(player.hasPermission("tickethub.staff")){

            List<Ticket> allTickets = this.plugin.getTicketSystem().getStoredData().getAllTickets().getAll(); 

            if(allTickets.isEmpty() || allTickets == null){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
                return; 
            }

            Conversation conv = conversationFactory
                    .withFirstPrompt(new FilterMenu(plugin, player, allTickets))
                    .withLocalEcho(false)
                    .withTimeout(120)
                    .buildConversation(player);
            conv.begin();
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    /**
     * All tickets belonging to the player who invoked this method.
     * /th mytickets (page) (date created/ date updated)
     * 
     * @param player the player that's calling this
     * @param args the command called | args0 - mytickets | args1 - page number (if applicable) | args2 - sort setting (created update)
     */
    private void myTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{

                super.TicketListView(player, args, plugin.getTicketSystem().getStoredData().getAllTickets().getAllX(player.getUniqueId()));

            }catch(NumberFormatException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th mytickets (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th all (page) ").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    /**
     * All tickets that are in the Hub.
     * They must have permission: "tickethub.staff".
     * By default - sorted by date updated & display the last updated ticket on top.
     * /th all (page) (datecreated/date updated)
     * 
     * @param player the player who's invoking this method
     * @param args args0 - all | args1 - page (if applicable) | args2 - sort by (date created or date updated [by default, sort by date updated])
     * 
     */
    private void allTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                List<Ticket> tickets = plugin.getTicketSystem().getStoredData().getAllTickets().getAll();

                super.TicketListView(player, args, tickets);

            }catch(NumberFormatException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th all (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th all (page) ").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    /**
     * All tickets that are assigned to the player that's invoking this command.
     * They have to be staff or have permission: "tickethub.staff".
     * /th assigned (page) (datecreated/dateupdated).
     * 
     * @param player player who's invoking this command 
     * @param args args0 - assigned | args1 - page (if applicable) | args2 - sorting order 
     */
    private void assignedTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                //Filter all tickets for those assigned to the player
                EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
                conditions.put(Options.ASSIGNEDTO, player.getUniqueId());

                super.TicketListView(player, args, super.plugin.getTicketSystem().filterTickets(conditions, super.plugin.getTicketSystem().getStoredData().getAllTickets().getAll()));

            }catch(NumberFormatException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th assigned (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th all (page) ").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    /**
     * Show full details of an individual ticket.
     * Player must have permission: "tickethub.player" to invoke this command. Player also must have "tickethub.staff" if they want to view a ticket that doesn't belong to them. 
     * /th details (ticket id).
     * 
     * @param player player who's invoking this command
     * @param args args0 - details | args1 - ticketid
     */
    private void ticketDetails(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                Ticket displayTicket = super.plugin.getTicketSystem().getTicket(args[1]);

                if(!displayTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
                    return;
                }

                String ticketContactsAsString = "";
                if(displayTicket.getTicketContacts().isEmpty() || displayTicket.getTicketContacts() == null){
                    ticketContactsAsString = "None"; 
                }else{
                    for(UUID i: displayTicket.getTicketContacts()){
                        ticketContactsAsString += " " + super.plugin.getTicketSystem().getUserName(i);
                    }
                }

                String ticketCategoriesAsString = "";
                for(String i :super.plugin.getCustomCategories()){
                    ticketCategoriesAsString += i + " "; 
    
                }
            
                player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nDetails for Ticket: ").add(new Clickable(ChatColor.AQUA, displayTicket.getTicketID())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Title: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketTitle(), "Click here to edit ticket title", "/th edittitle " + displayTicket.getTicketID() + " " + displayTicket.getTicketTitle(), ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Status: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketStatus().toString(), "Click here to edit ticket status \n(Opened InProgress Resolved) ", "/th editstatus " + displayTicket.getTicketID() + " ", ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Priority: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketPriority().toString(), "Click here to edit ticket priority \n(Low Medium High) ", "/th editpriority " + displayTicket.getTicketID() + " " , ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Category: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketCategory(), "Click here to edit ticket category \n(" + ticketCategoriesAsString + ")", "/th editcategory" + displayTicket.getTicketID() + " ", ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Contacts:").add(new Clickable(ChatColor.BLUE, ticketContactsAsString, "Click here to edit ticket contacts ", "/th editcontacts " + ticketContactsAsString, ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Description: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketDescription(), "Click here to edit ticket description", "/th editdesc " + displayTicket.getTicketID() + " " + displayTicket.getTicketDescription(), ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Creator: ").add(new Clickable(ChatColor.BLUE, super.plugin.getTicketSystem().getUserName(displayTicket.getTicketCreator()))).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Assigned To: ").add(new Clickable(ChatColor.BLUE, super.plugin.getTicketSystem().getUserName(displayTicket.getTicketAssignedTo()), "Click here to edit/change the person assigned to this ticket", "/th editassigned ", ClickEvent.Action.SUGGEST_COMMAND)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Last Updated On: ").add(new Clickable(ChatColor.BLUE, dateFormat.format(displayTicket.getTicketDateLastUpdated()))).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Created On: ").add(new Clickable(ChatColor.BLUE, dateFormat.format(displayTicket.getTicketDateCreated()))).text());

                player.spigot().sendMessage(new Clickable(ChatColor.YELLOW, "\n\n   Close ticket", "Click here to close ticket (This will delete the ticket)", "/th close " + displayTicket.getTicketID(), ClickEvent.Action.SUGGEST_COMMAND).text());

            }catch(TicketNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th details (ticket ID) ").text());
            }catch(PlayerNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Person was not found! ").text());

            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }

    }

    /**
     * Edit the ticket Title.
     * To be used by Ticket Details for click based edit interaction.
     * Player must have permission: "tickethub.staff" OR ticket must belong to player to edit the ticket.
     * /th edittitle (ticket id) (new title [might have multiple strings]).
     * 
     * @param player The player that's invoking this command
     * @param args   args0 - edittitle | args1 - ticket id | args2->argsN - new ticket title
     */
    private void ticketEditTitle(Player player, String[] args){
        try{
            Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]); 
            String newTitle = ""; 
            
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
                return;
            }

            for(int i = 2; i < args.length; i++){
                newTitle += " " + args[i]; 
            }
            
            editingTicket.setTicketTitle(newTitle);
            editingTicket.setTicketDateLastUpdated(new Date());
            super.plugin.getTicketSystem().updateTicket(editingTicket);
            player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Title Updated! ").text());

        }catch(TicketNotFoundException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
        }catch(IndexOutOfBoundsException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th edittitle (ticket ID) (New Title)").text());
        }

    }

    /**
     * Edit Ticket a Ticket Status.
     * Player must have permission: "tickethub.staff".
     * /th editstatus (ticket id) (new status).
     * 
     * @param player The player that's invoking this command
     * @param args   args0 - editstatus | args1 - ticket id | args2 - new ticket status
     */
    private void ticketEditStatus(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]); 
                switch(args[2].toLowerCase()){
                    case "opened":
                        editingTicket.setTicketStatus(Status.OPENED);
                        break;
                    case "inprogress":
                        editingTicket.setTicketStatus(Status.INPROGRESS);
                        break;
                    case "resolved":
                        editingTicket.setTicketStatus(Status.RESOLVED);
                        break;
                }
                editingTicket.setTicketDateLastUpdated(new Date());
                super.plugin.getTicketSystem().updateTicket(editingTicket);
                player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Status Updated! ").text());

            }catch(TicketNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editstatus (ticket ID) (New Status)").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

    /**
     * Edit Ticket a Ticket Priority.
     * Player must have permission: "tickethub.staff".
     * /th editcategory (ticket id) (new category).
     * 
     * @param player The player that's invoking this command
     * @param args   args0 - editcategory | args1 - ticket id | args2 - new ticket category
     */
    private void ticketEditCategory(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]); 
                
                if(!super.plugin.getCustomCategories().contains(args[2].toLowerCase())){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "Invalid Category!").text());
                    return; 
                }

                editingTicket.setTicketCategory(args[2].toLowerCase());
                editingTicket.setTicketDateLastUpdated(new Date());
                super.plugin.getTicketSystem().updateTicket(editingTicket);
                player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Priority Updated! ").text());

            }catch(TicketNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editcategory (ticket ID) (New Category)").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

    /**
     * Edit Ticket a Ticket Priority.
     * Player must have permission: "tickethub.staff".
     * /th editpriority (ticket id) (new priority).
     * 
     * @param player The player that's invoking this command
     * @param args   args0 - editpriority | args1 - ticket id | args2 - new ticket priority
     */
    private void ticketEditPriority(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]); 
                switch(args[2].toLowerCase()){
                    case "low":
                        editingTicket.setTicketPriority(Priority.LOW);
                        break;
                    case "medium":
                        editingTicket.setTicketPriority(Priority.MEDIUM);
                        break;
                    case "high":
                        editingTicket.setTicketPriority(Priority.HIGH);
                        break;
                }
                editingTicket.setTicketDateLastUpdated(new Date());
                super.plugin.getTicketSystem().updateTicket(editingTicket);
                player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Priority Updated! ").text());

            }catch(TicketNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editpriority (ticket ID) (New Priority)").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

    /**
     * Edit the ticket Contacts - replaces the current stored contacts list with an entirely different list.
     * Ticket must belong to the player or player must have permission: "tickethub.staff" if they want to edit contacts.
     * /th editcontacts (ticketid) (Contact1) (Contact2) (Contact3)
     * Up to 3 contacts. Contacts are optional entries in the ticket. 
     * 
     * @param player Player that's invoking this command 
     * @param args   args0 - editcontacts | args1 - ticket id | args2 - contacts 1 | args3 - contact 2 | args4 - contact 3
     */
    private void ticketEditContacts(Player player, String[] args){
        try{
            Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]);  
            List<UUID> newContacts = new ArrayList<>(); 
            
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
                return;
            }

            if(args.length > 5){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: You can only have three contacts!").text());
            }

            for(int i = 2; i < args.length; i++){
                newContacts.add(super.plugin.getTicketSystem().getUserUUID(args[i]));
            }
            
            editingTicket.setTicketContacts(newContacts);
            editingTicket.setTicketDateLastUpdated(new Date());
            super.plugin.getTicketSystem().updateTicket(editingTicket);
            player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Title Updated! ").text());

        }catch(TicketNotFoundException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
        }catch(IndexOutOfBoundsException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editcontacts (ticket ID) (Contact 1) (Contact 2) (Contact 3)").text());
        }catch(PlayerNotFoundException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: One of the contacts did not join the server! ").text());
        }
    }

    /**
     * Edit the ticket Description.
     * To be used by Ticket Details for click based edit interaction.
     * Player must have permission: "tickethub.staff" OR ticket must belong to player to edit the ticket.
     * /th editdesc (ticket id) (new description [might have multiple strings]).
     * 
     * @param player The player that's invoking this command
     * @param args   args0 - edittitle | args1 - ticket id | args2->argsn - new ticket title
     */
    private void ticketEditDescription(Player player, String[] args){
        try{
            Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]); 
            String newDescription = ""; 
            
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
                return;
            }

            for(int i = 2; i < args.length; i++){
                newDescription += " " + args[i]; 
            }
            
            editingTicket.setTicketDescription(newDescription);
            editingTicket.setTicketDateLastUpdated(new Date());
            super.plugin.getTicketSystem().updateTicket(editingTicket);
            player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Description Updated! ").text());

        }catch(TicketNotFoundException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
        }catch(IndexOutOfBoundsException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th edittitle (ticket ID) (New Title)").text());
        }
    }

    /**
     * Edit the person that the ticket is assigned to.
     * Player must have permission: "tickethub.staff".
     * /th editassigned (ticketid) (Assigned Person).
     * 
     * @param player Player that's invoking this command
     * @param args   args0 - editcontacts | args1 - ticket id | args2 - new assigned person
     */
    private void ticketEditAssignedTo(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]);  
                
                editingTicket.setTicketAssignedTo(super.plugin.getTicketSystem().getUserUUID(args[1]));
                editingTicket.setTicketDateLastUpdated(new Date());
                super.plugin.getTicketSystem().updateTicket(editingTicket);
                player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTicket Title Updated! ").text());
    
            }catch(TicketNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editassigned (ticket ID) (Person assigned to)").text());
            }catch(PlayerNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: One of the contacts did not join the server! ").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }


    /**
     * Hub statistics 
     * Player must have permission: "tickethub.staff"
     * /th stats
     * 
     * @param player player who's invoking this command. 
     */
    private void statistics(Player player){

        if(player.hasPermission("tickethub.staff")){

            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"\nPriority").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Low: ").add(new Clickable(ChatColor.BLUE, "" + super.plugin.getTicketSystem().getStoredData().getLowPriority())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Medium: ").add(new Clickable(ChatColor.BLUE, "" + super.plugin.getTicketSystem().getStoredData().getMediumPriority())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   High: ").add(new Clickable(ChatColor.BLUE, "" + super.plugin.getTicketSystem().getStoredData().getHighPriority())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"Status").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   New: ").add(new Clickable(ChatColor.BLUE, "" + super.plugin.getTicketSystem().getStoredData().getOpened())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   InProgress: ").add(new Clickable(ChatColor.BLUE, "" + super.plugin.getTicketSystem().getStoredData().getInProgress())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Resolved: ").add(new Clickable(ChatColor.BLUE, "" + super.plugin.getTicketSystem().getStoredData().getResolved())).text());
        
        }
        else {
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

    /**
     * Manually backup and save tickets into a separate ticket .json file.
     * Player must have permission: "tickethub.staff".
     * /th save (name).
     * 
     * @param player the player who's invoking this command 
     * @param args args0 - save | args1 - name
     */
    private void manualBackUpTickets(Player player, String[] args){

        if(player.hasPermission("tickethub.staff")){
            try{
                if(args.length > 2){
                    player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th save (file name) ").text());
                    return;
                }

                super.plugin.getTicketSystem().saveTickets(args[1]);
                player.spigot().sendMessage(new Clickable(ChatColor.GREEN, "\nTickets saved as " + args[1] + ".json !").text());

            }catch(IndexOutOfBoundsException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th save (file name) ").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

    /**
     * Close a ticket (Immediately deletes it).
     * The ticket must belong to to the player to close it OR the player must have permission: "tickethub.staff".
     * /th close (ticket id).
     * 
     * @param player the player who's invoking this command
     * @param args   args0 - close | args1 - ticket ID
     */
    private void closeTicket(Player player, String[] args){
        try{

            Ticket editingTicket = super.plugin.getTicketSystem().getTicket(args[1]);
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
                return;
            }

            super.plugin.getTicketSystem().removeTicket(editingTicket.getTicketID());


        }catch(TicketNotFoundException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
        }catch(IndexOutOfBoundsException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th close (ticket id) ").text());
        }
    }

}