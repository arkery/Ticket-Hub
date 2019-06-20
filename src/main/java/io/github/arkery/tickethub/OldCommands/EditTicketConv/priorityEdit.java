package io.github.arkery.tickethub.OldCommands.EditTicketConv;


import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class priorityEdit extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;


    @Override
    public String getPromptText(ConversationContext conv) {
        conv.getForWhom().sendRawMessage(ChatColor.GRAY + "Current Ticket Priority: " + this.editingTicket.getTicketPriority().toString());

        conv.getForWhom().sendRawMessage(ChatColor.GOLD + "Priority Options: [ "
                + Priority.LOW.toString()
                + " | " + Priority.MEDIUM.toString()
                + " | " + Priority.HIGH.toString()
                + " ]");
        return ChatColor.GOLD + "Enter a new Priority";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {


        if(answer.equalsIgnoreCase("low")){
            this.changeTicketPriority(Priority.LOW);
            return new OptionToEditMore(plugin, editingTicket);

        }
        else if(answer.equalsIgnoreCase("medium")){
            this.changeTicketPriority(Priority.MEDIUM);
            return new OptionToEditMore(plugin, editingTicket);
        }
        else if(answer.equalsIgnoreCase("high")){
            this.changeTicketPriority(Priority.HIGH);
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
     * @param newPriority   The new priority of the ticket
     */
    public void changeTicketPriority(Priority newPriority){
        this.plugin
                .getTicketSystem()
                .getStoredData()
                .updatePriorityStats(this.editingTicket.getTicketPriority(), newPriority );
        this.editingTicket.setTicketPriority(newPriority);
    }

}
