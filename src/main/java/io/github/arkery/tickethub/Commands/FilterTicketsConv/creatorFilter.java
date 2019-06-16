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
import java.util.UUID;

@AllArgsConstructor
public class creatorFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

    @Override
    public String getPromptText(ConversationContext conv) {

        return ChatColor.AQUA + "Enter the username of the ticket creator to add as a filter condition";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        Player creator = Bukkit.getOfflinePlayer((UUID) this.plugin.getTicketSystem().getStoredData().getPlayerIdentifiers().getValue(answer)).getPlayer();

        if(creator.hasPlayedBefore()){
            this.filterConditions.put(Options.CREATOR, creator.getUniqueId());
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "This person has not joined this server!");
            return this;
        }

    }
}