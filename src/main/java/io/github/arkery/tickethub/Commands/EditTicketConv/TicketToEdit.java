package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class TicketToEdit extends StringPrompt {

    private TicketHub plugin;

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Enter ID of the ticket you want to edit: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        String playerName = answer.substring(0, answer.length() - 12);
        Player getPlayer = Bukkit.getOfflinePlayer(this.plugin.getTicketSystem().getStoredData().getPlayerIdentifiers().getValue(answer)).getPlayer();

        if(!this.plugin.getTicketSystem().getStoredData().getAllTickets().contains(getPlayer.getUniqueId(), answer)){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Could not find ticket!");
            return this;
        }
        else if(this.plugin.getTicketSystem().getSingleTicket(answer).getTicketStatus().equals(Status.CLOSED)){
            conv.getForWhom().sendRawMessage(ChatColor.RED + "This ticket has been closed!");
            return this;
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.GREEN + "Editing Ticket: " + this.plugin.getTicketSystem().getSingleTicket(answer).getTicketID());
            return new EditMenu(plugin, this.plugin.getTicketSystem().getSingleTicket(answer));
        }
    }
}