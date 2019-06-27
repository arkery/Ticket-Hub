package io.github.arkery.tickethub.TicketSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.AlreadyExistsException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.PlayerNotFoundException;
import io.github.arkery.tickethub.CustomUtils.Exceptions.TicketNotFoundException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
public class Hub {

    private DataCore storedData;
    private File ticketFolder;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public Hub(File pluginFolder){
        this.ticketFolder = new File(pluginFolder + "/Tickets");
        this.storedData = new DataCore();
    }

    /**
     * saves the tickets offline
     *
     * @param name
     */
    @Synchronized
    public void saveTickets(String name){
        if(name.equalsIgnoreCase("")){
            name = "tickets";
        }
        try{

            if(!ticketFolder.isDirectory()){
                System.out.println("TicketHub: Creating the Ticket Folder");
                ticketFolder.mkdir();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter save = new FileWriter(new File(ticketFolder + "/" + name + ".json"));
            save.write(gson.toJson(storedData));
            save.close();
        }catch(IOException e) {e.printStackTrace();}
    }

    /**
     * Loads any existing tickets into the plugin
     */
    public void loadTickets(){
        try{

            if(!ticketFolder.isDirectory()){
                System.out.println("TicketHub: Creating the Ticket Folder");
                ticketFolder.mkdir();
            }

            File storedTicketsFile = new File(ticketFolder + "/" + "tickets.json");
            if(!storedTicketsFile.isFile()){
                System.out.println("TicketHub: No Pre-existing Tickets Found");
                return;
            }
            else if(storedTicketsFile.length()== 0){
                System.out.println("TicketHub: File is empty! Ignoring Loading");
                return;
            }

            //Deserialize the ticketHub
            System.out.println("TicketHub: Loading in Ticket Data");
            FileReader savedTickets = new FileReader(storedTicketsFile);
            Gson gson = new Gson();
            this.storedData = gson.fromJson(savedTickets, DataCore.class);

        }catch(FileNotFoundException e) {
            System.out.println("TicketHub: Folder not found, creating Folder");
            ticketFolder.mkdir();
        }catch(JsonIOException e){
            System.out.println("TicketHub: Unable to load in Tickets");
            e.printStackTrace();
        }
    }


    /**
     * Checks all tickets that are stored and deletes the ones that have been resolved for more than a week
     *If the player no longer has any tickets, delete them from the map
     */
    @Synchronized
    public void checkTickets(){

        if(this.storedData.getTicketsToClose().isEmpty()){
            System.out.println("TicketHub: No tickets to delete");
            return;
        }

        for(Map.Entry<UUID, String> i: this.storedData.getTicketsToClose().entrySet()){

            //x - uuid
            //y - string
            Ticket checkingThisTicket = this.storedData.getAllTickets().get(i.getKey(), i.getValue());
            Calendar c = Calendar.getInstance();
            c.setTime(checkingThisTicket.getTicketDateLastUpdated());
            c.add(Calendar.DATE, 7);

            //If after seven days, the ticket is resolved and there are no updates - remove it.
            if(checkingThisTicket.getTicketStatus().equals(Status.RESOLVED) &&
                    dateFormat.format(c.getTime()).equals(dateFormat.format(new Date()))){

                //Decrement Hub Statistic Values since ticket is being removed
                this.storedData.removePriorityStats(this.storedData.getAllTickets().get(i.getKey(), i.getValue()).getTicketPriority());
                this.storedData.removeStatusStats(this.storedData.getAllTickets().get(i.getKey(), i.getValue()).getTicketStatus());

                //Remove the ticket
                this.storedData.getAllTickets().remove(i.getKey(), i.getValue());

                //Now that it has been removed from the hub, remove it from the list of tickets to close
                this.storedData.getTicketsToClose().remove(i.getKey());

            }

            //If the ticket status is set to close, immediately remove it.
            if(checkingThisTicket.getTicketStatus().equals(Status.CLOSED)){
                this.storedData.removePriorityStats(this.storedData.getAllTickets().get(i.getKey(),i.getValue()).getTicketPriority());
                this.storedData.getAllTickets().remove(i.getKey(), i.getValue());
                this.storedData.getTicketsToClose().remove(i.getKey());
            }

            //If the status of the ticket is no longer resolved
            if(!this.storedData.getAllTickets().get(i.getKey(),i.getValue()).getTicketStatus().equals(Status.RESOLVED)){
                this.storedData.getTicketsToClose().remove(i.getKey());
            }
        }
    }

    /**
     * Filters tickets based on conditions inputted by user
     *
     * @throws IllegalArgumentException This is thrown when there are no conditions (conditions is empty)
     * @param conditions                Filtering conditions added by the user
     *
     * Possible Filter Conditions:
     *        Options.TicketCreator
     *        Options.TicketCategory
     *        Options.TicketStatus
     *        Options.TicketContact
     *        Options.TicketPriority
     *        Options.TicketDateCreated
     *        Options.TicketDateLastUpdated
     *        Options.TicketAssignedTo
     *
     * @return                          An UNSORTED List containing tickets that fulfill the conditions inputted by the user
     */
    public List<Ticket> filterTickets(EnumMap<Options, Object> conditions) throws IllegalArgumentException{
        List<Predicate<Ticket>> activeConditions = new ArrayList<>();
        List<Ticket> ticketsAsList = this.storedData.getAllTickets().getAll();

        if(conditions.isEmpty() || !(conditions instanceof Map)){
            throw new IllegalArgumentException("Filter Conditions are Empty");
        }

        if(conditions.containsKey(Options.CREATOR)){

            //ticketsAsList = this.storedData.getAllTickets().getAllX((UUID) conditions.get(Options.CREATOR));
            activeConditions.add(x -> x.getTicketCreator().equals(conditions.get(Options.CREATOR)));
        }
        else if(conditions.containsKey(Options.CATEGORY)){
            activeConditions.add(x -> x.getTicketCategory().equals(conditions.get(Options.CATEGORY)));
        }
        else if(conditions.containsKey(Options.STATUS)){
            activeConditions.add(x -> x.getTicketStatus().equals(conditions.get(Options.STATUS)));
        }
        else if(conditions.containsKey(Options.PRIORITY)){
            activeConditions.add(x -> x.getTicketPriority().equals(conditions.get(Options.PRIORITY)));
        }
        else if(conditions.containsKey(Options.CONTACT)){
            activeConditions.add(x -> x.getTicketContacts().contains(conditions.get(Options.CONTACT)));
        }
        else if(conditions.containsKey(Options.DATECREATED)){
            //activeConditions.add(x -> x.getTicketDateCreated().equals(conditions.get(Options.DATECREATED)));
            activeConditions.add(x -> dateFormat.format(x.getTicketDateCreated()).equals(dateFormat.format(conditions.get(Options.DATECREATED))));
        }
        else if(conditions.containsKey(Options.DATEUPDATED)){
            //activeConditions.add(x -> x.getTicketDateLastUpdated().equals(conditions.get(Options.DATEUPDATED)));
            activeConditions.add(x -> dateFormat.format(x.getTicketDateLastUpdated()).equals(dateFormat.format(conditions.get(Options.DATEUPDATED))));
        }
        else if(conditions.containsKey(Options.ASSIGNEDTO)){
            activeConditions.add(x -> x.getTicketAssignedTo().equals(conditions.get(Options.ASSIGNEDTO)));
        }

        return ticketsAsList
                .stream()
                .filter(activeConditions.stream().reduce(Predicate::and).orElse(x -> true))
                .collect(Collectors.toList());
    }


    /**
     * Searches and gets a for a single ticket within all the stored Tickets
     *
     * @throws TicketNotFoundException  Thrown if the ticket creator isn't found or doesn't have any tickets
     * @param TicketID                  The ticket ID
     * @return                          The ticket that the player is looking for
     */
    public Ticket getTicket(String TicketID) throws TicketNotFoundException{
        boolean found = true;
        Ticket ticket = new Ticket();
        String playerName = TicketID.substring(0, TicketID.length() - 12);
        UUID playerUUID = new UUID(0L, 0L);

        if(!this.storedData.getPlayerIdentifiers().containsKey(playerName)){
            found = false;
        }
        else{
            playerUUID = this.storedData.getPlayerIdentifiers().getValue(playerName);
        }

        if(!this.storedData.getAllTickets().contains(playerUUID, TicketID)){
            found = false;
        }

        //Try backup linear search.
        if(!found){

            for(Ticket i: this.storedData.getAllTickets().getAll()){
                if(i.getTicketID().equals(TicketID)){
                    ticket = i;
                    return ticket;
                }
            }
            throw new TicketNotFoundException("Data not found");
        }

        ticket = this.storedData.getAllTickets().get(playerUUID, TicketID);

        return ticket;
    }


    /**
     * Add a ticket
     * Automatically tries to update ticket if it detects that ticket already exists.
     *
     * @param newTicket Ticket to add
     */
    public void addTicket(Ticket newTicket){
        if(!this.storedData.getAllTickets().contains(newTicket.getTicketCreator(), newTicket.getTicketID())){
            this.storedData.getAllTickets().add(newTicket.getTicketCreator(), newTicket.getTicketID(), newTicket);
            this.storedData.addNewPriorityStats(newTicket.getTicketPriority());
            this.storedData.addnewStatusStats(newTicket.getTicketStatus());
        }
        else{
            try{
                this.updateTicket(newTicket);
            }catch(TicketNotFoundException e){
                System.out.println("Plugin Attempted to Update Ticket when TicketID doesn't exist!");
            }
        }
    }

    /**
     * Update an existing Ticket by replacing it.
     *
     * @param ticket                    Ticket to update
     * @throws TicketNotFoundException  Thrown if the existing ticket is not found;
     */
    public void updateTicket(Ticket ticket) throws TicketNotFoundException{
        if(this.storedData.getAllTickets().contains(ticket.getTicketCreator(), ticket.getTicketID())){

            this.storedData.updateStatusStats(this.storedData
                            .getAllTickets()
                            .get(ticket.getTicketCreator(), ticket.getTicketID()).getTicketStatus()
                            , ticket.getTicketStatus());
            this.storedData.updatePriorityStats(this.storedData
                            .getAllTickets()
                            .get(ticket.getTicketCreator()
                            , ticket.getTicketID()).getTicketPriority(),
                            ticket.getTicketPriority());
            this.storedData.getAllTickets().replace(ticket.getTicketCreator(), ticket.getTicketID(), ticket);

        }
        else{
            throw new TicketNotFoundException();
        }
    }

    /**
     * Remove a Ticket that's stored
     *
     * @param ticketID                  The ID of the ticket to search and remove
     * @throws TicketNotFoundException  Thrown if plugin can't find the given Ticket ID:
     */
    public void removeTicket(String ticketID) throws TicketNotFoundException{
        String playername = ticketID.substring(0, ticketID.length() - 12);
        if(this.storedData.getPlayerIdentifiers().containsKey(playername)){
           
            if(this.storedData.getAllTickets().contains(this.storedData.getPlayerIdentifiers().getValue(playername), ticketID)){
            
                Ticket removingTicket = this.getTicket(ticketID);
                this.storedData.removeStatusStats(this.storedData.getAllTickets().get(this.storedData.getPlayerIdentifiers().getValue(playername), ticketID).getTicketStatus());
                this.storedData.removePriorityStats(this.storedData.getAllTickets().get(this.storedData.getPlayerIdentifiers().getValue(playername), ticketID).getTicketPriority());
                this.storedData.getAllTickets().remove(removingTicket.getTicketCreator(), removingTicket.getTicketID());
            }
            else{
                throw new TicketNotFoundException();
            }
        }
        else{
            throw new TicketNotFoundException();
        }
    }

    /**
     * Get the UUID of a player that has joined the server
     *
     * @param name                      The username of the player
     * @return                          the UUID of the player
     * @throws PlayerNotFoundException  Thrown if the player hasn't joined the server.
     */
    public UUID getUserUUID(String name) throws PlayerNotFoundException{
        if(this.storedData.getPlayerIdentifiers().containsKey(name)){
            return this.storedData.getPlayerIdentifiers().getValue(name);
        }
        else{
            throw new PlayerNotFoundException();
        }
    }

    /**
     * Get the Username of the player that has joined the server
     *
     * @param playeruuid                The UUID of the player to look for
     * @return                          The username of the searching player
     * @throws PlayerNotFoundException  Thrown if the player hasn't joined the server
     */
    public String getUserName(UUID playeruuid) throws PlayerNotFoundException{
        if(this.storedData.getPlayerIdentifiers().containsValue(playeruuid)){
            return this.storedData.getPlayerIdentifiers().getKey(playeruuid);
        }
        else{
            throw new PlayerNotFoundException();
        }
    }

    /**
     * Adds the user to the list of players who have joined the server.
     *
     * @param username
     * @param playerUUID
     * @throws AlreadyExistsException
     */
    public void addUser(String username, UUID playerUUID) throws AlreadyExistsException{
        if(!this.joinedTheServer(username) && !this.joinedTheServer(playerUUID)){
            this.storedData.getPlayerIdentifiers().add(username, playerUUID);
        }
        else{
            throw new AlreadyExistsException();
        }
    }

    /**
     * Attempt to update the user if they changed their name. If they didn't change it, do nothing
     *
     * @param player The player to update
     */
    public void maybeUpdateUser(Player player){

        String storedUsername = this.storedData.getPlayerIdentifiers().getKey(player.getPlayer().getUniqueId());
        if(!storedUsername.equals(player.getPlayer().getName())){
            this.storedData.getPlayerIdentifiers().replaceKey(player.getPlayer().getUniqueId(), player.getPlayer().getName());
        }
    }


    /**
     * Has the player joined the server?
     *
     * @param player    the player to search for
     * @return          True if the have, False if they haven't
     */
    public boolean joinedTheServer(Player player){
        if (this.storedData.getPlayerIdentifiers()
                .containsValue(player.getUniqueId())
                && this.storedData.getPlayerIdentifiers()
                .containsKey(player.getName())) {
            return true;
        }
        else{
            return false;
        }
    }


    /**
     * Has the player joined the server?
     *
     * @param playerUsername    the player to search for
     * @return                  True if the have, False if they haven't
     */
    public boolean joinedTheServer(String playerUsername){
        if (this.storedData.getPlayerIdentifiers().containsKey(playerUsername)) {
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Has the player joined the server?
     *
     * @param playerUUID    the player to search for
     * @return              True if the have, False if they haven't
     */
    public boolean joinedTheServer(UUID playerUUID){
        if (this.storedData.getPlayerIdentifiers().containsValue(playerUUID)) {
            return true;
        }
        else{
            return false;
        }
    }



}