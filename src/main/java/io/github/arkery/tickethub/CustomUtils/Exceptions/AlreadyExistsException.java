package io.github.arkery.tickethub.CustomUtils.Exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AlreadyExistsException extends Exception {

    public AlreadyExistsException(String message){
        super(message);
    }
}
