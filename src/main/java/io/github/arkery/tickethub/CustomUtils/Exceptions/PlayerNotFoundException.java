package io.github.arkery.tickethub.CustomUtils.Exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayerNotFoundException extends Exception {

    public PlayerNotFoundException(String message){
        super(message);
    }

}
