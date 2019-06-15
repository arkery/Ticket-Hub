package io.github.arkery.tickethub.Commands.FilterTicketsConv;

import io.github.arkery.tickethub.Enums.DateSetting;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.OrderSetting;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;

@AllArgsConstructor
public class viewFilter extends StringPrompt {

    private TicketHub plugin;
    private EnumMap<Options, Object> filterConditions; //By Default: Date Last Updated, Ascending
    private int page;
    private DateSetting dateSetting;
    private OrderSetting orderSetting;
    private List<Ticket> filteredList;

    public viewFilter(TicketHub plugin, EnumMap<Options, Object> filterConditions){
        this.plugin = plugin;
        this.filterConditions = filterConditions;
        this.page = 1;
        this.dateSetting = DateSetting.DATEUPDATED;
        this.orderSetting = OrderSetting.ASCENDING;
        this.filteredList = this.plugin.getTicketSystem().filterTickets(this.filterConditions);
        this.filteredList.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
        Collections.reverse(this.filteredList);
    }

    @Override
    public String getPromptText(ConversationContext conv) {

        List<Ticket> filteredTickets = this.plugin.getTicketSystem().filterTickets(this.filterConditions);

        Collections.reverse(filteredTickets);
        this.ticketPageView(conv, this.page, filteredTickets);

        return ChatColor.GOLD + "Options: \n" +
                "Enter page number (if applicable) \n" +
                "Enter 'ascending' to view in ascending order \n" +
                "Enter 'descending' to view in descending order \n" +
                "Enter 'created' to view by date created \n" +
                "Enter 'updated' to view by date updated \n" +
                "Enter 'change' to change filter conditions \n" +
                "Enter 'exit' or 'cancel' to stop filter view";
    }

    @Override
    public Prompt acceptInput(ConversationContext conv, String answer) {

        if(this.filteredList.isEmpty()){
            conv.getForWhom().sendRawMessage(ChatColor.LIGHT_PURPLE + "No tickets found with applied filter conditions!");
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }

        if(answer.length() == 1){
            try{

                //Check if the page entered is too big
                int totalPages = (int) Math.ceil((double) this.filteredList.size() / 9);
                if(Integer.parseInt(answer) > totalPages){
                    conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Page");
                    return new viewFilter(this.plugin, this.filterConditions, 1, this.dateSetting, this.orderSetting, this.filteredList);

                }

                //Show Tickets
                this.ticketPageView(conv, Integer.parseInt(answer), this.filteredList);

                //Return this class via second allargs constructor
                return new viewFilter(this.plugin, this.filterConditions, Integer.parseInt(answer), this.dateSetting, this.orderSetting, this.filteredList);

            }catch(NumberFormatException e){
                conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
                return this;
            }catch(IllegalArgumentException e){

                //Cautionary backup check
                conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Page");
                return new viewFilter(this.plugin, this.filterConditions, 1, this.dateSetting, this.orderSetting, this.filteredList);
            }
        }
        else if(answer.equalsIgnoreCase("ascending")){
            if(this.orderSetting.equals(OrderSetting.DESCENDING)){
                Collections.reverse(this.filteredList);
            }
            this.ticketPageView(conv, this.page, this.filteredList);
            return new viewFilter(this.plugin, this.filterConditions, this.page, this.dateSetting, this.orderSetting, this.filteredList);
        }
        else if(answer.equalsIgnoreCase("descending")){
            if(this.orderSetting.equals(OrderSetting.ASCENDING)){
                Collections.reverse(this.filteredList);
            }
            this.ticketPageView(conv, this.page, this.filteredList);
            return new viewFilter(this.plugin, this.filterConditions, this.page, this.dateSetting, this.orderSetting, this.filteredList);
        }
        else if(answer.equalsIgnoreCase("created")){
            List<Ticket> displayList = this.plugin.getTicketSystem().filterTickets(this.filterConditions);
            displayList.sort(Comparator.comparing(Ticket::getTicketDateCreated));
            if(this.orderSetting.equals(OrderSetting.ASCENDING)){
                Collections.reverse(displayList);
            }
            this.ticketPageView(conv, this.page, displayList);
            return new viewFilter(this.plugin, this.filterConditions, this.page, this.dateSetting, this.orderSetting, displayList);
        }
        else if(answer.equalsIgnoreCase("updated")){
            List<Ticket> displayList = this.plugin.getTicketSystem().filterTickets(this.filterConditions);
            displayList.sort(Comparator.comparing(Ticket::getTicketDateLastUpdated));
            if(this.orderSetting.equals(OrderSetting.ASCENDING)){
                Collections.reverse(displayList);
            }
            this.ticketPageView(conv, this.page, displayList);
            return new viewFilter(this.plugin, this.filterConditions, this.page, this.dateSetting, this.orderSetting, displayList);
        }
        else if(answer.equalsIgnoreCase("change")){
            return new AddOrRemoveConditionOption(this.plugin, this.filterConditions);
        }
        else if(answer.equalsIgnoreCase("exit")){
            conv.getForWhom().sendRawMessage(ChatColor.LIGHT_PURPLE + "Exiting Filter");
            return END_OF_CONVERSATION;
        }
        else{
            conv.getForWhom().sendRawMessage(ChatColor.RED + "Invalid Entry");
            return new OptionForMoreConditions(this.plugin, this.filterConditions);
        }

    }

    /**
     * Displays list in player friendly page format
     * This is a duplicate of the one in Commands.java however modified to be used within
     * The Bukkit Conversation API using conv.getForWhom().sendRawMessage
     *
     * @param conv                      the player who's sending this command
     * @param page                      the command input
     * @param displayTickets            list to display as
     * @throws IllegalArgumentException thrown if it gets invalid page
     */
    public void ticketPageView(ConversationContext conv, int page, List<Ticket> displayTickets) {

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        //9 entries per page
        int totalPages = (int) Math.ceil((double) displayTickets.size() / 9);
        int topOfPage = (page - 1) * 9;
        int bottomOfPage = 9 * page - 1;

        if (page > 0 && page <= totalPages) {
            conv.getForWhom().sendRawMessage(ChatColor.GOLD + "Page: [" + page + "/" + totalPages + "]");
            if (displayTickets.size() < topOfPage + 9) {
                bottomOfPage = displayTickets.size();
            }

            conv.getForWhom().sendRawMessage(ChatColor.BLUE + "Ticket ID - Date Updated");
            for (int i = topOfPage; i < bottomOfPage; i++) {
                conv.getForWhom().sendRawMessage(ChatColor.GRAY + displayTickets.get(i).getTicketID() + " | " + dateFormat.format(displayTickets.get(i).getTicketDateLastUpdated()));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

}
