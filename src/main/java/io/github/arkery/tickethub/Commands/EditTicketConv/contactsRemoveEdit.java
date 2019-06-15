package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class contactsRemoveEdit extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        List<UUID> editingContacts = this.editingTicket.getTicketContacts();

        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "First Contact: " + Bukkit.getOfflinePlayer(editingContacts.get(0)).getName()
                                                    + " | Second Contact: " + Bukkit.getOfflinePlayer(editingContacts.get(1)).getName()
                                                    + " | Third Contact: " + Bukkit.getOfflinePlayer(editingContacts.get(2)).getName());

        return ChatColor.GOLD + "Enter '1' for First Contact | Enter '2' for Second Contact | Enter '3' for Third Contact";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        List<UUID> editingContacts = this.editingTicket.getTicketContacts();
        if(editingContacts.size() == 0 || editingContacts.isEmpty()){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Error: You have no contacts to remove!");
            return new contactsEdit(this.plugin, this.editingTicket);
        }
        else{
            try{

                this.editingTicket.getTicketContacts().remove(Integer.parseInt(answer) -1);
                return new OptionToEditMore(this.plugin, this.editingTicket);

            }catch(NumberFormatException e){
                conv.getForWhom().sendRawMessage(ChatColor.RED + "Please enter a number");
                return this;
            }
        }
    }
}
