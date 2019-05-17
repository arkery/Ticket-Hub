package io.github.arkery.customtickethub;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.*;
import java.util.*;


public class Ticket_Hub {
    private Map<String, Ticket> Hub = new HashMap<String, Ticket>();
    @Getter @Setter private File ticketFolderPath;

    @Getter @Setter
    private int criticalPriorityTickets,
                highPriorityTickets,
                mediumPriorityTickets,
                lowPriorityTickets,
                openedTickets,
                assignedTickets,
                resolvedTickets,
                closedTickets;

    //Index any pre-existing tickets
    public Ticket_Hub(File serverTicketFolder) throws IOException, ClassNotFoundException {
        highPriorityTickets = mediumPriorityTickets = lowPriorityTickets = openedTickets = assignedTickets = resolvedTickets = closedTickets = 0;
        this.ticketFolderPath = serverTicketFolder;

        if(!serverTicketFolder.exists()){
            System.out.println("No Folder Found: Creating Folder");
        }
        else if(serverTicketFolder.isDirectory()){

            if((serverTicketFolder.list().length) == 0) {
                System.out.println("Ticket Folder is empty");
            }
            else{
                System.out.println("Indexing Tickets | Building Hub");

                File[] ticketFileName = serverTicketFolder.listFiles();
                for(File file: ticketFileName) {
                    @Cleanup ObjectInputStream loadTicket= new ObjectInputStream(new FileInputStream(serverTicketFolder + "/" + file.getName()));
                    Ticket indexingTicket = (Ticket) loadTicket.readObject(); //cast to force

                    switch(indexingTicket.getPriority()){
                        case LOW:
                            lowPriorityTickets++;
                            break;
                        case MEDIUM:
                            mediumPriorityTickets++;
                            break;
                        case HIGH:
                            highPriorityTickets++;
                            break;
                        case CRITICAL:
                            criticalPriorityTickets++;
                            break;
                    }
                    switch(indexingTicket.getStatus()){
                        case OPEN:
                            openedTickets++;
                            break;
                        case ASSIGNED:
                            assignedTickets++;
                            break;
                        case RESOLVED:
                            resolvedTickets++;
                            break;
                        case CLOSED:
                            closedTickets++;
                            break;
                    }

                    Hub.put(indexingTicket.getID(), indexingTicket);

                }

                System.out.println("Indexing Finished | Hub Generated");
            }
        }

    }

    //Params: TicketID - the Ticket ID
    //Purp: get an individual Ticket in the Hub
    public Ticket getTicketInHub(String TicketID) {
        return Hub.get(TicketID);
    }

    //Param: updatedTicket - the updated ticket
    //Purp: update a pre-existing ticket on the hub.
    public void updateTicketInHub(Ticket updatedTicket) throws IOException{
        Hub.replace(updatedTicket.getID(),updatedTicket);
        saveTicketOffline(updatedTicket);
    }

    //Param: newTicket - the ticket to be added
    //Purp: add the ticket/new ticket to the Hub
    public void addTicketToHub(Ticket newTicket) throws IOException{
        Hub.put(newTicket.getID(), newTicket);
        saveTicketOffline(newTicket);
    }

    //Param: StatusProperty - If the user is searching for Status of ticket, String should be filled in
    //Param: PriorityProperty - If the user is searching for Priority of ticket, String should be filled in
    //Purp: Shoves everything from Hub HashMap into Array to display on screen and order it by date of created.
    public ArrayList<Ticket> filterTickets(Status_Properties StatusProperty, Priority_Properties PriorityProperty){
        ArrayList<Ticket> displayTicket = new ArrayList<Ticket>();
        for(val entry : Hub.entrySet()){
            if((entry.getValue().getStatus().equals(StatusProperty)) && PriorityProperty == Priority_Properties.EMPTY){
                displayTicket.add(entry.getValue());
            }
            if((entry.getValue().getPriority().equals(PriorityProperty)) && StatusProperty == Status_Properties.EMPTY){
                displayTicket.add(entry.getValue());
            }
            if((entry.getValue().getStatus().equals(StatusProperty)) && (entry.getValue().getPriority().equals(PriorityProperty))){
                displayTicket.add(entry.getValue());
            }
            if(StatusProperty == Status_Properties.EMPTY && PriorityProperty == Priority_Properties.EMPTY){
                displayTicket.add(entry.getValue());
            }
        }
        Collections.sort(displayTicket);
        return displayTicket;
    }

    //Param: player - the specific person's UUID
    //Purp: get all ticket made by a specific player OR all tickets involving a specific player
    public ArrayList<Ticket> ticketsIncludingPerson(UUID player, boolean Creator){
        ArrayList<Ticket> displayTicket = new ArrayList<Ticket>();
        for(val entry: Hub.entrySet()){
            if((entry.getValue().getCreator()).equals(player) && Creator){
                displayTicket.add(entry.getValue());
            }
            else if((entry.getValue().getAdditionalContacts().contains(player) && !Creator)){
                displayTicket.add(entry.getValue());
            }

        }
        return displayTicket;
    }

    //Param: ticket - ticket to be serialized
    //Purp: serialize and store ticket offline in directory
    public void saveTicketOffline(Ticket ticket) throws IOException{
        @Cleanup ObjectOutputStream saveTicket= new ObjectOutputStream(new FileOutputStream(ticketFolderPath + "/" + ticket.getID() + ".ser"));
        saveTicket.writeObject(ticket);
    }
}
