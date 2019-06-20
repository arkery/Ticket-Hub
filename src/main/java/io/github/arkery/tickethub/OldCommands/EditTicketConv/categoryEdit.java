package io.github.arkery.tickethub.OldCommands.EditTicketConv;

import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class categoryEdit extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Category: " + this.editingTicket.getTicketCategory());

        String all = "";
        for(String i : this.plugin.getCustomCategories()){
            all += " " +  i;
        }
        conv.getForWhom().sendRawMessage(ChatColor.DARK_AQUA + "[" + all + " ]");
        return ChatColor.AQUA + "Enter the new Category";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(this.plugin.getCustomCategories().contains(answer.toLowerCase())){
            this.editingTicket.setTicketCategory(answer.toLowerCase());
            return new OptionToEditMore(this.plugin, this.editingTicket);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
