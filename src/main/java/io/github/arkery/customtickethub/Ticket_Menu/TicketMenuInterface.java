package io.github.arkery.customtickethub.Ticket_Menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import io.github.arkery.customtickethub.Backend_System.Ticket;
import io.github.arkery.customtickethub.Backend_System.Ticket_Hub;
import io.github.arkery.customtickethub.Enum.Status_Properties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
//Menu for all user actions | User input commands will invoke this menu | Also does auto-maintenance on Hub
public class TicketMenuInterface{

    private File ticketFolder;
    private File closedTicketFolder;
    private Ticket_Hub ticketHub;
    private ArrayList<String> customCategories;

    //Deserialize a pre-existing Hub.
    public TicketMenuInterface(){
        this.ticketFolder = new File("plugins/" + "ticketHub");
        this.closedTicketFolder = new File("plugins/ticketHub/closedTickets");
        try{
            //Check if for some reason the folder is empty
            if(ticketFolder.isDirectory() && ticketFolder.length() == 0){
                System.out.println("No serialized .json file found");
            }
            //Deserialize the ticketHub
            else{
                System.out.println("De-serializing");
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new AfterburnerModule());
                this.ticketHub = mapper.readValue(new File(ticketFolder + "/" + "ticketHub.json"), Ticket_Hub.class);
            }

        //Create folder if it doesn't exist
        }catch(FileNotFoundException e) {
            System.out.println("Folder not found, creating Folder");
            ticketFolder.mkdir();
            closedTicketFolder.mkdir();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    //Purp: Save the entire Hub offline
    public synchronized void saveHub(String s){
        if(s.equals("")){
            s = "ticketHub";
        }

        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.registerModule(new AfterburnerModule());
            mapper.writeValue(new File(ticketFolder + "/" + s + ".json"), ticketHub);
        }catch(IOException e) {e.printStackTrace();}
    }

    //Purp: For all resolved tickets - User has 7 days before the ticket is closed.
    // Once closed, serialize individual ticket into another folder and then remove said ticket from Hub.
    public synchronized void checkResolvedTickets(){
        for(Map.Entry<String, Ticket> i: ticketHub.getHub().entrySet()){
            if(
                (i.getValue().getStatus()).equals(Status_Properties.RESOLVED) && (
                i.getValue().getResolvedDate().plusDays(7).toString("MM/dd/yyyy"))
                .equals(DateTime.now().toString("MM/dd/yyyy"))
            )
            {
                try{
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.registerModule(new AfterburnerModule());
                    mapper.writeValue(new File(closedTicketFolder + "/" + i.getValue().getID() +".json"), i.getValue());
                }catch(IOException e) {e.printStackTrace();}
                ticketHub.setClosedTickets(ticketHub.getClosedTickets() + 1);
                ticketHub.getHub().remove(i.getValue().getID());
            }
        }
    }

    //Purp: daily maintenaince - once a day, save the entire Hub & Check the Hub for resolved tickets more than 7 days
    //      Runs on another thread
    public void dailyMaintenance(){
        Runnable job = () -> {
            saveHub("");
            checkResolvedTickets();
        };
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = service.scheduleAtFixedRate(job, 0, 1, TimeUnit.DAYS);
    }

    public void showTickets(Player player, String requiredInfo){

    }

    public void editTicket(Player player){

    }

    public void createTicket(Player player){

    }

    public void getHubStatistics(Player player){

    }

    public void serializeDeserialize(){

    }

}
