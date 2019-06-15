package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

@AllArgsConstructor
public class OptionToEditMore extends BooleanPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Do you want to edit other parts of the Ticket: " + this.editingTicket.getTicketID() + " ?";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conv, boolean answer) {
        if(!answer){
            this.plugin.getTicketSystem()
                    .getStoredData().getAllTickets()
                    .get(this.editingTicket.getTicketCreator())
                    .replace(this.editingTicket.getTicketID(), this.editingTicket);
            conv.getForWhom().sendRawMessage(ChatColor.GREEN + "Ticket Updated!");
            return END_OF_CONVERSATION;
        }
        else{
            return new EditMenu(this.plugin, this.editingTicket);
        }
    }

}
