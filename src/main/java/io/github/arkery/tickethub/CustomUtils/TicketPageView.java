package io.github.arkery.tickethub.CustomUtils;

import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@NoArgsConstructor
public class TicketPageView {

    /**
     * Displays list in player friendly page format
     * To be used by other methods
     *
     * @param player         the player who's sending this command
     * @param page           the command input
     * @param displayTickets list to display as
     */
    public void ticketPageView(Player player, int page, List<Ticket> displayTickets) {

        DateFormat dateFormat = new SimpleDateFormat("MM.dd");
        int totalPages = (int) Math.ceil((double) displayTickets.size() / 9);
        int topOfPage = (page - 1) * 9;
        int bottomOfPage = 9 * page - 1;

        if (page > 0 && page <= totalPages) {
            if (displayTickets.size() < topOfPage + 9) {
                bottomOfPage = displayTickets.size();
            }

            //60 characters per line
            for (int i = topOfPage; i < bottomOfPage; i++) {

                String ticketID = displayTickets.get(i).getTicketID(); 
                if(ticketID.length() > 13){
                    ticketID = ticketID.substring(0, 9) + "..."; 
                }

                player.spigot().sendMessage(new Clickable(
                ChatColor.GRAY, 
                    ticketID +
                    " " + displayTickets.get(i).getTicketStatus().toString() +
                    " " + displayTickets.get(i).getTicketPriority().toString() +
                    " " + displayTickets.get(i).getTicketCategory() +
                    " " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                    " " + dateFormat.format(displayTickets.get(i).getTicketDateCreated()),
                displayTickets.get(i).getTicketID() +
                    "\n" + "Brief Details" +
                    "\n" + "    Title: " + displayTickets.get(i).getTicketTitle() +
                    "\n" + "    Status: " + displayTickets.get(i).getTicketStatus().toString() +
                    "\n" + "    Priority: " + displayTickets.get(i).getTicketPriority().toString() +
                    "\n" + "    Category: " + displayTickets.get(i).getTicketCategory() +
                    "\n" + "    Last Updated On: " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                    "\n" + "    Date Created:" + dateFormat.format(displayTickets.get(i).getTicketDateCreated()) +
                    "\n" + "Click here to look at full ticket details and/or edit the ticket",
                "/th details " + displayTickets.get(i).getTicketID(),
                ClickEvent.Action.RUN_COMMAND
                ).text());

            }
        } else {
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "Invalid Page!").text());
        }
    }
}

        /*

                TextComponent ticketInfo = new TextComponent(
                        displayTickets.get(i).getTicketID() +
                                " " + displayTickets.get(i).getTicketStatus().toString() +
                                " " + displayTickets.get(i).getTicketPriority().toString() +
                                " " + displayTickets.get(i).getTicketCategory() +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateCreated()
                        ));
                ticketInfo.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                ticketInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        displayTickets.get(i).getTicketID() +
                        "\n" + displayTickets.get(i).getTicketTitle() +
                        "\n" + displayTickets.get(i).getTicketStatus().toString() +
                        "\n" + displayTickets.get(i).getTicketPriority().toString() +
                        "\n" + displayTickets.get(i).getTicketCategory() +
                        "\n" + displayTickets.get(i).getTicketDescription() +
                        "\n" + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                        "\n" + dateFormat.format(displayTickets.get(i).getTicketDateCreated())).create()));
                ticketInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/th details " + displayTickets.get(i).getTicketID()));

                player.spigot().sendMessage(ticketInfo);
        */