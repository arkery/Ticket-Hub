package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class descriptionEdit extends StringPrompt {
    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Description: " + this.editingTicket.getTicketDescription());
        return ChatColor.GOLD + "Enter a new ticket description: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(!answer.isEmpty() || !answer.equals("")){
            this.editingTicket.setTicketDescription(answer);
            return new OptionToEditMore(plugin, editingTicket);
        }else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
