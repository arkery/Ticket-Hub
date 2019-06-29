package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.CustomUtils.Clickable;
import io.github.arkery.tickethub.CustomUtils.TicketPageView;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

@AllArgsConstructor
public class FilterMenu extends StringPrompt {
    
    private TicketHub plugin;
    private Player player; 
    private List<Ticket> displayList; //This must stay unordered
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page; 

    private static final Clickable onStatusCondition = new Clickable(ChatColor.GOLD, "Status", "Click here to remove Status Filter", "rstatus", ClickEvent.Action.RUN_COMMAND);
    private static final Clickable onPriorityCondition = new Clickable(ChatColor.GOLD, "Priority", "Click here to remove Priority Filter", "rpriority", ClickEvent.Action.RUN_COMMAND);
    private static final Clickable onCategoryCondition = new Clickable(ChatColor.GOLD, "Category", "Click here to remove Priority Filter", "rcategory", ClickEvent.Action.RUN_COMMAND);
    private static final Clickable onContactCondition = new Clickable(ChatColor.GOLD, "Contact", "Click here to remove Priority Filter", "rcontact", ClickEvent.Action.RUN_COMMAND); 
    private static final Clickable onDateCreated = new Clickable(ChatColor.GOLD, "DateCreated", "Click here to remove Date Ticket Created Filter", "rdatecreated", ClickEvent.Action.RUN_COMMAND);
    private static final Clickable onDateUpdated = new Clickable(ChatColor.GOLD, "DateUpdated", "Click here to remove Date Ticket Last Updated Filter", "rdateupdated", ClickEvent.Action.RUN_COMMAND);
    private static final Clickable onCreator = new Clickable(ChatColor.GOLD, "Creator", "Click here to remove Ticket Creator Filter", "rcreator", ClickEvent.Action.RUN_COMMAND);
    private static final Clickable onAssignedTo = new Clickable(ChatColor.GOLD, "AssignedTo", "Click here to remove Ticket Assigned To Filter", "rassignedto", ClickEvent.Action.RUN_COMMAND);

    public FilterMenu(TicketHub plugin, Player player, List<Ticket> displayList ){
        this.plugin = plugin;
        this.player = player;
        this.displayList = displayList; 
        this.dateSetting = DateSetting.UPDATED; 
        this.filterConditions = new EnumMap<>(Options.class);
        this.page = 1; 
    }

