package io.github.arkery.tickethub.CustomUtils;

import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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

        if(displayTickets.isEmpty()){
            player.sendMessage(ChatColor.RED + "There are no tickets!");
            return;
        }

        //9 entries per page
        int totalPages = (int) Math.ceil((double) displayTickets.size() / 9);
        int topOfPage = (page - 1) * 9;
        int bottomOfPage = 9 * page - 1;

        if (page > 0 && page <= totalPages) {
            if (displayTickets.size() < topOfPage + 9) {
                bottomOfPage = displayTickets.size();
            }

            //60 characters per line
            for (int i = topOfPage; i < bottomOfPage; i++) {

                TextComponent ticketInfo = new TextComponent(
                        displayTickets.get(i).getTicketID() +
                                " " + displayTickets.get(i).getTicketStatus().toString() +
                                " " + displayTickets.get(i).getTicketPriority().toString() +
                                " " + displayTickets.get(i).getTicketCategory() +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateCreated()
                        ));
                ticketInfo.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                ticketInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to see ticket details").create()));
                ticketInfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/th ticketdetails " + displayTickets.get(i).getTicketID()));

                player.spigot().sendMessage(ticketInfo);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Invalid Page");
        }
    }
}