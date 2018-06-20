package com.cognizant.samples.ai.instructions;

import lombok.Data;

@Data
public class FundInstruction {
    private int id;
    private int planInsId;
    private String fundId;
    private int percentage;

}
