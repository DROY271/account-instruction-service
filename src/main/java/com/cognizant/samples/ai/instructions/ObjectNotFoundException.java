package com.cognizant.samples.ai.instructions;

import lombok.Getter;

public class ObjectNotFoundException extends Exception {

    @Getter
    private final String type;
    @Getter
    private final String id;

    public ObjectNotFoundException(String type, String id) {
        super(String.format("No [%s] found with id [%s]", type, id));
        this.type = type;
        this.id = id;
    }

}
