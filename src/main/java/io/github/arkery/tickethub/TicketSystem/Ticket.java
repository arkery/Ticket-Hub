package io.github.arkery.tickethub.TicketSystem;

import io.github.arkery.tickethub.Enums.Priority;
import io.github.arkery.tickethub.Enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Ticket implements Serializable {

   private String ticketID;
   private String ticketTitle;
   private Status ticketStatus;
   private String ticketCategory;
   private Priority ticketPriority;
   private Set<UUID> ticketContacts;
   private String ticketDescription;
   private UUID ticketAssignedTo;
   private UUID ticketCreator;
   private Date ticketDateCreated;
   private Date ticketDateLastUpdated;
}

