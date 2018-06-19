# account-instruction-service

Account Instruction service provides the operations with the account contribution instructions sub-domain for the group pension plan domain.

The operations are:
1. Set contribution instrutions for an account
2. Set fund instructions for an account within the plan.
3. Get the current instructions for the account.

To be able to work properly, when a new account is created, default contribution and fund split instructions must be set. 
This also implies that this service needs to maintain the plan & fund details.


## Entity Model
![Entity Model](account-instruction-er-model.png)

## Service operations
1. Create Account for participant
    * REST Details - **POST** `/accounts`
        * Request Payload
            ```
            {
                "participantId":"{participantId}",
                "name":"{participantName}",
                "planId":"{planId}"
            }
            ```
        * Response Payload
            > No Body
    * SOAP Body
        * Request body
            ```
                <AddParticpantRequest participant-id="{participantId" name="{participantName" plan-id="{planId}">
                </AddParticpantRequest>
            ```
        * Response body
            ```
                <AddParticpantResponse/>
            ```
2. Enroll participant in plan
    * REST Details - **POST** `/accounts/{participantId}/plans`
        * Request Payload
            ```
            {
                "planId":"{planId}"
            }
            ```
        * Response Payload
            > No Body
    * SOAP Body
        * Request body
            ```
                <EnrollParticpantInPlanRequest participant-id="{participantId" plan-id="{planId}">
                </EnrollParticpantInPlanRequest>
            ```
        * Response body
            ```
                <EnrollParticpantInPlanResponse/>
            ```
3. Set contribution instructions for an account
    * REST Details - **POST** `/instructions/{participantId}/plans`
        * Request Payload
            ```
            {
                "instructions":[{
                    "id": "{planId}",
                    "split": 100
                }]
            }
            ```
        * Response Payload
            > No Body
    * SOAP Body
        * Request body
            ```
                <SetPlanInstructionsRequest participant-id="{participantId">
                    <Instruction id="{planId}" split="100"/>
                </SetPlanInstructionsRequest>
            ```
        * Response body
            ```
                <SetPlanInstructionsResponse/>
            ```
     ### Details
     * The split amount is the percentage the plan gets from the contribution.
     * This operation replaces the existing instructions for the plans but not the funds.
     * The total of the split amounts across all plans must be 100%
     * The elements in the request must match ALL the plans the participant has enrolled in.
     * For the SOAP endpoint, any error results in a SOAPFault.
     
4. Set fund instructions for an account within the plan.
    * REST Details - **POST** `/instructions/{participantId}/plans/{planId}/funds`
        * Request Payload
            ```
            {
                "instructions":[{
                    "id": "{fundId}",
                    "split": 100
                }]
            }
            ```
        * Response Payload
            > No Body
    * SOAP Body
        * Request body
            ```
                <SetFundInstructionsRequest participant-id="{participantId" plan-id="{planId}">
                    <Instruction id="{fundId}" split="100"/>
                </SetFundInstructionsRequest>
            ```
        * Response body
            ```
                <SetFundInstructionsResponse/>
            ```
     ### Details
     * The split amount is the percentage the fund gets from the plan allocation.
     * This operation replaces the existing instructions for the funds within the plan. Other plan instructions are not affected.
     * The total of the split amounts across all funds must be 100%
     * The elements in the request must match ALL the funds within the plan.
     * For the SOAP endpoint, any error results in a SOAPFault.
     
5. Get the current instructions for the account.
    * REST Details - **GET** `/instructions/{participantId}`
        * Request Payload
            > No Body
        * Response Payload
            ```
            {
                "participantId":"{participantId}",
                "instructions":[{
                    "plan": {
                        "id":"{planId}",
                        "name":"{planName}"
                    },
                    "split": 100,
                    "funds":[ {
                        "id":"{fundId}",
                        "name":"{fundName}",
                        "split":100
                    }]
                }]
            }
            ```
    * SOAP Body
        * Request body
            ```
                <GetAccountInstructionsRequest participant-id="{participantId"/>
            ```
        * Response body
            ```
                <GetAccountInstructionsResponse participant-id="{participantId}">
                    <Instruction split="100">
                        <Plan id="{planId}" name="{planName}"/>
                        <Fund id="{fundId}" name="{fundName}" split="100"/>
                    </Instruction>
                </GetAccountInstructionsResponse>
            ```