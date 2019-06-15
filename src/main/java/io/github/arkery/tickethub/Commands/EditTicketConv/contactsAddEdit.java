package io.github.arkery.tickethub.Commands.EditTicketConv;


import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class contactsAddEdit extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Enter the new contact";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        List<UUID> contacts =  this.editingTicket.getTicketContacts();

        if(contacts.size() >= 3){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Error: You already have the maximum number of contacts");
            return new contactsEdit(this.plugin, this.editingTicket);
        }
        else{
            Player contact = Bukkit.getOfflinePlayer(answer).getPlayer();
            if(!contact.hasPlayedBefore()){
                conv.getForWhom().sendRawMessage(ChatColor.RED + answer + "has not joined the server before!");
                return this;
            }else{
                this.editingTicket.getTicketContacts().add(contact.getUniqueId());
                return new OptionToEditMore(plugin, editingTicket);
            }
        }
    }
}
