package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class BackGroundCommandUntil {

    TicketHub plugin;

    /**
     * Displays list in player friendly page format
     * To be used by other methods
     *
     * @param player         the player who's sending this command
     * @param page           the command input
     * @param displayTickets list to display as
     */
    protected void ticketPageView(Player player, int page, List<Ticket> displayTickets) {

        DateFormat dateFormat = new SimpleDateFormat("MM/dd");

        //9 entries per page
        int totalPages = (int) Math.ceil((double) displayTickets.size() / 9);
        int topOfPage = (page - 1) * 9;
        int bottomOfPage = 9 * page - 1;

        if (page > 0 && page <= totalPages) {
            player.sendMessage(ChatColor.GOLD + "Page: [" + page + "/" + totalPages + "]");
            if (displayTickets.size() < topOfPage + 9) {
                bottomOfPage = displayTickets.size();
            }

            //60 characters per line
            player.sendMessage(ChatColor.GOLD + "\n[ ID Status Priority Category DateUpdated DateCreated ]");
            player.sendMessage("  ");
            for (int i = topOfPage; i < bottomOfPage; i++) {

                TextComponent ticketInfo = new TextComponent(
                        displayTickets.get(i).getTicketID() +
                                " " + displayTickets.get(i).getTicketStatus().toString() +
                                " " + displayTickets.get(i).getTicketPriority().toString() +
                                " " + displayTickets.get(i).getTicketCategory() +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()) +
                                " " + dateFormat.format(displayTickets.get(i).getTicketDateCreated()
                        ));
                ticketInfo.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                ticketInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to see ticket details").create()));
                ticketInfo.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/th ticketdetails " + displayTickets.get(i).getTicketID()));
                player.spigot().sendMessage(ticketInfo);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Invalid Page");
        }
    }


    /**
     * Click Command - Edit ticket Title;
     * /th cEditTitle <ticketID> <new Title>
     *
     * @param player
     * @param args
     */
    protected void clickEditTitle(Player player, String[] args){
        if(args.length < 3){
            player.sendMessage(ChatColor.RED + "Please enter in the format of "
                    + ChatColor.DARK_GREEN + "/th cEditTitle <TicketID> <New Title>");
            return;
        }

        if(!player.hasPermission("tickethub.staff")){
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
            return;
        }

        try{

            Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);
            editingTicket.setTicketTitle(args[2]);
            editingTicket.setTicketDateLastUpdated(new Date());
            this.plugin.getTicketSystem().updateTicket(editingTicket);

        }catch(TicketNotFoundException e){
            player.sendMessage(ChatColor.RED + "Could not find ticket!");
        }
    }
}