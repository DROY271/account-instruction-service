package com.cognizant.samples.ai.instructions;

import lombok.Data;

@Data
public class FundInstruction {
    private int id;
    private int planInsId;
    private String fundId;
    private int percentage;

    public FundInstruction() {
    }

    public FundInstruction(int planInsId, String fundId, int percentage) {
        this.planInsId = planInsId;
        this.fundId = fundId;
        this.percentage = percentage;
    }

}
