package io.github.arkery.tickethub.TicketSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.arkery.tickethub.Enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Core{

    private DataBase storedTickets;
    private File ticketFolder;

    public Core(File pluginFolder){
        this.ticketFolder = new File(pluginFolder + "/Tickets");
        this.storedTickets = new DataBase();
    }

    /*
    saves the tickets offline
    @Param name - if manually saving, json file will saved as this name
     */
    public synchronized void saveTickets(String name){
        if(name.equalsIgnoreCase("")){
            name = "tickets";
        }
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter save = new FileWriter(new File(ticketFolder + "/" + name + ".json"));
            save.write(gson.toJson(storedTickets));
        }catch(IOException e) {e.printStackTrace();}
    }

    public synchronized void canDelete(){
        for(Map.Entry<UUID, List<Ticket>> i: storedTickets.getAllTickets().entrySet()){
            for(Ticket j: i.getValue()){
                
                if(j.getTicketStatus().equals(Status.RESOLVED) && )
            }
        }
    }


}
