package io.github.arkery.tickethub.TicketSystem;

import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class DataBase implements Serializable {

    @Setter @Getter private ConcurrentHashMap<UUID, List<Ticket>> allTickets;
    @Getter private int highPriority, mediumPriority, lowPriority;
    @Getter private int opened, inProgress, resolved;

    public DataBase(){
        this.allTickets = new ConcurrentHashMap<>();
        this.highPriority = this.mediumPriority = this.lowPriority = 0;
        this.opened = this.inProgress = this.resolved = 0;
    }

    /*
    Updates Priority Count by decrementing old value and incrementing new value

    @param oldValue the old Priority Value
    @param newValue the new Priority Value
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

    /*
    Updates Status Count by decrementing old value and incrementing new value

    @Param oldValue the old Status Value
    @Param newValue the new Status Value
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

    /*
    Updates Priority Statistics based on a new ticket being created

    @param value - the priority to be added
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

    /*
    Updates Status Statistics based on a new ticket being created

    @param value the priority to be added
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

    /*
    Creates a copy of all tickets that exist into a single UNSORTED list

    @return A single list containing all tickets that are stored
     */
    public synchronized List<Ticket> convertTicketDataMapToList(){
        List<Ticket> allTicketsAsList = new ArrayList<>();

        for(Map.Entry<UUID, List<Ticket>> i: this.allTickets.entrySet()){
            for(Ticket ticket: i.getValue()){
                allTicketsAsList.add(ticket);
            }
        }

        return allTicketsAsList;
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
