package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class titleNewTicket extends StringPrompt {
    private TicketHub plugin;

    public titleNewTicket(TicketHub plugin){
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        return "\n" + ChatColor.GOLD + "Enter the title of your new ticket: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        conv.setSessionData(Options.TITLE, answer);
        return new priorityNewTicket(plugin);
    }

}