    @Override
    public String getPromptText(ConversationContext conv) {
        
        Clickable conditions = new Clickable(""); 
        Clickable StatusCondition = new Clickable(ChatColor.GRAY, "Status", "Click here to apply Status Filter", "status", ClickEvent.Action.RUN_COMMAND);
        Clickable PriorityCondition = new Clickable(ChatColor.GRAY, "Priority", "Click here to apply Priority Filter", "priority", ClickEvent.Action.RUN_COMMAND);
        Clickable CategoryCondition = new Clickable(ChatColor.GRAY, "Category", "Click here to apply Priority Filter", "category", ClickEvent.Action.RUN_COMMAND);
        Clickable ContactCondition = new Clickable(ChatColor.GRAY, "Contact", "Click here to apply Priority Filter", "contact", ClickEvent.Action.RUN_COMMAND); 
        Clickable DateCreated = new Clickable(ChatColor.GRAY, "DateCreated", "Click here to apply Date Ticket Created Filter", "datecreated", ClickEvent.Action.RUN_COMMAND);
        Clickable DateUpdated = new Clickable(ChatColor.GRAY, "DateUpdated", "Click here to apply Date Ticket Last Updated Filter", "dateupdated", ClickEvent.Action.RUN_COMMAND);
        Clickable Creator = new Clickable(ChatColor.GRAY, "Creator", "Click here to apply Ticket Creator Filter", "creator", ClickEvent.Action.RUN_COMMAND);
        Clickable AssignedTo = new Clickable(ChatColor.GRAY, "AssignedTo", "Click here to apply Ticket Assigned To Filter", "assignedto", ClickEvent.Action.RUN_COMMAND);

        if(this.filterConditions.containsKey(Options.STATUS)){
            StatusCondition = onStatusCondition; 
        }
        if(this.filterConditions.containsKey(Options.PRIORITY)){
            PriorityCondition = onPriorityCondition; 
        }
        if(this.filterConditions.containsKey(Options.CATEGORY)){
            CategoryCondition = onCategoryCondition; 
        }
        if(this.filterConditions.containsKey(Options.CONTACT)){
            ContactCondition = onContactCondition; 
        }
        if(this.filterConditions.containsKey(Options.DATECREATED)){
            DateCreated = onDateCreated; 
        }
        if(this.filterConditions.containsKey(Options.DATEUPDATED)){
            DateUpdated = onDateUpdated; 
        }
        if(this.filterConditions.containsKey(Options.CREATOR)){
            Creator = onCreator; 
        }
        if(this.filterConditions.containsKey(Options.ASSIGNEDTO)){
            AssignedTo = onAssignedTo; 
        }
        
        try{
            this.TicketListView(this.plugin.getTicketSystem().filterTickets(this.filterConditions, this.displayList));
            this.player.spigot().sendMessage(conditions.add(StatusCondition).add(" ").add(PriorityCondition).add(" ").add(CategoryCondition).add(" ").add(ContactCondition).add(" ").add(DateCreated).add(" ").add(DateUpdated).add(" ").add(Creator).add(" ").add(AssignedTo).text());
            this.player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "\nEnter 'cancel' to exit filter view").text());
        }catch(NullPointerException e){
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
        }

        return ""; 
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        switch(answer.toLowerCase()){
            case "category":
                return new categoryFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "status":
                return new statusFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "priority":
                return new priorityFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "contact":
                return new contactFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "datecreated":
                return new datecreatedFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "dateupdated":
                return new dateupdatedFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "assignedto":
                return new assignedtoFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "creator":
                return new creatorFilter(this.plugin, this.player, this.displayList, this.filterConditions, this.dateSetting, this.page);
            case "rstatus":
                this.filterConditions.remove(Options.STATUS); 
                return this; 
            case "rpriority":
                this.filterConditions.remove(Options.PRIORITY);
                return this; 
            case "rcategory":
                this.filterConditions.remove(Options.CATEGORY);
                return this; 
            case "rcontact":
                this.filterConditions.remove(Options.CONTACT);
                return this; 
            case "rdatecreated":
                this.filterConditions.remove(Options.DATECREATED);
                return this; 
            case "rdateupdated":
                this.filterConditions.remove(Options.DATEUPDATED);
                return this; 
            case "rassignedto":
                this.filterConditions.remove(Options.ASSIGNEDTO);
                return this; 
            case "rcreator":
                this.filterConditions.remove(Options.CREATOR);
                return this; 
            case "created":
                this.dateSetting = DateSetting.CREATED; 
                return this; 
            case "updated":
                this.dateSetting = DateSetting.UPDATED;
                return this; 
            case "cancel":
                return END_OF_CONVERSATION; 
            default:
                try{
                    this.page = Integer.parseInt(answer); 
                }catch(NumberFormatException e){
                    player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nInvalid Entry").text());
                }
                return this;
        }
    }

    private void TicketListView(List<Ticket> displayListView) throws NullPointerException {

        if(this.displayList.isEmpty()){
            player.spigot().sendMessage(new Clickable( ChatColor.RED, "\nThere are no tickets!").text());
            return;
        }

        int totalPages = (int) Math.ceil((double) this.displayList.size() / 9);

        if(this.dateSetting.equals(DateSetting.CREATED)){
            displayListView.sort(Comparator.comparing(Ticket::getTicketDateCreated));
            Collections.reverse(displayListView);
            dateSetting = DateSetting.CREATED; 
        }
        else if(this.dateSetting.equals(DateSetting.UPDATED)){
            displayListView.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
            Collections.reverse(displayListView);
            dateSetting = DateSetting.UPDATED; 
        }
       
        player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "\n(Created", "Click here to sort by date created", "created", ClickEvent.Action.RUN_COMMAND )
            .add(new Clickable(ChatColor.GOLD, " Updated )", "Click here to sort by date updated", "updated", ClickEvent.Action.RUN_COMMAND))
            .add(new Clickable( ChatColor.AQUA, " [" + page + "/" + totalPages + "]"))
            .text());
            new TicketPageView().ticketPageView(player, page, displayListView);

    //Navigation Arrows
        if(page != 1 ){
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "<---", "Click here to go back to previous page", "" + (page--), ClickEvent.Action.RUN_COMMAND ).text());
        }else{
            player.spigot().sendMessage(new Clickable("    ").text()); 
        }

        player.spigot().sendMessage(new Clickable("                                                    ").text()); 

        if(page != totalPages){
            player.spigot().sendMessage(new Clickable(ChatColor.GOLD, "--->", "Click here to go to next page", "" + (page++), ClickEvent.Action.RUN_COMMAND ).text());
        }else{
            player.spigot().sendMessage(new Clickable("    ").text()); 
        }
    }
}