package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.EnumMap;

@AllArgsConstructor
public class categoryFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

    @Override
    public String getPromptText(ConversationContext conv) {

        String all = "";
        for(String i : this.plugin.getCustomCategories()){
            all += " " +  i;
        }
        conv.getForWhom().sendRawMessage(ChatColor.DARK_AQUA + "[" + all + " ]");
        return ChatColor.AQUA + "Enter the category to add as filter condition";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(this.plugin.getCustomCategories().contains(answer.toLowerCase())){

            filterConditions.put(Options.CATEGORY, answer.toLowerCase());
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
