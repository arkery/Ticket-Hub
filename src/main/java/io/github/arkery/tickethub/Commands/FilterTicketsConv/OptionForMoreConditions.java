package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.EnumMap;

@AllArgsConstructor
public class OptionForMoreConditions extends BooleanPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

    @Override
    public String getPromptText(ConversationContext conv) {

        if(this.filterConditions.isEmpty()){
            conv.getForWhom().sendRawMessage(ChatColor.BLUE + "No Applied Filter Conditions");
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.BLUE + "Applied Filter Conditions: ");
            for(Options i: this.filterConditions.keySet()){
                conv.getForWhom().sendRawMessage(ChatColor.GRAY + "-" + i.toString());
            }
        }

        return ChatColor.GOLD + "Do you want to modify the current filter conditions? ";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, boolean answer) {
        if(answer){
            return new AddOrRemoveConditionOption(this.plugin, this.filterConditions);
        }
        else{
            return new viewFilter(this.plugin, this.filterConditions);
        }
    }

}
