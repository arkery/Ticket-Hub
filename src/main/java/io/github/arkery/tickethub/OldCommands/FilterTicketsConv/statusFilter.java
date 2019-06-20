package io.github.arkery.tickethub.OldCommands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.EnumMap;

@AllArgsConstructor
public class statusFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;


    @Override
    public String getPromptText(ConversationContext conv) {
        conv.getForWhom().sendRawMessage(ChatColor.GOLD + "Status Options: [ "
                + Status.OPENED.toString()
                + " | " + Status.INPROGRESS.toString()
                + " | " + Status.RESOLVED.toString()
                + " ]");
        return ChatColor.GOLD + "Enter status to add to filter condition";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if (answer.equalsIgnoreCase(Status.OPENED.toString())) {
            this.filterConditions.put(Options.STATUS, Status.OPENED);
            return new OptionForMoreConditions(this.plugin, this.filterConditions);

        } else if (answer.equalsIgnoreCase(Priority.MEDIUM.toString())) {
            this.filterConditions.put(Options.STATUS, Status.INPROGRESS);
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        } else if (answer.equalsIgnoreCase(Priority.HIGH.toString())) {
            this.filterConditions.put(Options.STATUS, Status.RESOLVED);
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        } else {
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
