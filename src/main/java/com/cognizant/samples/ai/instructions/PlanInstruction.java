package com.cognizant.samples.ai.instructions;

import lombok.Data;

@Data
public class PlanInstruction {
    private int id;
    private String planId;
    private int accountId;
    private int percentage;
}
