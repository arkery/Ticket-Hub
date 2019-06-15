package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import org.bukkit.ChatColor;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

import java.util.ArrayList;

public class additionalContactNewTicket extends BooleanPrompt {

    private TicketHub plugin;
    public additionalContactNewTicket(TicketHub plugin){
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Do you want to add more contacts?";
    }
    @Override
    protected Prompt acceptValidatedInput(ConversationContext conv, boolean moreContacts) {
        if(moreContacts){
            ArrayList<String> contacts = (ArrayList) conv.getSessionData(Options.CONTACTS);
            if(contacts.size() < 3) {
                return new contactNewTicket(plugin, false);
            }
            else{
                conv.getForWhom().sendRawMessage(ChatColor.RED + "You cannot have more than 3 contacts! Moving to Description");
                return new descriptionNewTicket(plugin);
            }

        }
        else{
            return new descriptionNewTicket(plugin);
        }
    }


}
