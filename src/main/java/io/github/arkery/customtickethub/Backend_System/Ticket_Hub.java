package io.github.arkery.customtickethub.Backend_System;

import io.github.arkery.customtickethub.Enums.Priority_Properties;
import io.github.arkery.customtickethub.Enums.Status_Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//Backbone of entire plugin. Responsible for storing, accessing and sorting all tickets.

//Note - in Test_Main, if you add an identical ticket, it will only store 1 ticket in the hub
// This is because the two tickets would have identical ID's since the ID is based on the time of creation.
@NoArgsConstructor
public class Ticket_Hub implements Serializable {
    private Map<String, Ticket> Hub = new HashMap<>();

    @Getter @Setter
    private int criticalPriorityTickets,
                        highPriorityTickets,
                        mediumPriorityTickets,
                        lowPriorityTickets,
                        openedTickets,
                        assignedTickets,
                        resolvedTickets,
                        closedTickets;

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
    }

    //Purp: Get All Tickets in the Hub - shove back and return a List.
    public List<Ticket> getAllTickets(){
        List<Ticket> displayTickets = new ArrayList<>();
        for(val entry: Hub.entrySet()) {
            displayTickets.add(entry.getValue());
        }
        return displayTickets;
    }

    //Parameters - all parameters are filtering conditions.
    //Purp: filters the entire hub based on the criteria(method parameters) and returns a filtered array based on said criteria
    public List<Ticket> masterFilter(Date dateCreated, Date dateUpdated, String ticketCreator, String personInvolved,
                                     String assignedTo, String category, Status_Properties status, Priority_Properties priority){

        List<Predicate<Ticket>> activeConditions = new ArrayList<>();

        //add the date the ticket was created to filter criteria
        if(null != dateCreated){
            //Predicate<Ticket> t = w ->w.getDateCreated().equals(dateCreated);
            activeConditions.add(x -> x.getDateCreated().equals(dateCreated));
        }
        //add the date the ticket was updated to filter criteria
        else if(null != dateUpdated){
            activeConditions.add(x -> x.getDateUpdated().equals(dateUpdated));
        }
        //add who created the ticket as filter criteria
        else if(!ticketCreator.equals("")){
            activeConditions.add(x -> x.getCreator().equals(ticketCreator));
        }
        //add who was involved/additional contact as filter criteria
        else if (!personInvolved.equals("")) {
            activeConditions.add(x -> x.getAdditionalContacts().contains(personInvolved));
        }
        //add who is assigned to the ticket as filter criteria
        else if(!assignedTo.equals("")){
            activeConditions.add(x -> x.getAssignedTo().equals(assignedTo));
        }
        //add a ticket category as filter criteria
        else if(!category.equals("")){
            activeConditions.add(x -> x.getCategory().equals(category));
        }
        //add a ticket's status as filter criteria
        else if(!status.equals(Status_Properties.EMPTY)){
            activeConditions.add(x -> x.getStatus().equals(status));
        }
        //add a ticket's priority as filter criteria
        else if(!priority.equals(Priority_Properties.EMPTY)){
            activeConditions.add(x -> x.getPriority().equals(priority));
        }
        //precaution: returns all tickets in hub.
        else{
            return getAllTickets();
        }

        Predicate<Ticket> allActiveConditions = activeConditions.stream().reduce(w -> true, Predicate::and);
        return getAllTickets().parallelStream().filter(allActiveConditions).collect(Collectors.toList());
    }
}
