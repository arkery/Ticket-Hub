package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.CustomUtils.Clickable;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.List;

@AllArgsConstructor
public class datecreatedFilter extends StringPrompt {

    private TicketHub plugin;
    private Player player; 
    private List<Ticket> displayList; //This must stay unordered
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page; 
    private static final DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy");

    @Override
    public String getPromptText(ConversationContext conv) {

        this.player.spigot().sendMessage(new Clickable(ChatColor.AQUA, "\nEnter the date created in the form MM.DD.YYYY or enter 'cancel' to cancel adding").text());
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        try{

            if(answer.equalsIgnoreCase("cancel")){
                this.player.spigot().sendMessage(new Clickable(ChatColor.DARK_PURPLE, "\nCancelling adding Assigned To Filter").text());
                return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            }

            this.filterConditions.put(Options.DATECREATED, dateFormat.parse(answer));
            return new FilterMenu(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            
        }catch(ParseException e){
            this.player.spigot().sendMessage(new Clickable(ChatColor.RED, "\nInvalid date format!").text());
            return this;
        }
    }
}
