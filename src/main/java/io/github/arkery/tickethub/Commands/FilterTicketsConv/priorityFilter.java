package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Commands.EditTicketConv.OptionToEditMore;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.EnumMap;

@AllArgsConstructor
public class priorityFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;


    @Override
    public String getPromptText(ConversationContext conv) {
          conv.getForWhom().sendRawMessage(ChatColor.GOLD + "Priority Options: [ "
                + Priority.LOW.toString()
                + " | " + Priority.MEDIUM.toString()
                + " | " + Priority.HIGH.toString()
                + " ]");
        return ChatColor.GOLD + "Enter priority to add to filter condition";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(answer.equalsIgnoreCase(Priority.LOW.toString())){
            this.filterConditions.put(Options.PRIORITY, Priority.LOW);
            return new OptionForMoreConditions(this.plugin, this.filterConditions);

        }
        else if(answer.equalsIgnoreCase(Priority.MEDIUM.toString())){
            this.filterConditions.put(Options.PRIORITY, Priority.MEDIUM);
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
        else if(answer.equalsIgnoreCase(Priority.HIGH.toString())){
            this.filterConditions.put(Options.PRIORITY, Priority.HIGH);
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }

}
