package io.github.arkery.customtickethub;

import io.github.arkery.customtickethub.Enums.Priority_Properties;
import io.github.arkery.customtickethub.Enums.Status_Properties;
import io.github.arkery.customtickethub.Ticket_Backend_System.Ticket;
import io.github.arkery.customtickethub.Ticket_Backend_System.Ticket_Hub;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.UUID;

public class Test_Main {

    public static void main(String[] args) {

        File serverTickets = new File("plugins/" + "serverTickets");

        try{
            Ticket_Hub Hub = new Ticket_Hub(serverTickets);
            //UUID ticketCreator = Bukkit.getOfflinePlayer("arkery").getUniqueId();

            Ticket testTicket = new Ticket("Test Title", "Test Category", Status_Properties.OPEN, Priority_Properties.CRITICAL,
                    null, "Test Description of this ticket", "Arkery");

            Hub.addTicketToHub(testTicket);



        }catch(Exception e){e.printStackTrace();}







    }
}
