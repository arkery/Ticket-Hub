package io.github.arkery.customtickethub;

import io.github.arkery.customtickethub.Backend_System.Ticket_Hub;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

//import java.time.format.DateTimeFormatter;


public class Test_Main {

    public static void main(String[] args) {

        try{
            /*
            Ticket_Hub t = new Ticket_Hub();
            Ticket a = new Ticket("Test Title", "Test Category", Status_Properties.OPEN, Priority_Properties.CRITICAL,
                    null, "Test Description of this ticket", "dub");

            Ticket a2 = new Ticket("Test Title", "Test Category", Status_Properties.RESOLVED, Priority_Properties.CRITICAL,
                    null, "Test Description of this ticket", "ark");


            t.addTicketToHub(a);
            t.addTicketToHub(a2);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new AfterburnerModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File("tes.json"), t);

            ObjectMapper mapper2 = new ObjectMapper();
            mapper2.registerModule(new AfterburnerModule());
            Ticket_Hub hello = mapper2.readValue(new File("tes.json"), Ticket_Hub.class);

            System.out.println(hello.getTotalTickets() + " ttata");

            t.getTicketInHub(a.getID()).setDescription("HOLA");

            System.out.println(t.getTicketInHub(a.getID()).getDescription());
            System.out.println(a2.getResolvedDate());
            */

            Ticket_Hub ticket_hub = new Ticket_Hub();
            ticket_hub.setClosedTickets(ticket_hub.getClosedTickets() + 1);
            System.out.println(ticket_hub.getClosedTickets());
            DateTime j = new DateTime();
            //DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
            //System.out.println(dtf.parseDateTime(j.toString()));
            DateTimeZone zoneParis = DateTimeZone.forID( "Europe/Paris" );
            System.out.println(j.plusDays(7).toString("MM/dd/yyyy HH:mm:ss"));
            System.out.println(j.toDateTime(zoneParis).toString("MM/dd/yyyy HH:mm:ss"));

            if(j.toString("MM/dd/yyyy").equals(DateTime.now().toString("MM/dd/yyyy"))){
                System.out.println("Boo   " + DateTime.now().toString("MM/dd/yyyy"));
            }

            //DateTimeZone t = new DateTimeZone.
            //j.toDateTime(new DateTimeZone.forID("Europe/Madrid"));

            //DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

/*
            Calendar calendar = Calendar.getInstance(); // this would default to now
            Date test = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 10);

            if(calendar.getTime().equals(new Date())){
                System.out.println("Boo");

            }
            else{
                System.out.print(calendar.getTime() + "   |    " + new Date());
            }

            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            if(dateFormat.format(calendar.getTime()).equals(dateFormat.format(new Date()))){
                System.out.println(dateFormat.format(calendar.getTime()));
            }

            System.out.println("\n"+dateFormat.format(calendar.getTime()) + "   " + dateFormat.format(new Date()));
*/
            //String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
            //System.out.println(serialized);
        }catch(Exception e){e.printStackTrace();}


    }
}
