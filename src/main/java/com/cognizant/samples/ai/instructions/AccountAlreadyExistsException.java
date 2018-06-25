package com.cognizant.samples.ai.instructions;

import com.cognizant.samples.ai.ApplicationException;
import lombok.Getter;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "${faultString.AccountAlreadyExistsException}")
public class AccountAlreadyExistsException extends ApplicationException {

    @Getter
    private String participantId;

    public AccountAlreadyExistsException(String participantId) {
        super(String.format("Account already exists for participant[%s]", participantId));
    }

    @Override
    public String code() {
        return "account.already-exists";
    }
}
