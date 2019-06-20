package io.github.arkery.tickethub.OldCommands.FilterTicketsConv;

import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.EnumMap;

@AllArgsConstructor
public class assignedtoFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

    @Override
    public String getPromptText(ConversationContext conv) {

        return ChatColor.AQUA + "Enter the username of the person assigned to the ticket to add as a filter condition";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        try{
            this.filterConditions.put(Options.ASSIGNEDTO, this.plugin.getTicketSystem().getUserUUID(answer));
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }catch(PlayerNotFoundException e){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "This person has not joined this server!");
            return this;
        }
    }

}
