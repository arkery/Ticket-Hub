package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class categoryNewTicket extends StringPrompt {

    private TicketHub plugin;
    public categoryNewTicket(TicketHub plugin){
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        String all = "";
        for(String i : this.plugin.getCustomCategories()){
            all += " " +  i;
        }
        conv.getForWhom().sendRawMessage("\n" + ChatColor.GOLD + "Available Options: " + ChatColor.DARK_AQUA + all);
        return ChatColor.GOLD + "Enter Category of Ticket: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(plugin.getCustomCategories().contains(answer.toLowerCase())){
            conv.setSessionData(Options.CATEGORY, answer);
            return new contactNewTicket(plugin, true);
        }else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
