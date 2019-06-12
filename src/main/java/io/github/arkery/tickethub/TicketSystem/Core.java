package io.github.arkery.tickethub.TicketSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

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

    public synchronized void checkResolved(){
        for(Map.Entry
    }

}
