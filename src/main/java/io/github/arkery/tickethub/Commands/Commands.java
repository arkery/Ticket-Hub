package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.Commands.NewTicketConv.titleNewTicket;
import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.CustomUtils.CommentsPageView;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.CustomUtils.TicketPageView;
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

public class Commands implements CommandExecutor {

    private TicketHub plugin;
    private ConversationFactory conversationFactory;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM.dd.yy");


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
                    case "mytickets":
                        this.myTickets(player, args);
                        return false;
                    case "details":
                        this.ticketDetails(player, args);
                        return false;
                    case "stats":
                        this.statistics(player);
                        return false;
                    case "edit":
                        return false;
                    case "all":
                        this.allTickets(player, args);
                        return false;
                    case "filter":
                        return false;
                    case "assigned":
                        this.assignedTickets(player, args);
                        return false;
                    case "save":
                        this.manualBackUpTickets(player, args);
                        return false;
                    case "close":
                        this.closeTicket(player, args);
                        return false;
                    case "edittitle":
                        this.ticketEditTitle(player, args);
                        this.ticketDetails(player, args);
                        return false;
                    case "editstatus":
                        this.ticketEditStatus(player, args);
                        this.ticketDetails(player, args);
                        return false;
                    case "editpriority":
                        this.ticketEditPriority(player, args);
                        this.ticketDetails(player, args);
                        return false;
                    case "editcontacts":
                        this.ticketEditContacts(player, args);
                        this.ticketDetails(player, args);
                        return false;
                    case "editdesc":
                        this.ticketEditDescription(player, args);
                        this.ticketDetails(player, args);
                        return false;
                    case "editassigned":
                        this.ticketEditAssignedTo(player, args);
                        this.ticketDetails(player, args);
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
            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"\nTicketHub Menu").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   new", "Click here to create a new ticket", "/th new ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " create a new ticket")).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   mytickets", "Click here to see your current tickets", "/th mytickets ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See your tickets")).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   details", "Click here to see individual ticket details", "/th details ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See individual ticket details")).text());

            if(player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.AQUA,"Staff Menu").text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   stats", "Click here to see ticket    stats", "/th stats", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " See Hub Statistics")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   edit").add(new Clickable(ChatColor.BLUE, " Edit a ticket")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   all", "Click here to all tickets", "/th all ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See all tickets")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   filter").add(new Clickable(ChatColor.BLUE, " Filter all Tickets")).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   assigned", "Click here to your assigned tickets", "/th assigned ", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See all your assigned tickets")).text());
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
     * All tickets belonging to the player who invoked this method.
     * /th mytickets (page) 
     * 
     * @param player the player that's calling this
     * @param args the command called | args0 - mytickets | args1 - page number (if applicable)
     */
    private void myTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                if(plugin.getTicketSystem().getStoredData().getAllTickets().getAllX(player.getUniqueId()).isEmpty()){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou have no tickets!").text());
                    return;
                }

                List<Ticket> playerTickets = plugin.getTicketSystem().getStoredData().getAllTickets().getAllX(player.getUniqueId());
                int page = 1;

                if(args.length == 1){
                    int totalPages = (int) Math.ceil((double) playerTickets.size() / 9);
                    player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nYour Tickets   [" + 1 + "/" + totalPages + "]").text());
                    new TicketPageView().ticketPageView(player, 1, playerTickets);
                }
                else{
                    page = Integer.parseInt(args[1]);
                    int totalPages = (int) Math.ceil((double) playerTickets.size() / 9);
                    player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nYour Tickets   [" + page + "/" + totalPages + "]").text());
                    new TicketPageView().ticketPageView(player, page, playerTickets);
                }

            }catch(NumberFormatException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th mytickets (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
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
     * /th all (page)
     * 
     * @param player the player who's invoking this method
     * @param args args0 - all | args1 - page (if applicable)
     */
    private void allTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                List<Ticket> allTickets = plugin.getTicketSystem().getStoredData().getAllTickets().getAll();
                if(allTickets.isEmpty()){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
                    return;
                }

                int page = 1;

                if(args.length == 1){
                    int totalPages = (int) Math.ceil((double) allTickets.size() / 9);
                    player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nTickets   [" + 1 + "/" + totalPages + "]").text());
                    new TicketPageView().ticketPageView(player, 1, allTickets);
                }
                else{
                    int totalPages = (int) Math.ceil((double) allTickets.size() / 9);
                    page = Integer.parseInt(args[1]);
                    player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nTickets   [" + page + "/" + totalPages + "]").text());
                    new TicketPageView().ticketPageView(player, page, allTickets);
                }

            }catch(NumberFormatException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th all (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    /**
     * All tickets that are assigned to the player that's invoking this command.
     * They have to be staff or have permission: "tickethub.staff".
     * /th assigned (page).
     * 
     * @param player player who's invoking this command 
     * @param args args0 - assigned | args1 - page (if applicable)
     */
    private void assignedTickets(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{
                EnumMap<Options, Object> conditions = new EnumMap<>(Options.class);
                conditions.put(Options.ASSIGNEDTO, player.getUniqueId());

                List<Ticket> assignedTickets = this.plugin.getTicketSystem().filterTickets(conditions);
                if(assignedTickets.isEmpty()){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
                    return;
                }

                int page = 1;

                if(args.length == 1){
                    int totalPages = (int) Math.ceil((double) assignedTickets.size() / 9);
                    player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nTickets   [" + 1 + "/" + totalPages + "]").text());
                    new TicketPageView().ticketPageView(player, 1, assignedTickets);
                }
                else{
                    int totalPages = (int) Math.ceil((double) assignedTickets.size() / 9);
                    page = Integer.parseInt(args[1]);
                    player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nTickets   [" + page + "/" + totalPages + "]").text());
                    new TicketPageView().ticketPageView(player, page, assignedTickets);
                }

            }catch(NumberFormatException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th assigned (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
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
                Ticket displayTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                if(!displayTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                    player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                    return;
                }

                String ticketContactsAsString = "";
                if(displayTicket.getTicketContacts().isEmpty() || displayTicket.getTicketContacts() == null){
                    ticketContactsAsString = "No Additional Contacts!"; 
                }else{
                    for(UUID i: displayTicket.getTicketContacts()){
                        ticketContactsAsString += " " + this.plugin.getTicketSystem().getUserName(i);
                    }
                }
            
                player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nDetails for Ticket: ").add(new Clickable(ChatColor.AQUA, displayTicket.getTicketID())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Title: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketTitle())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Status: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketStatus().toString())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Priority: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketPriority().toString())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Category: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketCategory())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Contacts:").add(new Clickable(ChatColor.BLUE, ticketContactsAsString)).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Description: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketDescription())).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Creator: ").add(new Clickable(ChatColor.BLUE, this.plugin.getTicketSystem().getUserName(displayTicket.getTicketCreator()))).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Assigned To: ").add(new Clickable(ChatColor.BLUE, this.plugin.getTicketSystem().getUserName(displayTicket.getTicketAssignedTo()))).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Last Updated On: ").add(new Clickable(ChatColor.BLUE, dateFormat.format(displayTicket.getTicketDateLastUpdated()))).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Created On: ").add(new Clickable(ChatColor.BLUE, dateFormat.format(displayTicket.getTicketDateCreated()))).text());

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
     * @param args   args0 - edittitle | args1 - ticket id | args2->argsn - new ticket title
     */
    private void ticketEditTitle(Player player, String[] args){
        try{
            Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]); 
            String newTitle = ""; 
            
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                return;
            }

            for(int i = 2; i < args.length; i++){
                newTitle += " " + args[i]; 
            }
            
            editingTicket.setTicketTitle(newTitle);
            editingTicket.setTicketDateLastUpdated(new Date());
            this.plugin.getTicketSystem().updateTicket(editingTicket);
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
                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]); 
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
                this.plugin.getTicketSystem().updateTicket(editingTicket);
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
                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]); 
                
                if(!this.plugin.getCustomCategories().contains(args[2].toLowerCase())){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "Invalid Category!").text());
                    return; 
                }

                editingTicket.setTicketCategory(args[2].toLowerCase());
                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);
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
                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]); 
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
                this.plugin.getTicketSystem().updateTicket(editingTicket);
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
            Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);  
            List<UUID> newContacts = new ArrayList<>(); 
            
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
                return;
            }

            if(args.length > 5){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: You can only have three contacts!").text());
            }

            for(int i = 2; i < args.length; i++){
                newContacts.add(this.plugin.getTicketSystem().getUserUUID(args[i]));
            }
            
            editingTicket.setTicketContacts(newContacts);
            editingTicket.setTicketDateLastUpdated(new Date());
            this.plugin.getTicketSystem().updateTicket(editingTicket);
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
            Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]); 
            String newDescription = ""; 
            
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                return;
            }

            for(int i = 2; i < args.length; i++){
                newDescription += " " + args[i]; 
            }
            
            editingTicket.setTicketDescription(newDescription);
            editingTicket.setTicketDateLastUpdated(new Date());
            this.plugin.getTicketSystem().updateTicket(editingTicket);
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
                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);  
                
                editingTicket.setTicketAssignedTo(this.plugin.getTicketSystem().getUserUUID(args[1]));
                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);
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
     * View a ticket's comments.
     * Ticket must belong to the player or player must have permission: "tickethub.staff".
     * /th comments (ticket id) (page)
     * 
     * @param player The player who's invoking this method
     * @param args   args0 - comments | args1 - ticket id | args2 - page number
     */
    private void ticketViewComments(Player player, String[] args){
        try{
            Ticket viewingTicket = this.plugin.getTicketSystem().getTicket(args[1]); 
            int page = 0; 

            if(args.length > 2){
                page = Integer.parseInt(args[2]); 
            }

            if(viewingTicket.getTicketComments().isEmpty() || viewingTicket.getTicketComments() == null){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nTicket has no comments! ").text());
                return; 
            }

            new CommentsPageView().pageView(player, page, viewingTicket.getTicketComments());

        }catch(TicketNotFoundException e){
                player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Ticket was not found! ").text());
        }catch(IndexOutOfBoundsException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editassigned (ticket ID) (Person assigned to)").text());
        }catch(NumberFormatException e){
            player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry: Format as /th editassigned (ticket ID) (Person assigned to)").text());
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
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Low: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getLowPriority())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Medium: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getMediumPriority())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   High: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getHighPriority())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"Status").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   New: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getOpened())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   InProgress: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getInProgress())).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Resolved: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getResolved())).text());
        
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

                this.plugin.getTicketSystem().saveTickets(args[1]);
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

            Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);
            if(!editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.sendMessage(ChatColor.RED + "You do not have permissions to close this ticket!");
                return;
            }

            this.plugin.getTicketSystem().removeTicket(editingTicket.getTicketID());

        }catch(TicketNotFoundException e){
            player.sendMessage(ChatColor.RED + "Could not find ticket!");
        }catch(IndexOutOfBoundsException e){
            player.sendMessage(ChatColor.RED + "Please enter in the format of "
                    + ChatColor.DARK_GREEN + "/th close (Ticket ID)");
        }
    }

}