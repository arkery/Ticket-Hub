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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Hub {

    private HubCore storedData = new HubCore();
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Saves tickets into file
     * 
     * @param fileName The name of the save file
     * @param saveLocationFolder The folder location that the save file is contained in
     */
    @Synchronized
    public void saveTickets(String fileName, File saveLocationFolder){
        try{

            if(!saveLocationFolder.isDirectory()){
                System.out.println("[TicketHub] Creating Folder");
                saveLocationFolder.mkdir();
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter save = new FileWriter(new File(saveLocationFolder + "/" + fileName + ".json"));
            save.write(gson.toJson(storedData));
            save.close();
        }catch(IOException e) {System.out.println("[TicketHub] Failed to save file"); e.printStackTrace();}
    }

    /**
     * Loads File into Plugin (save)
     * 
     * @param fileName the file to be loaded - should be "tickets" + .json
     * @param loadLocationFolder the folder that the file is in
     */
    public void loadTickets(String fileName, File loadLocationFolder){
        try{

            if(!loadLocationFolder.isDirectory()){
                System.out.println("[TicketHub] Creating the Ticket Folder");
                loadLocationFolder.mkdir();
            }

            File storedTicketsFile = new File(loadLocationFolder + "/" + "tickets.json");
            
            if(!storedTicketsFile.isFile()){
                System.out.println("[TicketHub] No Pre-existing Tickets Found");
                return;
            }
            if(storedTicketsFile.length()== 0){
                System.out.println("[TicketHub] File is empty! Ignoring Loading");
                return;
            }

            //Deserialize the ticketHub
            System.out.println("[TicketHub] Loading in Ticket Data");
            FileReader savedTickets = new FileReader(storedTicketsFile);
            Gson gson = new Gson();
            this.storedData = gson.fromJson(savedTickets, HubCore.class);

        }catch(FileNotFoundException e) {
            System.out.println("[TicketHub] Folder not found, creating Folder");
            loadLocationFolder.mkdir();
        }catch(JsonIOException e){
            System.out.println("[TicketHub] Unable to load in Tickets");
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
            System.out.println("[TicketHub] No tickets to delete");
            return;
        }

        for(Map.Entry<UUID, String> i: this.storedData.getTicketsToClose().entrySet()){

            //x - uuid
            //y - string
            Ticket checkingThisTicket = this.storedData.getStoredTickets().get(i.getValue());
            Calendar c = Calendar.getInstance();
            c.setTime(checkingThisTicket.getTicketDateLastUpdated());
            c.add(Calendar.DATE, 7);

            //If after seven days, the ticket is resolved and there are no updates - remove it.
            if(checkingThisTicket.getTicketStatus().equals(Status.RESOLVED) && dateFormat.format(c.getTime()).equals(dateFormat.format(new Date()))){

                //Remove the ticket
                this.storedData.getStoredTickets().remove(i.getKey(), i.getValue());

                //Now that it has been removed from the hub, remove it from the list of tickets to close
                this.storedData.getTicketsToClose().remove(i.getKey());
            }

            //If the status is no longer set to resolved, remove it from the list of tickets to close
            if(!this.storedData.getStoredTickets().get(i.getValue()).getTicketStatus().equals(Status.RESOLVED)){
                this.storedData.getTicketsToClose().remove(i.getKey());
            }
        }
    }

    /**
     * Filters tickets based on conditions inputted by user
     *
     * @param conditions                Filtering conditions added by the user
     * @throws NullPointerException     Thrown if there are no tickets or if for some reason tickets aren't initialized. 
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
     *                                  OR the original list if there were no conditions inputted.
     */
    public List<Ticket> filterTickets(EnumMap<Options, Object> conditions) throws NullPointerException {
        List<Ticket> filtered = new ArrayList<>(this.storedData.getStoredTickets().values()); 
        if(conditions.isEmpty() || conditions == null){ return filtered; }

        for(Map.Entry<Options, Object> i: conditions.entrySet()){
            if(filtered.size() < 10000){
                filtered = this.filterSingleProcess(i.getKey(), i.getValue(), filtered);
            }
            else{
                filtered = this.filterMultiProcess(i.getKey(), i.getValue(), filtered);
            }
        }
        if(filtered.isEmpty() || filtered == null){
            throw new NullPointerException(); 
        }
        return filtered;
    }

    /**
     * Helper to filter tickets. Single Thread. Linear Search Filter.
     *
     * @param filterOption      Condition to filter
     * @param filterOptionValue Value stored at condition
     * @param toBeFiltered      List to filter
     * @return                  The filtered list (unordered)
     */
    private List<Ticket> filterSingleProcess(Options filterOption, Object filterOptionValue, List<Ticket> toBeFiltered){
        List<Ticket> temp = new ArrayList<>();

        for(Ticket i: toBeFiltered){
            switch(filterOption){
                case CREATOR:
                    if(i.getTicketCreator().equals(filterOptionValue)){
                        temp.add(i);
                    }
                case CATEGORY:
                    if(i.getTicketCategory().equals(filterOptionValue)){
                        temp.add(i);
                    }
                    break;
                case STATUS:
                    if(i.getTicketStatus().equals(filterOptionValue)){
                        temp.add(i);
                    }
                    break;
                case PRIORITY:
                    if(i.getTicketPriority().equals(filterOptionValue)){
                        temp.add(i);
                    }
                    break;
                case CONTACT:
                    if(i.getTicketContacts().contains(filterOptionValue)){
                        temp.add(i);
                    }
                    break;
                case DATECREATED:
                    if(dateFormat.format(i.getTicketDateCreated()).equals(dateFormat.format(filterOptionValue))){
                        temp.add(i);
                    }
                    break;
                case DATEUPDATED:
                    if(dateFormat.format(i.getTicketDateLastUpdated()).equals(dateFormat.format(filterOptionValue))){
                        temp.add(i);
                    }
                    break;
                case ASSIGNEDTO:
                    if(i.getTicketAssignedTo().equals(filterOptionValue)){
                        temp.add(i);
                    }
                    break;
                default:
                    break;
            }
        }

        return temp;
    }


    /**
     * Helper to filter Tickets. Multi Threaded.
     * 
     * @param filterOption      Condition to filter
     * @param filterOptionValue Value stored at the Condition
     * @param toBeFiltered      List to filter
     * @return                  The filtered list (unordered)
     */
    private List<Ticket> filterMultiProcess(Options filterOption, Object filterOptionValue, List<Ticket> toBeFiltered){
        List<Ticket> temp = Collections.synchronizedList(new ArrayList<>());  

        Thread part1 = new Thread(() -> {
            for(int i = 0; i < toBeFiltered.size()/2; i++){
                switch(filterOption){
                    case CREATOR:
                        if(toBeFiltered.get(i).getTicketCreator().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                    case CATEGORY:
                        if(toBeFiltered.get(i).getTicketCategory().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case STATUS:
                        if(toBeFiltered.get(i).getTicketStatus().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case PRIORITY:
                        if(toBeFiltered.get(i).getTicketPriority().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case CONTACT:
                        if(toBeFiltered.get(i).getTicketContacts().contains(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case DATECREATED:
                        if(dateFormat.format(toBeFiltered.get(i).getTicketDateCreated()).equals(dateFormat.format(filterOptionValue))){
                            temp.add(toBeFiltered.get(i)); ;
                        }
                        break;
                    case DATEUPDATED:
                        if(dateFormat.format(toBeFiltered.get(i).getTicketDateLastUpdated()).equals(dateFormat.format(filterOptionValue))){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case ASSIGNEDTO:
                        if(toBeFiltered.get(i).getTicketAssignedTo().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        Thread part2 = new Thread(() -> {
            for(int i = (toBeFiltered.size()/2) + 1; i < toBeFiltered.size(); i++){
                switch(filterOption){
                    case CREATOR:
                        if(toBeFiltered.get(i).getTicketCreator().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                    case CATEGORY:
                        if(toBeFiltered.get(i).getTicketCategory().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case STATUS:
                        if(toBeFiltered.get(i).getTicketStatus().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case PRIORITY:
                        if(toBeFiltered.get(i).getTicketPriority().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case CONTACT:
                        if(toBeFiltered.get(i).getTicketContacts().contains(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case DATECREATED:
                        if(dateFormat.format(toBeFiltered.get(i).getTicketDateCreated()).equals(dateFormat.format(filterOptionValue))){
                            temp.add(toBeFiltered.get(i)); ;
                        }
                        break;
                    case DATEUPDATED:
                        if(dateFormat.format(toBeFiltered.get(i).getTicketDateLastUpdated()).equals(dateFormat.format(filterOptionValue))){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    case ASSIGNEDTO:
                        if(toBeFiltered.get(i).getTicketAssignedTo().equals(filterOptionValue)){
                            temp.add(toBeFiltered.get(i)); 
                        }
                        break;
                    default:
                        break; 
                }
            }
        });

        part1.start();
        part2.start(); 

        try{
            part1.join(); 
            part2.join(); 
        }catch(InterruptedException e){
            System.out.println("[TicketHub] Error, Plugin failed to filter list. Sending back original List");
            Thread.currentThread().interrupt();
            return toBeFiltered; 
        }

        return temp; 
    }

    private List<Ticket> filterIteration(int start, int end, List<Ticket> targetList){
        
    }

    /**
     * Searches and gets a for a single ticket within all the stored Tickets
     *
     * @throws TicketNotFoundException  Thrown if the ticket creator isn't found or doesn't have any tickets
     * @param TicketID                  The ticket ID
     * @return                          The ticket that the player is looking for
     */
    public Ticket getTicket(String TicketID) throws TicketNotFoundException{
        if(!this.storedData.getStoredTickets().containsKey(TicketID)){
            throw new TicketNotFoundException(); 
        }
        return this.storedData.getStoredTickets().get(TicketID); 
    }

    /**
     * Add a ticket
     * Automatically tries to update ticket if it detects that ticket already exists.
     *
     * @param newTicket Ticket to add
     */
    public void addTicket(Ticket newTicket){
        if(!this.storedData.getStoredTickets().containsKey(newTicket.getTicketID())){
            this.storedData.getStoredTickets().put(newTicket.getTicketID(), newTicket);
        }
        else{
            try{
                this.updateTicket(newTicket);
            }catch(TicketNotFoundException e){
                System.out.println("[TicketHub] Plugin Attempted to Update Ticket when TicketID doesn't exist!");
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
        if(this.storedData.getStoredTickets().containsKey(ticket.getTicketID())){
            this.storedData.getStoredTickets().replace(ticket.getTicketID(), ticket);
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
        if(this.storedData.getStoredTickets().containsKey(ticketID)){
            this.storedData.getStoredTickets().remove(ticketID); 
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
        return this.storedData.getPlayerIdentifiers().containsValue(player.getUniqueId())
                   && this.storedData.getPlayerIdentifiers().containsKey(player.getName());
    }


    /**
     * Has the player joined the server?
     *
     * @param playerUsername    the player to search for
     * @return                  True if the have, False if they haven't
     */
    public boolean joinedTheServer(String playerUsername){
        return this.storedData.getPlayerIdentifiers().containsKey(playerUsername);
    }

    /**
     * Has the player joined the server?
     *
     * @param playerUUID    the player to search for
     * @return              True if the have, False if they haven't
     */
    public boolean joinedTheServer(UUID playerUUID){
        return this.storedData.getPlayerIdentifiers().containsValue(playerUUID); 
    }
}