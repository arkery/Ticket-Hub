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
public class RemoveFilterCondition extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions;

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

        return ChatColor.AQUA + "Choose Filter Conditions To Remove: "
                + ChatColor.GOLD + "[ Category | Status | Priority | Contact | DateCreated | DateUpdated | AssignedTo ]";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        switch(answer.toLowerCase()){
            case "category":
                if(this.filterConditions.containsKey(Options.CATEGORY)){
                    this.filterConditions.remove(Options.CATEGORY);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "status":
                if(this.filterConditions.containsKey(Options.STATUS)){
                    this.filterConditions.remove(Options.STATUS);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "priority":
                if(this.filterConditions.containsKey(Options.PRIORITY)){
                    this.filterConditions.remove(Options.PRIORITY);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "contact":
                if(this.filterConditions.containsKey(Options.CONTACT)){
                    this.filterConditions.remove(Options.CONTACT);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "datecreated":
                if(this.filterConditions.containsKey(Options.DATECREATED)){
                    this.filterConditions.remove(Options.DATECREATED);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "dateupdated":
                if(this.filterConditions.containsKey(Options.DATEUPDATED)){
                    this.filterConditions.remove(Options.DATEUPDATED);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "assignedto":
                if(this.filterConditions.containsKey(Options.ASSIGNEDTO)){
                    this.filterConditions.remove(Options.ASSIGNEDTO);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            case "creator":
                if(this.filterConditions.containsKey(Options.CREATOR)){
                    this.filterConditions.remove(Options.CREATOR);
                }
                else{
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "This condition is not applied!");
                }
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
            default:
                conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
                return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }
    }
}
