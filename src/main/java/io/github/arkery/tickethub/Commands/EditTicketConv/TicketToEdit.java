package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
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
        Player getPlayer = Bukkit.getOfflinePlayer(playerName).getPlayer();

        if(!this.plugin.getTicketSystem().getStoredData().getAllTickets().containsKey(getPlayer.getUniqueId())){
            conv.getForWhom().sendRawMessage(Color.RED + "Could not find ticket!");
            return this;
        }
        else if(!this.plugin.getTicketSystem().getStoredData().getAllTickets().get(getPlayer.getUniqueId()).containsKey(answer)){
            conv.getForWhom().sendRawMessage(Color.RED + "Could not find ticket!");
            return this;
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.GREEN + "Editing Ticket: " + this.plugin.getTicketSystem().getSingleTicket(answer).getTicketID());
            return new EditMenu(plugin, this.plugin.getTicketSystem().getSingleTicket(answer));
        }
    }
}