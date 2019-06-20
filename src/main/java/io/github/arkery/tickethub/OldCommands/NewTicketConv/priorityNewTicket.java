package io.github.arkery.tickethub.OldCommands.NewTicketConv;

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
        conv.getForWhom().sendRawMessage("\n" + ChatColor.GOLD + "Available Options: " + ChatColor.DARK_AQUA + "Low Medium High");
        return ChatColor.GOLD + "Enter Priority of Ticket ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(answer.equalsIgnoreCase("low")){
            conv.setSessionData(Options.PRIORITY, Priority.LOW);
            return new categoryNewTicket(plugin);
        }
        else if(answer.equalsIgnoreCase("medium")){
            conv.setSessionData(Options.PRIORITY, Priority.MEDIUM);
            return new categoryNewTicket(plugin);
        }
        else if(answer.equalsIgnoreCase("high")){
            conv.setSessionData(Options.PRIORITY, Priority.HIGH);
            return new categoryNewTicket(plugin);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
