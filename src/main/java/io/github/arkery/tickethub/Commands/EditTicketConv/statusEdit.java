package io.github.arkery.tickethub.Commands.EditTicketConv;


import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class statusEdit extends StringPrompt {
    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {
        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Status: " +
                ((Ticket) conv.getSessionData(Options.TICKET)).getTicketStatus().toString());

        conv.getForWhom().sendRawMessage(ChatColor.GOLD + "Status Options: " +
                "[ " + Status.OPENED.toString() + " | " + Status.INPROGRESS.toString() + " | " + Status.RESOLVED.toString() + " ]");
        return ChatColor.GOLD + "Enter a new Status";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(answer.equalsIgnoreCase("open")){
            this.changeTicketStatus(Status.OPENED);
            return new OptionToEditMore(plugin, editingTicket);
        }
        else if(answer.equalsIgnoreCase("inprogress")){
            this.changeTicketStatus(Status.INPROGRESS);
            return new OptionToEditMore(plugin, editingTicket);
        }
        else if(answer.equalsIgnoreCase("resolved")){
            this.changeTicketStatus(Status.RESOLVED);
            return new OptionToEditMore(plugin, editingTicket);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }

    /**
     * Modifies the priority of the editing ticket
     * To be used by the above method
     *
     * @param newStatus   The new priority of the ticket
     */
    public void changeTicketStatus(Status newStatus){
        this.plugin
                .getTicketSystem()
                .getStoredData()
                .updateStatusStats(this.editingTicket.getTicketStatus(), newStatus );
        this.editingTicket.setTicketStatus(newStatus);
    }
}
