package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.CustomUtils.Clickable;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.List;

@AllArgsConstructor
public class categoryFilter extends StringPrompt {

    private TicketHub plugin;
    private Player player; 
    private List<Ticket> displayList; //This must stay unordered
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page; 

    @Override
    public String getPromptText(ConversationContext conv) {

        String all = "";
        for(String i : this.plugin.getCustomCategories()){
            all += " " +  i;
        }
        this.player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "\nCategory Options: ").add(new Clickable(ChatColor.AQUA, all)).text());
        this.player.spigot().sendMessage(new Clickable(ChatColor.AQUA, "\nEnter the category to add as filter condition or enter 'cancel' to cancel adding").text());
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(this.plugin.getCustomCategories().contains(answer.toLowerCase())){

            filterConditions.put(Options.CATEGORY, answer.toLowerCase());
            return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            
        }
        else if(answer.equalsIgnoreCase("cancel")){
            this.player.spigot().sendMessage(new Clickable(ChatColor.DARK_PURPLE, "\nCancelling adding Assigned To Filter").text());
            return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
