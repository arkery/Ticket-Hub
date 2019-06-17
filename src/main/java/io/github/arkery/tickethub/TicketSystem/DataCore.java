package io.github.arkery.tickethub.TicketSystem;

import io.github.arkery.tickethub.CustomUtils.BasicBiMap;
import io.github.arkery.tickethub.CustomUtils.BasicConcurrentTable;
import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@AllArgsConstructor
@Getter
public class DataCore implements Serializable {

    private BasicConcurrentTable<UUID, String, Ticket> allTickets;

    private HashMap<UUID, String> ticketsToClose;
    private BasicBiMap<String, UUID> playerIdentifiers;


    private int highPriority, mediumPriority, lowPriority;
    private int opened, inProgress, resolved;

    public DataCore(){
        //this.allTickets = new ConcurrentHashMap<>();
        this.allTickets = new BasicConcurrentTable<>();
        this.ticketsToClose = new HashMap<>();
        this.playerIdentifiers = new BasicBiMap<>();

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
}
