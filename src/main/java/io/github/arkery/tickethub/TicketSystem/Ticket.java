package io.github.arkery.tickethub.TicketSystem;

import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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

@Getter @Setter @AllArgsConstructor
public class Ticket implements Serializable {

   private String ticketID;
   private String ticketTitle;
   private Status ticketStatus;
   private String ticketCategory;
   private Priority ticketPriority;
   private List<UUID> ticketContacts;
   private String ticketDescription;
   private UUID ticketAssignedTo;
   private UUID ticketCreator;
   private Date ticketDateCreated;
   private Date ticketDateLastUpdated;
   private List<String> ticketComments;
}

