package io.github.arkery.tickethub.CustomUtils;

import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TicketListView {

    private boolean conversation;
    private Player player;
    private List<Ticket> tickets;
    private String command;
    private DateSetting dateSetting;
    private int page;
    private int totalPages;


    /**
     * Display Tickets in a full page format with page count & filter options.
     * This constructor should not be used inside of the conversation.
     *
     * @param player Player that's invoking
     * @param tickets List of tickets to display
     * @param command The command that was run earlier
     * @param dateSetting Filter setting for date (created/updated).
     * @param page The page to display
     * @param totalPages Total amount of pages possible
     */
    public TicketListView(Player player, List<Ticket> tickets, String command, DateSetting dateSetting, int page, int totalPages){
        this.player = player;
        this.tickets = tickets;
        this.command = command;
        this.dateSetting = dateSetting;
        this.page = page;
        this.totalPages = totalPages;
        this.conversation = false;
    }

    /**
     * Display Tickets in a full page format with page count & filter options.
     * This constructor should only be used inside of a conversation
     *
     * @param player Player that's invoking
     * @param tickets List of tickets to display
     * @param dateSetting Filter setting for date (created/updated).
     * @param page The page to display
     * @param totalPages Total amount of pages possible
     */
    public TicketListView(Player player, List<Ticket> tickets, DateSetting dateSetting, int page, int totalPages){
        this.player = player;
        this.tickets = tickets;
        this.dateSetting = dateSetting;
        this.page = page;
        this.totalPages = totalPages;
        this.conversation = true;
    }

    public void display(){
        if(this.tickets.isEmpty()){
            player.spigot().sendMessage(new ChatText( ChatColor.RED, "\nThere are no tickets!").text());
            return;
        }

        if(this.dateSetting.equals(DateSetting.CREATED)){
            tickets.sort(Comparator.comparing(Ticket::getTicketDateCreated));
            Collections.reverse(tickets);
            dateSetting = DateSetting.CREATED;
        }
        else if(this.dateSetting.equals(DateSetting.UPDATED)){
            tickets.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
            Collections.reverse(tickets);
            dateSetting = DateSetting.UPDATED;
        }

        if(this.conversation && (this.command == null || this.command.equals(""))){
            this.convCommandDisplay(this.player, this.tickets, this.page, this.totalPages);
        }
        else{
            this.regularCommandDisplay(this.player, this.tickets, this.command, this.dateSetting, this.page, this.totalPages);
        }
    }

    /**
     *
     * @param player
     * @param displayList
     * @param page
     * @param totalPages
     */
    private void convCommandDisplay(Player player, List<Ticket> displayList, int page, int totalPages){
        player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\n(Created |", "Click here to sort by date created", "created", ClickEvent.Action.RUN_COMMAND )
                .add(new ChatText(ChatColor.GOLD, " Updated )", "Click here to sort by date updated", "updated", ClickEvent.Action.RUN_COMMAND))
                .add(new ChatText( ChatColor.AQUA, " [" + page + "/" + totalPages + "]"))
                .text());
        this.ticketsPageFormat(player, page, displayList);

        //Navigation Arrows
        int next = page + 1; //bungee does not play nice with direct increments in ChatText
        int prev = page - 1;
        if(page !=1 && page != totalPages){

            player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\n<---", "Click here to go back to previous page", String.valueOf(prev), ClickEvent.Action.RUN_COMMAND )
                    .add("         ")
                    .add(new ChatText(ChatColor.GOLD, "--->", "Click here to go to next page", String.valueOf(next), ClickEvent.Action.RUN_COMMAND ))
                    .text());
        }
        else{
            if(page != 1 ){
                player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\n<---", "Click here to go back to previous page", String.valueOf(prev), ClickEvent.Action.RUN_COMMAND ).text());
            }else{
                player.spigot().sendMessage(new ChatText("    ").text());
            }

            if(page != totalPages){
                player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "--->", "Click here to go to next page", String.valueOf(next) , ClickEvent.Action.RUN_COMMAND ).text());
            }else{
                player.spigot().sendMessage(new ChatText("    ").text());
            }
        }
    }

    /**
     *
     *
     * @param player
     * @param displayList
     * @param command
     * @param dateSetting
     * @param page
     * @param totalPages
     */
    private void regularCommandDisplay(Player player, List<Ticket> displayList, String command, DateSetting dateSetting, int page, int totalPages){
        player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\n(Created |", "Click here to sort by date created", "/th " + command + " " + page + " created", ClickEvent.Action.RUN_COMMAND )
                .add(new ChatText(ChatColor.GOLD, " Updated )", "Click here to sort by date updated", "/th " + command + " " + page + " updated", ClickEvent.Action.RUN_COMMAND))
                .add(new ChatText( ChatColor.AQUA, " [" + page + "/" + totalPages + "]"))
                .text());
        this.ticketsPageFormat(player, page, displayList);

        //Navigation Arrows
        int next = page + 1; //bungee does not play nice with direct increments in ChatText
        int prev = page - 1;
        if(page !=1 && page != totalPages){

            player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\n<---", "Click here to go back to previous page", "/th " + command + " " + prev + " " + dateSetting.toString().toLowerCase(), ClickEvent.Action.RUN_COMMAND )
                    .add("         ")
                    .add(new ChatText(ChatColor.GOLD, "--->", "Click here to go to next page", "/th " + command + " " + next + " " + dateSetting.toString().toLowerCase(), ClickEvent.Action.RUN_COMMAND ))
                    .text());
        }
        else{
            if(page != 1 ){
                player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\n<---", "Click here to go back to previous page", "/th " + command + " " + prev + " " + dateSetting.toString().toLowerCase(), ClickEvent.Action.RUN_COMMAND ).text());
            }else{
                player.spigot().sendMessage(new ChatText("    ").text());
            }

            if(page != totalPages){
                player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "--->", "Click here to go to next page", "/th " + command + " " + next + " " + dateSetting.toString().toLowerCase(), ClickEvent.Action.RUN_COMMAND ).text());
            }else{
                player.spigot().sendMessage(new ChatText("    ").text());
            }
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
    private void ticketsPageFormat(Player player, int page, List<Ticket> displayTickets) {

        DateFormat dateFormat = new SimpleDateFormat("MM.dd");
        int entriesPerPage = 10;
        int totalPages = (int) Math.ceil((double) displayTickets.size() / entriesPerPage);
        int topOfPage = (page - 1) * entriesPerPage;
        int bottomOfPage = entriesPerPage * page - 1;

        if (page > 0 && page <= totalPages) {
            if (displayTickets.size() < topOfPage + 9) {
                bottomOfPage = displayTickets.size();
            }

            //60 characters per line
            for (int i = topOfPage; i < bottomOfPage; i++) {

                String ticketID = displayTickets.get(i).getTicketID();
                String ticketPriority = displayTickets.get(i).getTicketPriority().toString();
                String ticketStatus = displayTickets.get(i).getTicketStatus().toString() + "";
                String ticketCategory = displayTickets.get(i).getTicketCategory();
                if(ticketID.length() > 13){
                    ticketID = ticketID.substring(0, 9) + "...";
                }

                //Resize Priority
                if(ticketPriority.length() >= 3){
                    ticketPriority = ticketPriority.substring(0, 3);
                }

                //Resize Status
                if(ticketStatus.length() >=5){
                    ticketStatus = ticketStatus.substring(0, 6);
                }

                //Resize Category
                if(ticketCategory.length() >=5){
                    ticketCategory = ticketCategory.substring(0, 5);
                }
                if(ticketCategory.length() < 5){
                    for(int j = 0; j < 5 - ticketCategory.length(); j++){
                        ticketCategory += " ";
                    }
                }

                //Print
                player.spigot().sendMessage(new ChatText(
                        ChatColor.GRAY,
                        ticketID +
                                " " + ticketStatus +
                                " " + ticketPriority +
                                " " + ticketCategory +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateCreated()),
                        displayTickets.get(i).getTicketID() +
                                "\n" + "    Title: " + displayTickets.get(i).getTicketTitle() +
                                "\n" + "    Status: " + displayTickets.get(i).getTicketStatus().toString() +
                                "\n" + "    Priority: " + displayTickets.get(i).getTicketPriority().toString() +
                                "\n" + "    Category: " + displayTickets.get(i).getTicketCategory() +
                                "\n" + "    Last Updated On: " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                                "\n" + "    Date Created: " + dateFormat.format(displayTickets.get(i).getTicketDateCreated()) +
                                "\n" + "Click here to look at full ticket details and/or edit the ticket",
                        "/th details " + displayTickets.get(i).getTicketID(),
                        ClickEvent.Action.RUN_COMMAND
                ).text());

            }
        } else {
            player.spigot().sendMessage(new ChatText( ChatColor.RED, "Invalid Page!").text());
        }
    }
}