package io.github.arkery.tickethub.CustomUtils;

import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.OrderSetting;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Check if viewingTickets is null beforehand.
 *
 */
public class DisplayTickets extends StringPrompt {

    TicketHub plugin;
    List<Ticket> viewingTickets;
    List<Ticket> originalList;
    Player player;
    private DateSetting dateSetting;
    private OrderSetting orderSetting;
    int page;
    int totalPages;

    public DisplayTickets(TicketHub plugin, List<Ticket> tickets, Player player){
        this.plugin = plugin;
        this.viewingTickets = tickets;
        this.originalList = tickets;
        this.player = player;
        this.dateSetting = DateSetting.DATEUPDATED;
        this.orderSetting = OrderSetting.ASCENDING;
        this.page = 1;

        this.totalPages = (int) Math.ceil((double) viewingTickets.size() / 9);
        this.viewingTickets.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
        Collections.reverse(viewingTickets);

    }

    public DisplayTickets(TicketHub plugin, List<Ticket> viewingTickets, List<Ticket> originalList, Player player, DateSetting dateSetting, OrderSetting orderSetting, int page){
        this.plugin = plugin;
        this.viewingTickets = viewingTickets;
        this.originalList = originalList;
        this.player = player;
        this.dateSetting = DateSetting.DATEUPDATED;
        this.orderSetting = OrderSetting.ASCENDING;
        this.page = page;

        this.totalPages = (int) Math.ceil((double) viewingTickets.size() / 9);
    }

    @Override
    public String getPromptText(ConversationContext conv){

        TextComponent Line = new TextComponent("");
        TextComponent Options = new TextComponent("");
        Options.addExtra(this.Ascending());
        Options.addExtra("  ");
        Options.addExtra(this.Descending());
        Options.addExtra("  ");
        Options.addExtra(this.DateCreated());
        Options.addExtra("  ");
        Options.addExtra(this.DateUpdated());
        Options.addExtra("  ");
        Options.addExtra(this.Exit());

        conv.getForWhom().sendRawMessage("\n" + ChatColor.GOLD + "All Tickets          [" + this.page + "/" + this.totalPages + "]");
        conv.getForWhom().sendRawMessage(ChatColor.DARK_GRAY + "-----------------------------------------------------");
        conv.getForWhom().sendRawMessage(ChatColor.BLUE + "    ID  STATUS  PRIORITY  DATEUPDATED  DATECREATED    ");
        conv.getForWhom().sendRawMessage("");
        new TicketPageView().ticketPageView(this.player, this.page, this.viewingTickets);
        conv.getForWhom().sendRawMessage(ChatColor.DARK_GRAY + "-----------------------------------------------------");

        if(this.page != totalPages && this.page != 1){
            Line.addExtra(this.PrevPage());
            Line.addExtra("     ");
            Line.addExtra(this.NextPage());
            player.spigot().sendMessage(Line);
        }
        if(this.page != 1){
            //Show next page button
            this.player.spigot().sendMessage(this.NextPage());
        }
        if(this.page != totalPages){
            //Show prev page button
            this.player.spigot().sendMessage(this.PrevPage());
        }
        player.spigot().sendMessage(Options);
        return "";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {
        switch(answer.toLowerCase()){
            case "ascending":
                if(this.orderSetting.equals(OrderSetting.DESCENDING)){
                    Collections.reverse(this.viewingTickets);
                }
                this.orderSetting = OrderSetting.ASCENDING;
                //return new DisplayTickets(this.plugin, this.viewingTickets, this.originalList, this.player, this.dateSetting, this.orderSetting, this.page);
                return this;
            case "descending":
                if(this.orderSetting.equals(OrderSetting.ASCENDING)){
                    Collections.reverse(this.viewingTickets);
                }
                this.orderSetting = OrderSetting.DESCENDING;
                return this;
            case "datecreated":
                this.viewingTickets = this.originalList;
                this.viewingTickets.sort(Comparator.comparing(Ticket::getTicketDateCreated));
                if(this.orderSetting.equals(OrderSetting.ASCENDING)){
                    Collections.reverse(this.viewingTickets);
                }
                this.dateSetting = DateSetting.DATECREATED;
                return this;
            case "dateupdated":
                this.viewingTickets = this.originalList;
                this.viewingTickets.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
                if(this.orderSetting.equals(OrderSetting.ASCENDING)){
                    Collections.reverse(this.viewingTickets);
                }
                this.dateSetting = DateSetting.DATEUPDATED;
                return this;
            case "nextpage":
                if(this.page == totalPages){
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "There are no more pages after this!");
                    return this;
                }
                else{
                    this.page++;
                    return this;
                }
            case "prevpage":
                if(this.page == 0){
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "There are no more pages before this!");
                    return this;
                }
                else{
                    this.page++;
                    return this;
                }
            case "exit":
                conv.getForWhom().sendRawMessage(ChatColor.YELLOW + "Ending All Ticket View");
                return END_OF_CONVERSATION;
            default:
                conv.getForWhom().sendRawMessage("\n" + ChatColor.RED + "Invalid Entry");
                return this;

        }
    }

    private TextComponent Ascending (){
        TextComponent Ascending = new TextComponent("Ascending");
        Ascending.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        Ascending.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to view in ascending order").create()));
        Ascending.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"ascending"));
        return Ascending;
    }

    private TextComponent Descending(){
        TextComponent Descending = new TextComponent("Descending");
        Descending.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        Descending.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to view in descending order").create()));
        Descending.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"descending"));
        return Descending;
    }

    private TextComponent DateCreated(){
        TextComponent DateCreated = new TextComponent("DateCreated");
        DateCreated.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        DateCreated.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to view in the order of date created").create()));
        DateCreated.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"datecreated"));
        return DateCreated;
    }

    private TextComponent DateUpdated(){
        TextComponent DateUpdated = new TextComponent("DateUpdated");
        DateUpdated.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        DateUpdated.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to view in the order of date updated").create()));
        DateUpdated.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"dateupdated"));
        return DateUpdated;
    }

    private TextComponent NextPage(){
        TextComponent NextPage = new TextComponent("NextPage");
        NextPage.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        NextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to go to next page").create()));
        NextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"nextpage"));
        return NextPage;
    }

    private TextComponent PrevPage(){
        TextComponent PrevPage = new TextComponent("PrevPage");
        PrevPage.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        PrevPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to go to previous page").create()));
        PrevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"prevpage"));
        return PrevPage;
    }

    private TextComponent Exit(){
        TextComponent Exit = new TextComponent("Exit");
        Exit.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        Exit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to exit").create()));
        Exit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"exit"));
        return Exit;
    }
}
