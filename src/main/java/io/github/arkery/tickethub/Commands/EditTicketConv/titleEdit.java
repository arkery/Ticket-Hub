package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class titleEdit extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;


    @Override
    public String getPromptText(ConversationContext conv) {

        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Title: " + this.editingTicket.getTicketTitle());

        return ChatColor.GOLD + "Enter a new Ticket Title";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(!answer.isEmpty() || !answer.equals("")){
            this.editingTicket.setTicketTitle(answer);
            conv.setSessionData(Options.TICKET, this.editingTicket);
            return new OptionToEditMore(plugin, editingTicket);
        }else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}

