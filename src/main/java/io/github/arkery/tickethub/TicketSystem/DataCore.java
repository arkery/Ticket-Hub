package io.github.arkery.tickethub.TicketSystem;

import io.github.arkery.tickethub.Enums.Options;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
public class DataCore implements Serializable {

    private ConcurrentHashMap<UUID, HashMap<String, Ticket>> allTickets;
    private HashMap<UUID, String> ticketsToClose;
    private int highPriority, mediumPriority, lowPriority;
    private int opened, inProgress, resolved;

    public DataCore(){
        this.allTickets = new ConcurrentHashMap<>();
        this.ticketsToClose = new HashMap<>();
        this.highPriority = this.mediumPriority = this.lowPriority = 0;
        this.opened = this.inProgress = this.resolved = 0;
    }

    /**
     * Updates Priority Count by decrementing old value and incrementing new value
     *
     * @param oldValue the old Priority Value
     * @param newValue the new Priority Value
     */
    public void updatePriorityStats(Priority oldValue, Priority newValue){
        switch(oldValue){
            case LOW:
                this.lowPriority--;
                break;
            case MEDIUM:
                this.mediumPriority--;
                break;
            case HIGH:
                this.highPriority--;
                break;
        }
        switch(newValue){
            case LOW:
                this.lowPriority++;
                break;
            case MEDIUM:
                this.mediumPriority++;
                break;
            case HIGH:
                this.highPriority++;
                break;
        }
    }

    /**
     * Updates Status Count by decrementing old value and incrementing new value
     *
     * @param oldValue the old Status Value
     * @param newValue the new Status Value
     */
    public void updateStatusStats(Status oldValue, Status newValue){
        switch(oldValue){
            case OPENED:
                this.opened--;
                break;
            case INPROGRESS:
                this.inProgress--;
                break;
            case RESOLVED:
                this.resolved--;
                break;
        }
        switch(newValue){
            case OPENED:
                this.opened++;
                break;
            case INPROGRESS:
                this.inProgress++;
                break;
            case RESOLVED:
                this.resolved++;
                break;
        }
    }

    /**
     * Updates Priority Statistics based on a new ticket being created
     *
     * @param value the priority to be added
     */
    public void addNewPriorityStats(Priority value){
        switch(value){
            case LOW:
                this.lowPriority++;
                break;
            case MEDIUM:
                this.mediumPriority++;
                break;
            case HIGH:
                this.highPriority++;
                break;
        }
    }

    /**
     * Updates Status Statistics based on ticket being removed
     *
     * @param value the priority to be added
     */
    public void removeStatusStats(Status value){
        switch(value){
            case OPENED:
                this.opened--;
                break;
            case INPROGRESS:
                this.inProgress--;
                break;
            case RESOLVED:
                this.resolved--;
                break;
        }
    }

    /**
     * Updates Priority Statistics based on ticket being removed
     *
     * @param value the priority to be added
     */
    public void removePriorityStats(Priority value){
        switch(value){
            case LOW:
                this.lowPriority--;
                break;
            case MEDIUM:
                this.mediumPriority--;
                break;
            case HIGH:
                this.highPriority--;
                break;
        }
    }

    /**
     * Updates Status Statistics based on a new ticket being created
     *
     * @param value the priority to be added
     */
    public void addnewStatusStats(Status value){
        switch(value){
            case OPENED:
                this.opened++;
                break;
            case INPROGRESS:
                this.inProgress++;
                break;
            case RESOLVED:
                this.resolved++;
                break;
        }
    }

    /**
     * Creates a copy of all tickets that exist into a single UNSORTED list
     *
     * @return A single list containing all tickets that are stored
     */
    public synchronized List<Ticket> convertAllTicketsMapToList(){
        List<Ticket> allTicketsAsList = new ArrayList<>();

        for(HashMap<String, Ticket> i: this.allTickets.values()){
            for(Ticket ticket: i.values()){
                allTicketsAsList.add(ticket);
            }
        }

        return allTicketsAsList;
    }

    /**
     * Creates a copy of all tickets belonging to a certain player converted into a single UNSORTED list
     *
     * @param mapToConvert  The specific player's tickets
     * @return              A single list containing all tickets that belong to a specified player
     */
    public synchronized List<Ticket> convertPlayerTicketsMapToList(HashMap<String, Ticket> mapToConvert){
        List<Ticket> ticketsAsList = new ArrayList<>();

        for(Ticket ticket: mapToConvert.values()){
            ticketsAsList.add(ticket);
        }

        return ticketsAsList;
    }

/*

    Removes Hours, Minutes and Seconds from a date object

    @param dateToRemove the date object that Hours, Minutes and Seconds will be removed from
    @return             Date Object without Hours, Minutes and Seconds

    public Date removeHourMinuteSeconds(Date dateToRemove){
    Calendar modifyDate = Calendar.getInstance();

    modifyDate.setTime(dateToRemove);
    modifyDate.set(Calendar.HOUR_OF_DAY, 0);
    modifyDate.set(Calendar.MINUTE, 0);
    modifyDate.set(Calendar.SECOND, 0);
    modifyDate.set(Calendar.MILLISECOND, 0);

    return modifyDate.getTime();
}
 */



}
