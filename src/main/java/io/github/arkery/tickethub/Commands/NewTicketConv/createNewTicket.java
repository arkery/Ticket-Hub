package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.CustomUtils.ChatText;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import net.md_5.bungee.api.chat.ClickEvent;

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
                        (String) conv.getSessionData(Options.TITLE),                        //TITLE
                        Status.OPENED,                                                      //Status
                        (String) conv.getSessionData(Options.CATEGORY),                     //Category
                        (Priority) conv.getSessionData(Options.PRIORITY),                   //Priority
                        (Set) conv.getSessionData(Options.CONTACTS),                        //Contacts
                        (String) conv.getSessionData(Options.DESCRIPTION),                  //Description
                        player.getUniqueId(),                                               //Assigned To
                        player.getUniqueId(),                                               //Creator
                        new Date(),                                                         //Date Created
                        new Date()                                                          //Date Updated
                        //new ArrayList<>()                                                   //Comments
                );

                this.plugin.getTicketSystem().addTicket(newTicket);

                player.spigot().sendMessage(new ChatText(
                net.md_5.bungee.api.ChatColor.GREEN, 
                    "\nTicket has now been created!" + " Ticket ID: " + player.getName() + dateFormat.format(new Date()),
                newTicket.getTicketID() +
                    "\n" + "Brief Details" +
                    "\n" + "    Title: " + newTicket.getTicketTitle() +
                    "\n" + "    Status: " + newTicket.getTicketStatus().toString() +
                    "\n" + "    Priority: " + newTicket.getTicketPriority().toString() +
                    "\n" + "    Category: " + newTicket.getTicketCategory() +
                    "\n" + "    Last Updated On: " + dateFormat.format(newTicket.getTicketDateLastUpdated()) +
                    "\n" + "    Date Created:" + dateFormat.format(newTicket.getTicketDateCreated()) +
                    "\n" + "Click here to look at full ticket details and/or edit the ticket",
                "/th details " + newTicket.getTicketID(),
                ClickEvent.Action.RUN_COMMAND
                ).text());

                //return ChatColor.GREEN + "\nTicket has now been created!" + " Ticket ID: " + ChatColor.AQUA + player.getName() + dateFormat.format(new Date());
                return ""; 
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
