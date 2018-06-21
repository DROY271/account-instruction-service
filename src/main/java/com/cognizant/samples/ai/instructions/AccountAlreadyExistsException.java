package com.cognizant.samples.ai.instructions;

import lombok.Getter;

public class AccountAlreadyExistsException extends Exception {

    @Getter
    private String participantId;

    public AccountAlreadyExistsException(String participantId) {
        super(String.format("Account already exists for participant[%s]", participantId));
    }
}
