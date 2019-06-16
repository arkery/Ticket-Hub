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
public class AddOrRemoveConditionOption extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

    @Override
    public String getPromptText(ConversationContext conv) {

        return ChatColor.GOLD + "Enter 'add' or 'replace' to add or replaceKey a filter condition \n" +
                "Enter 'remove' to remove a filter condition";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(answer.equalsIgnoreCase("add") || answer.equalsIgnoreCase("replace")){
            return new FilterMenu(this.plugin, this.filterConditions);

        }
        else if(answer.equalsIgnoreCase("remove")){
            return new RemoveFilterCondition(this.plugin, this.filterConditions);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
    }
}

