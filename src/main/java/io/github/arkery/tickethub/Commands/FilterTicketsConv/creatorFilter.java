package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
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
public class creatorFilter extends StringPrompt {

    private TicketHub plugin;
    private Player player; 
    private List<Ticket> displayList; //This must stay unordered
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page; 

    @Override
    public String getPromptText(ConversationContext conv) {

        this.player.spigot().sendMessage(new Clickable(ChatColor.AQUA, "\nEnter the username of the ticket creator or enter 'cancel' to cancel adding").text());
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        try{

            if(answer.equalsIgnoreCase("cancel")){
                this.player.spigot().sendMessage(new Clickable(ChatColor.DARK_PURPLE, "\nCancelling adding Creator To Filter").text());
                return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            }

            this.filterConditions.put(Options.CREATOR, this.plugin.getTicketSystem().getUserUUID(answer));
            return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            
        }catch(PlayerNotFoundException e){
            this.player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nThis person has not joined the server!").text());
            return this;
        }
    }
}
