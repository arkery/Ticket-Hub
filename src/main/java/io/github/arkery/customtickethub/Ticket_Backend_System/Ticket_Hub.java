package io.github.arkery.customtickethub.Ticket_Backend_System;

import io.github.arkery.customtickethub.Enums.Filter_Conditions;
import io.github.arkery.customtickethub.Enums.Priority_Properties;
import io.github.arkery.customtickethub.Enums.Status_Properties;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//Backbone of entire plugin. Responsible for storing, accessing and sorting all tickets.
public class Ticket_Hub {
    private Map<String, Ticket> Hub = new HashMap<>();
    @Getter @Setter private static File ticketFolderPath;

    @Getter @Setter
    private static int criticalPriorityTickets,
                        highPriorityTickets,
                        mediumPriorityTickets,
                        lowPriorityTickets,
                        openedTickets,
                        assignedTickets,
                        resolvedTickets,
                        closedTickets;

    //Index any pre-existing tickets
    public Ticket_Hub(File serverTicketFolder) throws IOException, ClassNotFoundException {
        //highPriorityTickets = mediumPriorityTickets = lowPriorityTickets = openedTickets = assignedTickets = resolvedTickets = closedTickets = 0;
        ticketFolderPath = serverTicketFolder;

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

    //Purp: Get total number of tickets in hub.
    public int getTotalTickets(){
        return Hub.size();
    }

    //Params: TicketID - the Ticket ID
    //Purp: get an individual Ticket in the Hub
    public Ticket getTicketInHub(String TicketID) {
        return Hub.get(TicketID);
    }

    //Param: updatedTicket - the updated ticket
    //Purp: update a pre-existing ticket on the hub. Also updates that last updated date.
    public void updateTicketInHub(Ticket updatedTicket) throws IOException{
        if(Hub.get(updatedTicket.getID()).getPriority() != updatedTicket.getPriority()){
            switch(Hub.get(updatedTicket.getID()).getPriority()){
                case LOW:
                    lowPriorityTickets--;
                    break;
                case MEDIUM:
                    mediumPriorityTickets--;
                    break;
                case HIGH:
                    highPriorityTickets--;
                    break;
                case CRITICAL:
                    criticalPriorityTickets--;
                    break;
            }
            switch(updatedTicket.getPriority()){
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
        }

        if(Hub.get(updatedTicket.getID()).getStatus() != updatedTicket.getStatus()){
            switch(Hub.get(updatedTicket.getID()).getStatus()){
                case CLOSED:
                    closedTickets--;
                    break;
                case RESOLVED:
                    resolvedTickets--;
                    break;
                case ASSIGNED:
                    assignedTickets--;
                    break;
                case OPEN:
                    openedTickets--;
                    break;
            }
            switch(updatedTicket.getStatus()){
                case CLOSED:
                    closedTickets++;
                    break;
                case RESOLVED:
                    resolvedTickets++;
                    break;
                case ASSIGNED:
                    assignedTickets++;
                    break;
                case OPEN:
                    openedTickets++;
                    break;
            }
        }

        updatedTicket.setDateUpdated(new Date());
        Hub.replace(updatedTicket.getID(),updatedTicket);
        saveTicketOffline(updatedTicket);
    }

    //Param: newTicket - the ticket to be added
    //Purp: add the ticket/new ticket to the Hub
    public void addTicketToHub(Ticket newTicket) throws IOException{
        switch(newTicket.getPriority()){
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
        switch(newTicket.getStatus()){
            case CLOSED:
                closedTickets++;
                break;
            case RESOLVED:
                resolvedTickets++;
                break;
            case ASSIGNED:
                assignedTickets++;
                break;
            case OPEN:
                openedTickets++;
                break;
        }

        Hub.put(newTicket.getID(), newTicket);
        saveTicketOffline(newTicket);
    }

    //Param: condition - what the filter category is, nonPersonCondition - if they aren't looking for a person, use this.
    //Param: personCondition - if they are using/searching a person, use this
    //Param: status - if they are looking for something involving status, use this. || Same applies to Priority
    //Param: byCreationDate - sort by creation date or by updated date?
    //Purp: Filter tickets in Hub to display certain information in array.
    public List<Ticket> filterViewTickets(Filter_Conditions condition, String nonPersonCondition, UUID personCondition,
                                          Status_Properties status, Priority_Properties priority, boolean byCreationDate)throws NullPointerException{
        if(condition.equals(Filter_Conditions.EMPTY)){
            return null;
        }

        List<Ticket> displayTickets = new ArrayList<>();
        for(val entry: Hub.entrySet()){
            switch(condition){
                //Concerning Person
                case GETCREATOR:
                    if((entry.getValue().getCreator()).equals(personCondition)){
                        displayTickets.add(entry.getValue());
                    }break;
                case GETINVOLVED:
                    if((entry.getValue().getAdditionalContacts().contains(personCondition))){
                        displayTickets.add(entry.getValue());
                    }break;
                case GETASSIGNED:
                    if((entry.getValue().getAssignedTo()).equals(personCondition)){
                        displayTickets.add(entry.getValue());
                    }break;
                //Concerning Ticket
                case GETCATEGORY:
                    if((entry.getValue().getCategory()).equals(nonPersonCondition)){
                        displayTickets.add(entry.getValue());
                    }break;
                case GETTICKETSTATUS:
                    if((entry.getValue().getStatus().equals(status))){
                        displayTickets.add(entry.getValue());
                    }break;
                case GETTICKETPRIORITY:
                    if((entry.getValue().getPriority().equals(priority))){
                        displayTickets.add(entry.getValue());
                    }break;
                case GETTICKETSTATUSANDPRIORITY:
                    if((entry.getValue().getStatus().equals(status)) && (entry.getValue().getPriority().equals(priority))){
                        displayTickets.add(entry.getValue());
                    }break;
                //Date Match
                case GETSPECIFICCREATIONDATE:
                    List<Ticket> displayTicket = new ArrayList<>();
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    if(dateFormat.format(entry.getValue().getDateCreated()).equals(nonPersonCondition)){
                        displayTicket.add(entry.getValue());
                    }break;
                default:
                    displayTickets.add(entry.getValue());
                    break;
            }

        }

        if(byCreationDate){
            displayTickets.sort(Comparator.comparing(Ticket::getDateCreated));
        }
        else if(!byCreationDate){
            displayTickets.sort(Comparator.comparing(Ticket::getDateUpdated));
        }

        return displayTickets;
    }

    //Param: ticket - ticket to be serialized
    //Purp: serialize and store ticket offline in directory
    public void saveTicketOffline(Ticket ticket) throws IOException{
        @Cleanup ObjectOutputStream saveTicket= new ObjectOutputStream(new FileOutputStream(ticketFolderPath + "/" + ticket.getID() + ".ser"));
        saveTicket.writeObject(ticket);
    }
}
