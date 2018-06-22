package com.cognizant.samples.ai.plan;

import lombok.Data;

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
