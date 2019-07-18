package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Commands.FilterTicketsConv.*;
import io.github.arkery.tickethub.CustomUtils.ChatText;
import io.github.arkery.tickethub.CustomUtils.TicketListView;
import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.List;

@AllArgsConstructor
public class Menu extends StringPrompt {

    private TicketHub plugin;
    private Player player;
    private EnumMap<Options, Object> filterConditions;
    private DateSetting dateSetting;
    private int page;

    private static final ChatText onStatusCondition = new ChatText(ChatColor.GOLD, "Status", "Click here to remove Status Filter", "rstatus", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onPriorityCondition = new ChatText(ChatColor.GOLD, "Priority", "Click here to remove Priority Filter", "rpriority", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onCategoryCondition = new ChatText(ChatColor.GOLD, "Category", "Click here to remove Category Filter", "rcategory", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onContactCondition = new ChatText(ChatColor.GOLD, "Contact", "Click here to remove Contact Filter", "rcontact", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onDateCreated = new ChatText(ChatColor.GOLD, "DateCreated", "Click here to remove Date Ticket Created Filter", "rdatecreated", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onDateUpdated = new ChatText(ChatColor.GOLD, "DateUpdated", "Click here to remove Date Ticket Last Updated Filter", "rdateupdated", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onCreator = new ChatText(ChatColor.GOLD, "Creator", "Click here to remove Ticket Creator Filter", "rcreator", ClickEvent.Action.RUN_COMMAND);
    private static final ChatText onAssignedTo = new ChatText(ChatColor.GOLD, "AssignedTo", "Click here to remove Ticket Assigned To Filter", "rassignedto", ClickEvent.Action.RUN_COMMAND);

    /**
     * Called upon Menu Start
     *
     * @param plugin plugin
     * @param player player that's invoking command
     */
    public Menu(TicketHub plugin, Player player){
        this.plugin = plugin;
        this.player = player;
        this.filterConditions = new EnumMap<>(Options.class);
        this.dateSetting = DateSetting.UPDATED;
        this.page = 1;
    }

    @Override
    public String getPromptText(ConversationContext context) {

        //Display Tickets
        try{
            List<Ticket> displayList = this.plugin.getTicketSystem().filterTickets(this.filterConditions);
            int totalPages = (int) Math.ceil((double) displayList.size() / 10);
            if(this.page > totalPages){ this.page = totalPages; }
            new TicketListView(this.player, displayList, this.dateSetting, this.page, totalPages).display();
        }catch(NullPointerException e){
            this.player.spigot().sendMessage(new ChatText( ChatColor.RED, "\nThere are no tickets!").text());
            return "cancel";
        }

        //Display Filter Options
        ChatText conditions = new ChatText("");
        ChatText StatusCondition = new ChatText(ChatColor.GRAY, "Status", "Click here to apply Status Filter", "status", ClickEvent.Action.RUN_COMMAND);
        ChatText PriorityCondition = new ChatText(ChatColor.GRAY, "Priority", "Click here to apply Priority Filter", "priority", ClickEvent.Action.RUN_COMMAND);
        ChatText CategoryCondition = new ChatText(ChatColor.GRAY, "Category", "Click here to apply Priority Filter", "category", ClickEvent.Action.RUN_COMMAND);
        ChatText ContactCondition = new ChatText(ChatColor.GRAY, "Contact", "Click here to apply Priority Filter", "contact", ClickEvent.Action.RUN_COMMAND);
        ChatText DateCreated = new ChatText(ChatColor.GRAY, "DateCreated", "Click here to apply Date Ticket Created Filter", "datecreated", ClickEvent.Action.RUN_COMMAND);
        ChatText DateUpdated = new ChatText(ChatColor.GRAY, "DateUpdated", "Click here to apply Date Ticket Last Updated Filter", "dateupdated", ClickEvent.Action.RUN_COMMAND);
        ChatText Creator = new ChatText(ChatColor.GRAY, "Creator", "Click here to apply Ticket Creator Filter", "creator", ClickEvent.Action.RUN_COMMAND);
        ChatText AssignedTo = new ChatText(ChatColor.GRAY, "AssignedTo", "Click here to apply Ticket Assigned To Filter", "assignedto", ClickEvent.Action.RUN_COMMAND);

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

        this.player.spigot().sendMessage(
                conditions.add(StatusCondition).add(" ")
                            .add(PriorityCondition).add(" ")
                            .add(CategoryCondition).add(" ")
                            .add(ContactCondition).add(" ")
                            .add(DateCreated).add(" ")
                            .add(DateUpdated).add(" ")
                            .add(Creator).add(" ")
                            .add(AssignedTo).text()
        );
        this.player.spigot().sendMessage(new ChatText(ChatColor.GOLD, "\nEnter 'cancel' to exit filter view").text());

        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        switch(input.toLowerCase()){
            case "category":
                return new categoryFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "status":
                return new statusFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "priority":
                return new priorityFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "contact":
                return new contactFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "datecreated":
                return new datecreatedFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "dateupdated":
                return new dateupdatedFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "assignedto":
                return new assignedtoFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
            case "creator":
                return new creatorFilter(this.plugin, this.player, this.filterConditions, this.dateSetting, this.page);
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
                this.player.spigot().sendMessage(new ChatText(ChatColor.DARK_PURPLE, "\nExiting Filter View").text());
                return END_OF_CONVERSATION;
            default:
                try{
                    this.page = Integer.parseInt(input);
                }catch(NumberFormatException e){
                    player.spigot().sendMessage(new ChatText( ChatColor.RED, "\nInvalid Entry").text());
                }
                return this;
        }

    }
}
