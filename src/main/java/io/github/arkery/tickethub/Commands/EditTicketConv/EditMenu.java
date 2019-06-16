package io.github.arkery.tickethub.Commands.EditTicketConv;

import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

@AllArgsConstructor
public class EditMenu extends StringPrompt {

    private TicketHub plugin;
    private Ticket editingTicket;

    @Override
    public String getPromptText(ConversationContext conv) {

        return ChatColor.AQUA + "Choose Edit Options: "
                + ChatColor.GOLD + "[ Title | Status | Priority | Category | Contacts | Description | Assigned_To ]";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        switch(answer.toLowerCase()){
            case "title":
                return new titleEdit(plugin, editingTicket);
            case "status":
                return new statusEdit(plugin, editingTicket);
            case "priority":
                return new priorityEdit(plugin, editingTicket);
            case "category":
                return new categoryEdit(plugin, editingTicket);
            case "contacts":
                return new contactsEdit(plugin, editingTicket);
            case "description":
                return new descriptionEdit(plugin, editingTicket);
            case "assigned_to":
                return new assignedtoEdit(plugin, editingTicket);
            default:
                conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
                return this;
        }
    }
}
