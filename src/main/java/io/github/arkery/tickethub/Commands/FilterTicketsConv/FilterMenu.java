package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.EnumMap;

@AllArgsConstructor
public class FilterMenu extends StringPrompt {
    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

    public FilterMenu(TicketHub plugin){
        this.plugin = plugin;
        this.filterConditions = new EnumMap<>(Options.class);
    }

    @Override
    public String getPromptText(ConversationContext conv) {

        if(this.filterConditions.isEmpty()){
            conv.getForWhom().sendRawMessage(ChatColor.BLUE + "No Applied Filter Conditions");
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.BLUE + "Applied Filter Conditions: ");
            for(Options i: this.filterConditions.keySet()){
                conv.getForWhom().sendRawMessage(ChatColor.GRAY + "-" + i.toString());
            }
        }

        return ChatColor.AQUA + "Choose Filter Conditions: "
                + ChatColor.GOLD + "[ Category | Status | Priority | Contact | DateCreated | DateUpdated | AssignedTo | Creator ]";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        switch(answer.toLowerCase()){
            case "category":
                return new categoryFilter(this.plugin, this.filterConditions);
            case "status":
                return new statusFilter(this.plugin, this.filterConditions);
            case "priority":
                return new priorityFilter(this.plugin, this.filterConditions);
            case "contact":
                return new contactFilter(this.plugin, this.filterConditions);
            case "datecreated":
                return new datecreatedFilter(this.plugin, this.filterConditions);
            case "dateupdated":
                return new dateupdatedFilter(this.plugin, this.filterConditions);
            case "assignedto":
                return new assignedtoFilter(this.plugin, this.filterConditions);
            case "creator":
                return new creatorFilter(this.plugin, this.filterConditions);
            default:
                conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
                return this;
        }
    }
}
