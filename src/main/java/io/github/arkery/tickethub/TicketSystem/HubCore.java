package io.github.arkery.tickethub.TicketSystem;

import io.github.arkery.tickethub.CustomUtils.BasicBiMap;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@NoArgsConstructor
public class HubCore implements Serializable {

    private ConcurrentMap<String, Ticket> storedTickets = new ConcurrentHashMap<>();
    private BasicBiMap<String, UUID> playerIdentifiers = new BasicBiMap<>();
    private Map<UUID, String> ticketsToClose = new HashMap<>();
}