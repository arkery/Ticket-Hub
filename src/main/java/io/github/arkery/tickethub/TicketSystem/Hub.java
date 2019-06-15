package io.github.arkery.tickethub.TicketSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Status;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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
     * If the user has no tickets belonging to them after deletion, user is deleted from the hashmap
     *
     */
    public synchronized void checkPastOneWeek(){

        if(this.storedData.getTicketsToClose().isEmpty()){
            System.out.println("TicketHub: No tickets to delete");
            return;
        }

        for(Map.Entry<UUID, String> i: this.storedData.getTicketsToClose().entrySet()){

            Ticket checkingThisTicket = this.storedData.getAllTickets().get(i.getKey()).get(i.getValue());
            Calendar c = Calendar.getInstance();
            c.setTime(checkingThisTicket.getTicketDateLastUpdated());
            c.add(Calendar.DATE, 7);
            if(checkingThisTicket.getTicketStatus().equals(Status.RESOLVED) &&
                    dateFormat.format(c.getTime()).equals(dateFormat.format(new Date()))){

                //Decrement Hub Statistic Values since ticket is being removed
                this.storedData.removePriorityStats(this.storedData.getAllTickets().get(i.getKey()).get(i.getValue()).getTicketPriority());
                this.storedData.removeStatusStats(this.storedData.getAllTickets().get(i.getKey()).get(i.getValue()).getTicketStatus());

                //Remove the ticket
                this.storedData.getAllTickets().get(i.getKey()).remove(i.getValue());

                //If the player now has no other tickets, remove the player from hub.
                this.storedData.getTicketsToClose().remove(i.getKey());

            }

            if(!this.storedData.getAllTickets().get(i.getKey()).get(i.getValue()).getTicketStatus().equals(Status.RESOLVED)){
                this.storedData.getTicketsToClose().remove(i.getKey());
                continue;
            }

            if(this.storedData.getAllTickets().get(i.getKey()).isEmpty()){
                this.storedData.getAllTickets().remove(i.getKey());
                continue;
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
     *        Options.TicketPriority
     *        Options.TicketContact
     *        Options.TicketDateCreated
     *        Options.TicketDateLastUpdated
     *        Options.TicketAssignedTo
     *
     * @return                          An UNSORTED List containing tickets that fulfill the conditions inputted by the user
     */
    public List<Ticket> filterTickets(EnumMap conditions){
        List<Predicate<Ticket>> activeConditions = new ArrayList<>();
        List<Ticket> ticketsAsList = this.storedData.convertAllTicketsMapToList();

        if(conditions.isEmpty() || !(conditions instanceof Map)){
            throw new IllegalArgumentException();
        }

        if(conditions.containsKey(Options.CREATOR)){

            ticketsAsList = this.storedData.convertPlayerTicketsMapToList(this.storedData.getAllTickets().get(conditions.get(Options.CREATOR)));
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
        Ticket ticket = new Ticket();
        String playerName = TicketID.substring(0, TicketID.length() - 12);
        Player getPlayer = Bukkit.getOfflinePlayer(playerName).getPlayer();

        if(!this.storedData.getAllTickets().containsKey(getPlayer.getUniqueId())){
            throw new IllegalArgumentException();
        }
        else if(this.storedData.getAllTickets().get(getPlayer.getUniqueId()).isEmpty()){
            throw new IllegalArgumentException();
        }

        if(this.storedData.getAllTickets().get(getPlayer.getUniqueId()).containsKey(TicketID)){
            ticket = this.storedData.getAllTickets().get(getPlayer.getUniqueId()).get(TicketID);
        }

        return ticket;
    }

}
