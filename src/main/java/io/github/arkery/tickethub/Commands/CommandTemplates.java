package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.CustomUtils.TicketPageView;
import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
public class CommandTemplates{
    
    TicketHub plugin; 

    /**
     * List view of tickets
     * 
     * @param player                     Player who invoked the method
     * @param args                       Player's commands. Args0 should be the baseCommand. args1 should always be the ticket ID. args2 should always be page #. args3 should always be sorting order.
     * @param displayTickets             The tickets that are to be displayed as a list.
     * @throws NumberFormatException     Thrown if args2 is not parseable as an int. 
     * @throws IndexOutOfBoundsException Thrown if They forgot parts of the command.
     * @throws NullPointerException      Thrown if accessing data is not initialized. 
     */
    protected void TicketListView(Player player, String[] args,  List<Ticket> displayTickets) throws NumberFormatException, IndexOutOfBoundsException, NullPointerException {

        if(displayTickets.isEmpty()){
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            return;
        }

        DateSetting setting = DateSetting.UPDATED;
        int page = 1;
        int totalPages = (int) Math.ceil((double) displayTickets.size() / 9);

        //If player did include page number and sort setting when running command. 
        if(args.length > 1){
            page = Integer.parseInt(args[1]);
        }

        if(args.length == 3){
            if(args[2].toLowerCase().equals("created")){
                displayTickets.sort(Comparator.comparing(Ticket::getTicketDateCreated));
                Collections.reverse(displayTickets);
                setting = DateSetting.CREATED; 
            }
            else if(args[2].toLowerCase().equals("updated")){
                displayTickets.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
                Collections.reverse(displayTickets);
                setting = DateSetting.UPDATED; 
            }
        }
        else{
            displayTickets.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
            Collections.reverse(displayTickets);
        }
       
        player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "\n(Created", "Click here to sort by date created", "/th " + args[0] + " " + page + " created", ClickEvent.Action.RUN_COMMAND )
            .add(new Clickable(ChatColor.GOLD, " Updated )", "Click here to sort by date updated", "/th " + args[0] + " " + page + " updated", ClickEvent.Action.RUN_COMMAND))
            .add(new Clickable( ChatColor.AQUA, " [" + page + "/" + totalPages + "]"))
            .text());
            new TicketPageView().ticketPageView(player, page, displayTickets);

    //Navigation Arrows
        if(page != 1 ){
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "<---", "Click here to go back to previous page", "/th " + args[0] + " " + (page--) + " " + setting.toString().toLowerCase(), ClickEvent.Action.RUN_COMMAND ).text());
        }else{
            player.spigot().sendMessage(new Clickable("    ").text()); 
        }

        player.spigot().sendMessage(new Clickable("                                                    ").text()); 

        if(page != totalPages){
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "--->", "Click here to go to next page", "/th " + args[0] + " " + (page++) + " " + setting.toString().toLowerCase(), ClickEvent.Action.RUN_COMMAND ).text());
        }else{
            player.spigot().sendMessage(new Clickable("    ").text()); 
        }
    }
}