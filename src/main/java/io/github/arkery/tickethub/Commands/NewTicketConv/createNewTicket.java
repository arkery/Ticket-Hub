package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class createNewTicket extends MessagePrompt {
    private TicketHub plugin;

    public createNewTicket(TicketHub plugin){
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        try{
            if(
                conv.getAllSessionData().containsKey(Options.TICKETCONTACTS) &&
                conv.getAllSessionData().containsKey(Options.TICKETCATEGORY) &&
                conv.getAllSessionData().containsKey(Options.TICKETDESCRIPTION) &&
                conv.getAllSessionData().containsKey(Options.TICKETPRIORITY) &&
                conv.getAllSessionData().containsKey(Options.TICKETTITLE)
            ){
                Player player = (Player) conv.getForWhom();
                DateFormat dateFormat = new SimpleDateFormat("MMddyyHHmmss"); // player IGN + 12 numbers

                Ticket newTicket = new Ticket(
                         player.getName() + dateFormat.format(new Date()),           //ID
                        (String) conv.getSessionData(Options.TICKETTITLE),                  //TITLE
                        Status.OPENED,                                                      //Status
                        (String) conv.getSessionData(Options.TICKETCATEGORY),               //Category
                        (Priority) conv.getSessionData(Options.TICKETPRIORITY),             //Priority
                        (ArrayList) conv.getSessionData(Options.TICKETCONTACTS),            //Contacts
                        (String) conv.getSessionData(Options.TICKETDESCRIPTION),            //Description
                        player.getUniqueId(),                                               //Assigned To
                        player.getUniqueId(),                                               //Creator
                        new Date(),                                                         //Date Created
                        new Date(),                                                         //Date Updated
                        new ArrayList<>()                                                   //Comments
                );

                List<Ticket> playerTickets = new ArrayList<>();

                //Check if the player has other tickets belonging to them
                if(plugin.getTicketSystem().getStoredTickets().getAllTickets().containsKey(player.getUniqueId())){
                    playerTickets = plugin.getTicketSystem().getStoredTickets().getAllTickets().get(player.getUniqueId());
                }

                //Add the ticket into the Hub
                playerTickets.add(newTicket);
                plugin.getTicketSystem().getStoredTickets().getAllTickets().put(player.getUniqueId(), playerTickets);

                //Update Hub Statistics
                plugin.getTicketSystem().getStoredTickets().addNewPriorityStats((Priority) conv.getSessionData(Options.TICKETPRIORITY));
                plugin.getTicketSystem().getStoredTickets().addnewStatusStats(Status.OPENED);
                conv.getForWhom().sendRawMessage(ChatColor.GREEN + "Ticket has now been created");
                return ChatColor.GREEN + "Ticket ID is: " + player.getName() + dateFormat.format(new Date());
            }
            else{
                return ChatColor.RED + "Unable to retrieve data for ticket creation";
            }

        }catch(IllegalArgumentException e) {
            e.printStackTrace();
            return ChatColor.RED + "Error: Unable to Create Ticket";
        }catch(NullPointerException e){
            e.printStackTrace();
            return ChatColor.RED + "Error: Unable to retrieve Data";
        }
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext conv) {
        return END_OF_CONVERSATION;
    }
}
