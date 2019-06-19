package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class TicketToEdit extends StringPrompt {

    private TicketHub plugin;

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Enter ID of the ticket you want to edit: ";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        try{
            if(this.plugin.getTicketSystem().getTicket(answer).getTicketStatus().equals(Status.CLOSED)){
                conv.getForWhom().sendRawMessage(ChatColor.RED + "This ticket has been closed!");
                return this;
            }
            else{
                conv.getForWhom().sendRawMessage(ChatColor.GREEN + "Editing Ticket: " + this.plugin.getTicketSystem().getTicket(answer).getTicketID());
                return new EditMenu(plugin, this.plugin.getTicketSystem().getTicket(answer));
            }
        }catch(TicketNotFoundException e) {
            conv.getForWhom().sendRawMessage("Could not find ticket!");
            return END_OF_CONVERSATION;
        }
    }
}