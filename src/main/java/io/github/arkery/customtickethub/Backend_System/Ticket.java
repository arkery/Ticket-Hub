package io.github.arkery.customtickethub.Backend_System;

import io.github.arkery.customtickethub.Enum.Priority_Properties;
import io.github.arkery.customtickethub.Enum.Status_Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;

/*
An Individual Ticket - All info on the ticket:

Ticket ID
Title
Priority - LOW, MEDIUM, HIGH, CRITICAL
Category - Custom Categories by User input
Additional Contact - player UUID
Description
Assigned To - player UUID
Creator - player UUID
Date Created - the date the ticket was made
Date Updated - the last time the ticket was updated/edited
*/

@Getter @Setter @NoArgsConstructor
public class Ticket implements Serializable {

    private String  ID,
                    Title,
                    Category;
    private Status_Properties Status;
    private Priority_Properties Priority;
    private String Description;
    private ArrayList<String> additionalContacts;

    //For testing purposes, this is a string - should be UUID
    private String  Creator,
                    wholastupdatedIt,
                    assignedTo;

    private ArrayList<String> ticketComments;
    private DateTime dateCreated,
                 dateUpdated,
                 resolvedDate;

    public Ticket(String Title, String Category, Status_Properties Status, Priority_Properties Priority,
                  ArrayList<String> additionalContacts, String Description, String Creator){

        //DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
        this.dateCreated = this.dateUpdated = new DateTime();
        //this.ID = Bukkit.getPlayer(Creator).getName() + dateFormat.format(dateCreated); //not sure if this will work
        this.ID = Creator + dateCreated.toString("MMddyyyyHHmmss");
        this.Title = Title;
        this.Category = Category;
        this.additionalContacts = additionalContacts;
        this.Description = Description;
        this.Creator = Creator;
        this.wholastupdatedIt = Creator;
        this.Status = Status;
        this.Priority = Priority;
    }
}