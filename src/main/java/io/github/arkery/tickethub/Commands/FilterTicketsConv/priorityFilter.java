package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.CustomUtils.ChatText;
import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.EnumMap;

@AllArgsConstructor
public class priorityFilter extends StringPrompt {

    private TicketHub plugin;
    private Player player; 
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page; 

    @Override
    public String getPromptText(ConversationContext conv) {
        
        this.player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\nPriority Options: LOW MEDIUM HIGH").text());
        this.player.spigot().sendMessage(new ChatText(ChatColor.AQUA, "\nEnter the priority to add to filter or enter 'cancel' to cancel adding").text());
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        Priority filterPriority;

        switch(answer.toLowerCase()){
            case "low":
                filterPriority = Priority.LOW; 
                break;
            case "medium":
                filterPriority = Priority.MEDIUM; 
                break;
            case "high":
                filterPriority = Priority.HIGH; 
                break;
            case "cancel":
                this.player.spigot().sendMessage(new ChatText(ChatColor.DARK_PURPLE, "\nBack to Filter View").text());
                return new Menu(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            default:
                this.player.spigot().sendMessage(new ChatText(ChatColor.RED, "\nInvalid Entry!").text());
                return this;
        }
        this.filterConditions.put(Options.PRIORITY, filterPriority);
        return new Menu(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
    }
}
