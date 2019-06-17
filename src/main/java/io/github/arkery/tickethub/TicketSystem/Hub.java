package io.github.arkery.tickethub.TicketSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Status;
import lombok.Getter;
import lombok.Setter;

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
    public synchronized void saveTickets(String name){
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
    public synchronized void checkTickets(){

        if(this.storedData.getTicketsToClose().isEmpty()){
            System.out.println("TicketHub: No tickets to delete");
            return;
        }

        for(Map.Entry<UUID, String> i: this.storedData.getTicketsToClose().entrySet()){

            //x - uuid
            //x - string
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
    public List<Ticket> filterTickets(EnumMap conditions){
        List<Predicate<Ticket>> activeConditions = new ArrayList<>();
        List<Ticket> ticketsAsList = this.storedData.getAllTickets().getAll();

        if(conditions.isEmpty() || !(conditions instanceof Map)){
            throw new IllegalArgumentException("Filter Conditions are Empty");
        }

        if(conditions.containsKey(Options.CREATOR)){

            ticketsAsList = this.storedData.getAllTickets().getAllX((UUID) conditions.get(Options.CREATOR));
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
     * @throws IllegalArgumentException Thrown if the ticket creator isn't found or doesn't have any tickets
     * @param TicketID                  The ticket ID
     * @return                          The ticket that the player is looking for
     */
    public Ticket getSingleTicket(String TicketID){
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
            throw new IllegalArgumentException("Data not found");
        }

        ticket = this.storedData.getAllTickets().get(playerUUID, TicketID);

        return ticket;
    }

}