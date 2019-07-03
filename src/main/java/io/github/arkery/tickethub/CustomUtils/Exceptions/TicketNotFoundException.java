package io.github.arkery.tickethub.CustomUtils.Exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TicketNotFoundException extends Exception {

    public TicketNotFoundException(String message){
        super(message);
    }
}
