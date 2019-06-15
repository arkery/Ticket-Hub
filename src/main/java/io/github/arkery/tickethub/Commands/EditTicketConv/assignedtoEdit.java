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

@AllArgsConstructor
public class assignedtoEdit extends StringPrompt {
    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Category: " + this.editingTicket.getTicketCategory());

        return ChatColor.GOLD + "Enter username of the person to be assigned to this ticket: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(!answer.isEmpty() || !answer.equals("")){
            Player assignedPlayer = Bukkit.getOfflinePlayer(answer).getPlayer();
            if(!assignedPlayer.hasPlayedBefore()){
                conv.getForWhom().sendRawMessage(ChatColor.RED + answer + "has not joined the server before!");
                return this;
            }else{
                this.editingTicket.setTicketAssignedTo(assignedPlayer.getUniqueId());
                return new OptionToEditMore(plugin, editingTicket);
            }

        }else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry | Cannot be Empty");
            return this;
        }
    }
}
