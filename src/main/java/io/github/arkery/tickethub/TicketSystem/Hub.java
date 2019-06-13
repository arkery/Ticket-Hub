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

    private DataCore storedTickets;
    private File ticketFolder;
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public Hub(File pluginFolder){
        this.ticketFolder = new File(pluginFolder + "/Tickets");
        this.storedTickets = new DataCore();
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
            save.write(gson.toJson(storedTickets));
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

            //Deserialize the ticketHub
            System.out.println("TicketHub: Loading in Ticket Data");
            FileReader resolvedTicket = new FileReader(storedTicketsFile);
            Gson gson = new Gson();
            this.storedTickets = gson.fromJson(resolvedTicket, DataCore.class);

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
     * Note - For future reference, adding another conditional check in the nested for loop will not work.
     *      - Currently, method can only remove one condition at a time, will need to run through the loop again or make
     *        a second nested loop for another condition. This is because after deletion, the size of i.getValue is shortened
     *        which will potentially cause Exception Errors (ie. OutOfBounds) if it attempts to find and delete another value.
     */
    public synchronized void deletePastOneWeek(){
        //all values in the hashmap
        for(Map.Entry<UUID, List<Ticket>> i: storedTickets.getAllTickets().entrySet()){
            //The arraylist stored in the hashmap
            for(int j = 0; j < i.getValue().size(); j++){
                Calendar c = Calendar.getInstance();
                c.setTime(i.getValue().get(j).getTicketDateLastUpdated());
                c.add(Calendar.DATE, 7);

                if(i.getValue().get(j).getTicketStatus().equals(Status.RESOLVED) &&
                        dateFormat.format(c.getTime()).equals(dateFormat.format(new Date()))){
                    i.getValue().remove(j);
                }
            }
            //Delete user from hashmap if they don't have any tickets
            if(i.getValue().isEmpty()){
                storedTickets.getAllTickets().remove(i.getKey());
            }
        }
    }

    /**
     * Filters tickets based on conditions inputted by user
     *
     * @param conditions                Filtering conditions added by the user
     * @return                          An UNSORTED List containing tickets that fulfill the conditions inputted by the user
     * @throws IllegalArgumentException This is thrown when there are no conditions (conditions is empty)
     */
    public List<Ticket> filterTickets(Map conditions){
        List<Predicate<Ticket>> activeConditions = new ArrayList<>();

        if(conditions.isEmpty() || !(conditions instanceof Map)){
            throw new IllegalArgumentException();
        }

        if(conditions.containsKey(Options.TICKETCATEGORY)){
            activeConditions.add(x -> x.getTicketCategory().equals(conditions.get(Options.TICKETCATEGORY)));
        }
        else if(conditions.containsKey(Options.TICKETSTATUS)){
            activeConditions.add(x -> x.getTicketStatus().equals(conditions.get(Options.TICKETSTATUS)));
        }
        else if(conditions.containsKey(Options.TICKETPRIORITY)){
            activeConditions.add(x -> x.getTicketPriority().equals(conditions.get(Options.TICKETPRIORITY)));
        }
        else if(conditions.containsKey(Options.TICKETCONTACT)){
            activeConditions.add(x -> x.getTicketContacts().contains(conditions.get(Options.TICKETCONTACT)));
        }
        else if(conditions.containsKey(Options.TICKETDATECREATED)){
            //activeConditions.add(x -> x.getTicketDateCreated().equals(conditions.get(Options.TICKETDATECREATED)));
            activeConditions.add(x -> dateFormat.format(x.getTicketDateCreated()).equals(dateFormat.format(conditions.get(Options.TICKETDATECREATED))));
        }
        else if(conditions.containsKey(Options.TICKETDATELASTUPDATED)){
            //activeConditions.add(x -> x.getTicketDateLastUpdated().equals(conditions.get(Options.TICKETDATELASTUPDATED)));
            activeConditions.add(x -> dateFormat.format(x.getTicketDateLastUpdated()).equals(dateFormat.format(conditions.get(Options.TICKETDATELASTUPDATED))));
        }
        else if(conditions.containsKey(Options.TICKETCREATOR)){
            activeConditions.add(x -> x.getTicketCreator().equals(conditions.get(Options.TICKETCREATOR)));
        }
        else if(conditions.containsKey(Options.TICKETASSIGNEDTO)){
            activeConditions.add(x -> x.getTicketAssignedTo().equals(conditions.get(Options.TICKETASSIGNEDTO)));
        }

        return storedTickets.convertTicketDataMapToList()
                .stream()
                .filter(activeConditions.stream().reduce(Predicate::and).orElse(x -> true))
                .collect(Collectors.toList());
    }

}
