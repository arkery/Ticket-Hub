package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.List;

@AllArgsConstructor
public class statusFilter extends StringPrompt {

    private TicketHub plugin;
    private Player player; 
    private List<Ticket> displayList; //This must stay unordered
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page; 

    @Override
    public String getPromptText(ConversationContext conv) {
        
        this.player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "\nSTATUS Options: OPENED INPROGRESS RESOLVED").text());
        this.player.spigot().sendMessage(new Clickable(ChatColor.AQUA, "\nEnter the status to add to filter or enter 'cancel' to cancel adding").text());
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        Status filterStatus = Status.OPENED; 

        switch(answer.toLowerCase()){
            case "opened":
                filterStatus = Status.OPENED; 
                break;
            case "inprogress":
                filterStatus = Status.INPROGRESS; 
                break;
            case "resolved":
                filterStatus = Status.RESOLVED; 
                break;
            case "cancel":
                this.player.spigot().sendMessage(new Clickable(ChatColor.DARK_PURPLE, "\nCancelling adding Status To Filter").text());
                return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            default:
                this.player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid Entry!").text());
                return this;
        }
        this.filterConditions.put(Options.STATUS, filterStatus);
        return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
    }

}