package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.Commands.NewTicketConv.titleNewTicket;
import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.CustomUtils.TicketPageView;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
                        this.saveAllTickets(player, args);
                        return false;
                    case "close":
                        this.closeTicket(player, args);
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
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   new", "Click here to create a new ticket", HoverEvent.Action.SHOW_TEXT, "/th new ", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " create a new ticket").text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   mytickets", "Click here to see your current tickets", HoverEvent.Action.SHOW_TEXT, "/th mytickets (page)", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See your tickets").text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   details", "Click here to see individual ticket details", HoverEvent.Action.SHOW_TEXT, "/th details (ticket id)", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See individual ticket details").text()).text());

            if(player.hasPermission("tickethub.staff")){
                player.spigot().sendMessage(new Clickable( ChatColor.AQUA,"Staff Menu").text());
                //player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   stats", "Click here to see ticket    stats", HoverEvent.Action.SHOW_TEXT, "/th stats", ClickEvent.Action.RUN_COMMAND).add(new Clickable(ChatColor.BLUE, " See Hub Statistics").text()).text());
                //player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   edit").add(new Clickable(ChatColor.BLUE, " Edit a ticket").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   all", "Click here to all tickets", HoverEvent.Action.SHOW_TEXT, "/th all (page)", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See all tickets").text()).text());
                //player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   filter").add(new Clickable(ChatColor.BLUE, " Filter all Tickets").text()).text());
                //player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   assigned", "Click here to your assigned tickets", HoverEvent.Action.SHOW_TEXT, "/th assigned (page)", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " See all your assigned tickets").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   close", "Click here to close a ticket", HoverEvent.Action.SHOW_TEXT, "/th close (ticket id)", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " Close a ticket").text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   save", "Click here to see ticket stats", HoverEvent.Action.SHOW_TEXT, "/th save filename", ClickEvent.Action.SUGGEST_COMMAND).add(new Clickable(ChatColor.BLUE, " Save Tickets Manually").text()).text());
            }
        }else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "You don't have permissions to do this!").text());
        }
    }

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
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry: Format as /th all (page) ").text());
            }catch(NullPointerException e){
                player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            }
        }
        else{
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nYou don't have permissions to do this!").text());
        }
    }

    private void ticketDetails(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{
                Ticket displayTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                if(!displayTicket.getTicketCreator().equals(this.plugin.getTicketSystem().getUserUUID(player.getName())) &&
                        !player.hasPermission("tickethub.staff")){
                    player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                    return;
                }

                String ticketContactsAsString = "";
                for(UUID i: displayTicket.getTicketContacts()){
                    ticketContactsAsString += " " + this.plugin.getTicketSystem().getUserName(i);
                }

                player.spigot().sendMessage(new Clickable( ChatColor.AQUA, "\nDetails for Ticket: ").add(new Clickable(ChatColor.AQUA, displayTicket.getTicketID()).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Title: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketTitle()).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Status: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketStatus().toString()).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Priority: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketPriority().toString()).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Category: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketCategory()).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Contacts:").add(new Clickable(ChatColor.BLUE, ticketContactsAsString).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Description: ").add(new Clickable(ChatColor.BLUE, displayTicket.getTicketDescription()).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "\n   Creator: ").add(new Clickable(ChatColor.BLUE, this.plugin.getTicketSystem().getUserName(displayTicket.getTicketCreator())).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Assigned To: ").add(new Clickable(ChatColor.BLUE, this.plugin.getTicketSystem().getUserName(displayTicket.getTicketAssignedTo())).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Last Updated On: ").add(new Clickable(ChatColor.BLUE, dateFormat.format(displayTicket.getTicketDateLastUpdated())).text()).text());
                player.spigot().sendMessage(new Clickable( ChatColor.GOLD, "   Created On: ").add(new Clickable(ChatColor.BLUE, dateFormat.format(displayTicket.getTicketDateCreated())).text()).text());



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




    private void statistics(Player player){

        if(player.hasPermission("tickethub.staff")){
            /*
            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"\nPriority").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Low: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getLowPriority()).text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Medium: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getMediumPriority()).text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   High: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getHighPriority()).text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.AQUA,"Status").text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   New: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getOpened()).text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   InProgress: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getInProgress()).text()).text());
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Resolved: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getResolved()).text()).text());
        */
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "   Total Tickets: ").add(new Clickable(ChatColor.BLUE, "" + this.plugin.getTicketSystem().getStoredData().getAllTickets().size()).text()).text());

        }
        else {
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }

    private void saveAllTickets(Player player, String[] args){

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
            player.sendMessage(ChatColor.RED + "\nYou do not have permissions to do this");
        }
    }

    private void closeTicket(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);
                if(editingTicket.getTicketStatus().equals(Status.CLOSED)){
                    player.sendMessage(ChatColor.RED + "This ticket is closed!");
                    return;
                }

                editingTicket.setTicketStatus(Status.CLOSED);

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().getStoredData().getTicketsToClose().put(editingTicket.getTicketCreator(), editingTicket.getTicketID());
                this.plugin.getTicketSystem().updateTicket(editingTicket);
                this.plugin.getTicketSystem().removeTicket(editingTicket.getTicketID());

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cCloseTicket <TicketID>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

}
