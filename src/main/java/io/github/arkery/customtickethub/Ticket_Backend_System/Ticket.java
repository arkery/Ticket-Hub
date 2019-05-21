package io.github.arkery.customtickethub.Ticket_Backend_System;

import io.github.arkery.customtickethub.Enums.Priority_Properties;
import io.github.arkery.customtickethub.Enums.Status_Properties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

@Getter @Setter
public class Ticket implements Serializable {

    private Date dateCreated;
    private Date dateUpdated;
   // private UUID Creator,
   //              assignedTo;
    private String Creator, assignedTo; //This is created for testing purposes - replacing UUID above
    private ArrayList<String> ticketComments;
    private ArrayList<String> additionalContacts;
    private String  ID,
                    Title,
                    Category,
                    Description;
    private Status_Properties Status;
    private Priority_Properties Priority;

    public Ticket(String Title, String Category, Status_Properties Status, Priority_Properties Priority,
                  ArrayList<String> additionalContacts, String Description, String Creator){

        this.Title = Title;
        this.Category = Category;
        this.additionalContacts = additionalContacts;
        this.Description = Description;
        this.Creator = Creator;
        this.Status = Status;
        this.Priority = Priority;

        dateCreated = dateUpdated = new Date();

        DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
        //this.ID = Bukkit.getPlayer(Creator).getName() + dateFormat.format(dateCreated); //not sure if this will work
        this.ID = Creator + dateFormat.format(dateCreated);
    }
}

