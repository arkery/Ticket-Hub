package io.github.arkery.customtickethub;

import io.github.arkery.customtickethub.Enums.Priority_Properties;
import io.github.arkery.customtickethub.Enums.Status_Properties;
import io.github.arkery.customtickethub.Backend_System.Ticket;
import io.github.arkery.customtickethub.Backend_System.Ticket_Hub;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Test_Main {

    public static void main(String[] args) {

        try{
            Ticket_Hub t = new Ticket_Hub();
            Ticket a = new Ticket("Test Title", "Test Category", Status_Properties.OPEN, Priority_Properties.CRITICAL,
                    null, "Test Description of this ticket", "dub");

            Ticket a2 = new Ticket("Test Title", "Test Category", Status_Properties.OPEN, Priority_Properties.CRITICAL,
                    null, "Test Description of this ticket", "ark");


            System.out.println(a.getID() + "\n" + a2.getID());

            t.addTicketToHub(a);
            t.addTicketToHub(a2);

            System.out.println("\n Number of Tickets in Hub: " + t.getTotalTickets() + "\n" + t.getClosedTickets());

            List<Ticket> t2 = t.masterFilter(null, null, "", "","","",
                    Status_Properties.EMPTY,Priority_Properties.EMPTY);

            System.out.println("\n Number of matches: " + t2.size() + "\n");

            for(Ticket i: t2){
                System.out.println(i.getID() + "\n");
            }

        }catch(Exception e){e.printStackTrace();}


    }
}
