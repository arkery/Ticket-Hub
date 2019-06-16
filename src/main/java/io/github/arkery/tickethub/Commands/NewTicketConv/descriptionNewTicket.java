package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class descriptionNewTicket extends StringPrompt {
    private TicketHub plugin;

    public descriptionNewTicket(TicketHub plugin){
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Enter Ticket Description: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        conv.setSessionData(Options.DESCRIPTION, answer);
        return new createNewTicket(plugin);
    }
}
