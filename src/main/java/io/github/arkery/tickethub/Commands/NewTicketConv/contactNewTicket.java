package io.github.arkery.tickethub.Commands.NewTicketConv;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class contactNewTicket extends StringPrompt {

    private TicketHub plugin;
    private boolean first;
    private ArrayList<UUID> contacts;

    public contactNewTicket(TicketHub plugin, boolean first){
        this.plugin = plugin;
        this.first = first;
        this.contacts = new ArrayList<>();
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        return ChatColor.GOLD + "Enter contact's username . Enter 'none' if there are no contacts.";
    }
    
    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        if(!answer.equalsIgnoreCase("none") && first){
            if(!Bukkit.getOfflinePlayer(answer).hasPlayedBefore()) {
                conv.getForWhom().sendRawMessage(ChatColor.RED + answer + " has not joined the server before!");
                return this;
            }
            else if(contacts.contains(Bukkit.getOfflinePlayer(answer).getUniqueId())){
                conv.getForWhom().sendRawMessage(ChatColor.RED + answer + " is already a contact!");
                return new additionalContactNewTicket(plugin);
            }
            else{
                contacts.add(Bukkit.getOfflinePlayer(answer).getUniqueId());
                conv.setSessionData(Options.CONTACTS, contacts);
                return new additionalContactNewTicket(plugin);
            }

        }else if(!answer.equalsIgnoreCase("none") && !first){
            contacts = (ArrayList) conv.getSessionData(Options.CONTACTS);

            if(!Bukkit.getOfflinePlayer(answer).hasPlayedBefore()) {
                conv.getForWhom().sendRawMessage(ChatColor.RED + answer + "has not joined the server before!");
                return this;
            }
            else if(contacts.contains(Bukkit.getOfflinePlayer(answer).getUniqueId())){
                conv.getForWhom().sendRawMessage(ChatColor.RED + answer + " is already a contact!");
                return new additionalContactNewTicket(plugin);
            }
            else{
                contacts.add(Bukkit.getOfflinePlayer(answer).getUniqueId());
                conv.setSessionData(Options.CONTACTS, contacts);
                return new additionalContactNewTicket(plugin);
            }

        } else if(answer.equalsIgnoreCase("none")){

            contacts.add(((Player) conv.getForWhom()).getUniqueId());
            conv.setSessionData(Options.CONTACTS, contacts);
            return new descriptionNewTicket(plugin);
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return this;
        }
    }
}
