package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

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
        Player assignedTo = Bukkit.getOfflinePlayer(answer).getPlayer();

        if(assignedTo.hasPlayedBefore() && assignedTo.hasPermission("tickethub.staff")){
            this.filterConditions.put(Options.ASSIGNEDTO, assignedTo.getUniqueId());
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
        else if(!assignedTo.hasPermission("tickethub.staff")){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "This person is not a staff member!");
            return this;
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "This person has not joined this server!");
            return this;
        }

    }

}
