package io.github.arkery.tickethub.Commands.EditTicketConv;


import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class contactsEdit extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        List<UUID> contacts = this.editingTicket.getTicketContacts();

        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Contacts: ");

        int num = 1;
        for(UUID i : contacts){
            conv.getForWhom().sendRawMessage(Color.AQUA + "Contact " + num + ": " + Bukkit.getOfflinePlayer(i).getName());
        }
        if(contacts.size() == 3){
            return Color.AQUA + "You have the maximum number of contacts | Enter 'remove' to remove a contact | Enter 'removeall' to remove all contacts";
        }
        if(contacts.isEmpty()){
            return Color.AQUA + "You have no contacts | Enter 'add' to add a contact";
        }
        else{
            return Color.AQUA + "Enter 'add' to add a contact | Enter 'remove' to remove a contact | Enter 'removeall' to remove all contacts";
        }
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(answer.equalsIgnoreCase("add")){
            return new contactsAddEdit(plugin, editingTicket);
        }
        if(answer.equalsIgnoreCase("remove")){
            return new contactsRemoveEdit(plugin, editingTicket);
        }
        if(answer.equalsIgnoreCase("removeall")){
            this.editingTicket.setTicketContacts(new ArrayList<>());
            return new OptionToEditMore(this.plugin, this.editingTicket);
        }
        else{
            conv.getForWhom().sendRawMessage(Color.RED + "Invalid Entry");
            return this;
        }
    }
}
