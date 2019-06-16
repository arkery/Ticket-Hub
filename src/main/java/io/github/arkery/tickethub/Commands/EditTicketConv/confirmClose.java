package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.Date;

@AllArgsConstructor
public class confirmClose extends BooleanPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.LIGHT_PURPLE + "Are you sure you want to close ticket: " + editingTicket.getTicketID() + ChatColor.BOLD + "? \n Closing a ticket will prevent further updates/editing";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conv, boolean closeTheTicket) {
       if(closeTheTicket){

           this.plugin
                   .getTicketSystem()
                   .getStoredData()
                   .removeStatusStats(this.editingTicket.getTicketStatus());
           this.editingTicket.setTicketStatus(Status.CLOSED);
           this.editingTicket.setTicketDateLastUpdated(new Date());

           this.plugin.getTicketSystem()
                   .getStoredData().getTicketsToClose()
                   .put(this.editingTicket.getTicketCreator(), this.editingTicket.getTicketID());
           conv.getForWhom().sendRawMessage(ChatColor.DARK_PURPLE + "Ticket Closed!");

           return END_OF_CONVERSATION;
       }
       else{
           return new OptionToEditMore(this.plugin, this.editingTicket);
       }
    }
}
