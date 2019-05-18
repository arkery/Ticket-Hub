package io.github.arkery.customtickethub;

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
    private String ID;
    private UUID Creator, assignedTo;
    private ArrayList<String> ticketComments;
    private ArrayList<UUID> additionalContacts;
    private String  Title,
                    Category,
                    Description;
    private Status_Properties Status;
    private Priority_Properties Priority;

    public Ticket(String Title, String Category, Status_Properties Status, Priority_Properties Priority,
                  ArrayList<UUID> additionalContacts, String Description, UUID Creator){

        this.Title = Title;
        this.Category = Category;
        this.additionalContacts = additionalContacts;
        this.Description = Description;
        this.Creator = Creator;
        this.Status = Status;
        this.Priority = Priority;

        dateCreated = new Date();
        dateUpdated = new Date();

        DateFormat dateFormat = new SimpleDateFormat("MMddyyyyHHmmss");
        this.ID = Bukkit.getPlayer(Creator).getName() + dateFormat.format(dateCreated);

    }
}

