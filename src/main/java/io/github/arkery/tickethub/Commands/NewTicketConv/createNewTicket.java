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
                conv.getAllSessionData().containsKey(Options.CONTACTS) &&
                conv.getAllSessionData().containsKey(Options.CATEGORY) &&
                conv.getAllSessionData().containsKey(Options.DESCRIPTION) &&
                conv.getAllSessionData().containsKey(Options.PRIORITY) &&
                conv.getAllSessionData().containsKey(Options.TITLE)
            ){
                Player player = (Player) conv.getForWhom();
                DateFormat dateFormat = new SimpleDateFormat("MMddyyHHmmss"); // player IGN + 12 numbers

                Ticket newTicket = new Ticket(
                         player.getName() + dateFormat.format(new Date()),           //ID
                        (String) conv.getSessionData(Options.TITLE),                  //TITLE
                        Status.OPENED,                                                      //Status
                        (String) conv.getSessionData(Options.CATEGORY),               //Category
                        (Priority) conv.getSessionData(Options.PRIORITY),             //Priority
                        (ArrayList) conv.getSessionData(Options.CONTACTS),            //Contacts
                        (String) conv.getSessionData(Options.DESCRIPTION),            //Description
                        player.getUniqueId(),                                               //Assigned To
                        player.getUniqueId(),                                               //Creator
                        new Date(),                                                         //Date Created
                        new Date(),                                                         //Date Updated
                        new ArrayList<>()                                                   //Comments
                );

                HashMap<String, Ticket> playerTickets = new HashMap<>();

                //Check if the player has other tickets belonging to them
                if(plugin.getTicketSystem().getStoredData().getAllTickets().containsKey(player.getUniqueId())){
                    playerTickets = this.plugin.getTicketSystem().getStoredData().getAllTickets().get(player.getUniqueId());
                }


                //Add the ticket into the Hub
                playerTickets.put(newTicket.getTicketID(), newTicket);
                plugin.getTicketSystem().getStoredData().getAllTickets().put(player.getUniqueId(), playerTickets);

                //Update Hub Statistics
                plugin.getTicketSystem().getStoredData().addNewPriorityStats((Priority) conv.getSessionData(Options.PRIORITY));
                plugin.getTicketSystem().getStoredData().addnewStatusStats(Status.OPENED);
                return ChatColor.GREEN + "\nTicket has now been created!" + " Ticket ID: " + ChatColor.AQUA + player.getName() + dateFormat.format(new Date());
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
