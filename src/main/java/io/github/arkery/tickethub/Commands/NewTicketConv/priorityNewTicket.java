package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.TicketHub;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class priorityNewTicket extends StringPrompt {
    private TicketHub plugin;
    public priorityNewTicket(TicketHub plugin){
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Enter Priority of Ticket: " + ChatColor.DARK_AQUA + "[ LOW | MEDIUM | HIGH ]";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(answer.equalsIgnoreCase("low")){
            conv.setSessionData(Options.TICKETPRIORITY, Priority.LOW);
            return new categoryNewTicket(plugin);
        }
        else if(answer.equalsIgnoreCase("medium")){
            conv.setSessionData(Options.TICKETPRIORITY, Priority.MEDIUM);
            return new categoryNewTicket(plugin);
        }
        else if(answer.equalsIgnoreCase("high")){
            conv.setSessionData(Options.TICKETPRIORITY, Priority.HIGH);
            return new categoryNewTicket(plugin);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
