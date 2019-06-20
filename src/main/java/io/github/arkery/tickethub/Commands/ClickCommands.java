package io.github.arkery.tickethub.Commands;

import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import io.github.arkery.tickethub.TicketHub;
import io.github.arkery.tickethub.TicketSystem.Ticket;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@AllArgsConstructor
public class ClickCommands {

    TicketHub plugin;

    /**
     * Click Command - Edit ticket Title;
     * /th cedittitle <ticketID> <new Status>
     *
     * @param player Player invoking
     * @param args   args0 = cedittitle, args1 = TicketID, args2 and onwards (since individual words occupy single element in array) = new Title
     */
    protected void clickEditTitle(Player player, String[] args){

        if(player.hasPermission("tickethub.staff")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);
                if(editingTicket.getTicketStatus().equals(Status.CLOSED)){
                    player.sendMessage(ChatColor.RED + "This ticket is closed!");
                    return;
                }

                String newTitle = "";
                for(int i = 2; i < args.length; i++){
                    newTitle += " " + args[i];
                }

                editingTicket.setTicketTitle(args[2]);
                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditTitle <TicketID> <New Title>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket Status;
     * /th ceditstatus <ticketID> <new Status>
     *
     * @param player Player invoking
     * @param args   args0 = ceditstatus, args1 = TicketID, args2 = new Status
     */
    protected void clickEditStatus(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);
                if(editingTicket.getTicketStatus().equals(Status.CLOSED)){
                    player.sendMessage(ChatColor.RED + "This ticket is closed!");
                    return;
                }
                switch(args[2].toLowerCase()){
                    case "open":
                        editingTicket.setTicketStatus(Status.OPENED);
                        break;
                    case "inprogress":
                        editingTicket.setTicketStatus(Status.INPROGRESS);
                        break;
                    case "resolved":
                        editingTicket.setTicketStatus(Status.RESOLVED);
                        break;

                }

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditStatus <TicketID> <New Status>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - close ticket;
     * /th ccloseticket <ticketID>
     *
     * @param player Player invoking
     * @param args   args0 = ccloseticket, args1 = TicketID
     */
    protected void clickCloseTicket(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);
                if(editingTicket.getTicketStatus().equals(Status.CLOSED)){
                    player.sendMessage(ChatColor.RED + "This ticket is closed!");
                    return;
                }
                editingTicket.setTicketStatus(Status.CLOSED);

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().getStoredData().getTicketsToClose().put(editingTicket.getTicketCreator(), editingTicket.getTicketID());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cCloseTicket <TicketID>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket Priority;
     * /th ceditpriority <ticketID> <new Priority>
     *
     * @param player Player invoking
     * @param args   args0 = ceditpriority, args1 = TicketID, args2 = new Priority
     */
    protected void clickEditPriority(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                if(editingTicket.getTicketStatus().equals(Status.CLOSED)){
                    player.sendMessage(ChatColor.RED + "This ticket is closed!");
                    return;
                }

                switch(args[2].toLowerCase()){
                    case "open":
                        editingTicket.setTicketPriority(Priority.LOW);
                        break;
                    case "inprogress":
                        editingTicket.setTicketPriority(Priority.MEDIUM);
                        break;
                    case "resolved":
                        editingTicket.setTicketPriority(Priority.HIGH);
                        break;
                }

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditPriority <TicketID> <New Priority>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket Category;
     * /th ceditcategory <ticketID> <new Category>
     *
     * @param player Player invoking
     * @param args   args0 = ceditcategory, args1 = TicketID, args2 = new category
     */
    protected void clickEditCategory(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                if(this.plugin.getCustomCategories().contains(args[2].toLowerCase())){
                 editingTicket.setTicketCategory(args[2].toLowerCase());
                }

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditPriority <TicketID> <New Category>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket Contacts;
     * /th ceditcontacts <ticketID> <new contact1> <new contact2 (if applicable)> <new contact3 (if applicable)>
     *
     * @param player Player invoking
     * @param args   args0 = ceditcontacts, args1 = TicketID, args2 = contact1, args3 = contact2, args4 = contact3
     */
    protected void clickEditContacts(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){
            try{

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                if(editingTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                    player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
                    return;
                }

                List<UUID> editingContacts = new ArrayList<>();

                for(int i = 2; i < args.length; i++){
                    editingContacts.add(this.plugin.getTicketSystem().getUserUUID(args[i]));
                }

                if(editingContacts.size() > 3){
                    player.sendMessage(ChatColor.RED + "You can't have more than 3 contacts!");
                    return;
                }

                editingTicket.setTicketContacts(editingContacts);
                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditContacts <TicketID> <Contact1> <Contact2> <Contact3>");
            }catch(PlayerNotFoundException e){
                player.sendMessage(ChatColor.RED + "At least one of the contacts was not found!");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket Description;
     * /th ceditdescription <ticketID> <new Description>
     *
     * @param player Player invoking
     * @param args   args0 = ceditdesciption, args1 = TicketID, args2 and onwards (since individual words occupy single element in array) = new Description
     */
    protected void clickEditDescription(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")) {
            try {

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                editingTicket.setTicketDescription(args[2]);

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            } catch (TicketNotFoundException e) {
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            } catch (IndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditTitle <TicketID> <New Description>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket assigned person;
     * /th ceditdescription <ticketID> <new Assigned Person>
     *
     * @param player Player invoking
     * @param args   args0 = ceditassignedto, args1 = TicketID, args2 = new Assigned Person
     */
    protected void clickEditAssignedTo(Player player, String[] args){
        if(player.hasPermission("tickethub.staff")){
            try {

                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                editingTicket.setTicketAssignedTo(this.plugin.getTicketSystem().getUserUUID(args[2]));

                editingTicket.setTicketDateLastUpdated(new Date());
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch (TicketNotFoundException e) {
                player.sendMessage(ChatColor.RED + "Could not find ticket!");
            }catch (IndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th cEditTitle <TicketID> <Assigned Person's Username>");
            }catch(PlayerNotFoundException e){
                player.sendMessage(ChatColor.RED + "Assigned person not found!");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You don't have permissions to do this!");
        }
    }

    /**
     * Click Command - Edit ticket Title;
     * /th cViewComments <ticketID> <Page>
     *
     * @param player
     * @param args
     */
    protected void clickViewComments(Player player, String[] args){

        try{
            Ticket displayTicket = this.plugin.getTicketSystem().getTicket(args[1]);

            if(!displayTicket.getTicketCreator().equals(player.getUniqueId()) && !player.hasPermission("tickethub.staff")){
                player.sendMessage(ChatColor.RED + "You do not have permissions to view this ticket!");
                return;
            }
            else if(displayTicket.getTicketComments().isEmpty()){
                player.sendMessage(ChatColor.GOLD + "This ticket doesn't have any comments!");
                return;
            }

            int page = Integer.parseInt(args[2]);

            //9 entries per page
            int totalPages = (int) Math.ceil((double) displayTicket.getTicketComments().size() / 9);
            int topOfPage = (page - 1) * 9;
            int bottomOfPage = 9 * page - 1;

            TextComponent ticketPageCount = new TextComponent( "Page: [" + page + "/" + totalPages + "]");
            ticketPageCount.setColor(net.md_5.bungee.api.ChatColor.GOLD);

            TextComponent ticketDetailsPage = new TextComponent("\nTicket Comments | Back To Ticket Details");
            ticketDetailsPage.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
            ticketDetailsPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("CLick here to go back to ticket details").create()));
            ticketDetailsPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/th ticketdetails " + displayTicket.getTicketID()));
            ticketDetailsPage.addExtra("    ");
            ticketDetailsPage.addExtra(ticketPageCount);

            if (page > 0 && page <= totalPages) {
                player.spigot().sendMessage(ticketDetailsPage);
                if (displayTicket.getTicketComments().size() < topOfPage + 9) {
                    bottomOfPage = displayTicket.getTicketComments().size();
                }

                //Reverse it so it shows the latest comments on top
                Collections.reverse(displayTicket.getTicketComments());

                for (int i = topOfPage; i < bottomOfPage; i++) {
                    player.sendMessage(ChatColor.GOLD + displayTicket.getTicketComments().get(i));
                }

                if(page != totalPages){
                    page++;
                    TextComponent nextPage = new TextComponent("Next Page");
                    nextPage.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                    nextPage.setItalic(true);
                    nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("CLick here for next comment page").create()));
                    nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/th cviewcomments " + displayTicket.getTicketID() + " " + page));
                    player.spigot().sendMessage(nextPage);
                }
                else if(page != 1){
                    page--;
                    TextComponent prevPage = new TextComponent("Prev Page");
                    prevPage.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                    prevPage.setItalic(true);
                    prevPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("CLick here for prev comment page").create()));
                    prevPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/th cviewcomments " + displayTicket.getTicketID() + " " + page));
                    player.spigot().sendMessage(prevPage);
                }

            } else {
                player.sendMessage(ChatColor.RED + "Invalid Page");
            }

        }catch(TicketNotFoundException e){
            player.sendMessage(ChatColor.RED + "Could not find Ticket!");
        }catch(IndexOutOfBoundsException e){
            player.sendMessage(ChatColor.RED + "Please enter in the format of "
                    + ChatColor.DARK_GREEN + "/th cViewComments <ticketid> <page>");
        }catch(NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid Page!");
        }
    }

    /**
     * Click Command - Add a comment to a ticket that belongs to the player who invoked this method
     * If player has staff permissions, they can add comments to all tickets
     * /th caddcomment <ticketid> <comment>
     *
     * @param player the player who's sending this command
     * @param args   args0 - caddcomment, args1 = ticketid, args2&onwards = new comment;
     */
    protected void clickAddComment(Player player, String[] args){
        if(player.hasPermission("tickethub.player")){

            try{
                DateFormat dateFormat = new SimpleDateFormat("MM.dd.yy");
                Ticket editingTicket = this.plugin.getTicketSystem().getTicket(args[1]);

                if(editingTicket.getTicketCreator() != player.getUniqueId() && !player.hasPermission("tickethub.staff")){
                    player.sendMessage("You do not have permission to modify a ticket that isn't yours!");
                    return;
                }

                String commentToAdd = dateFormat.format(new Date()) + " " + player.getName() + ": ";
                for(int i = 2; i < args.length; i++){
                    commentToAdd += " " + args[i];
                }

                editingTicket.getTicketComments().add(commentToAdd);
                this.plugin.getTicketSystem().updateTicket(editingTicket);

            }catch(TicketNotFoundException e){
                player.sendMessage(ChatColor.RED + "Could not find Ticket!");
            }catch(IndexOutOfBoundsException e){
                player.sendMessage(ChatColor.RED + "Please enter in the format of "
                        + ChatColor.DARK_GREEN + "/th addcomment <ticketid> <comment>");
            }
        }
        else{
            player.sendMessage(ChatColor.RED + "You do not have permissions to do this");
        }
    }
}