package com.cognizant.samples.accountinstructions.plan;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * Models a enrollable Plan.
 */
@Data
public class Plan {
    private String id;
    private String name;

    private Set<Fund> funds;

}
