package com.cognizant.samples.ai.instructions;

import com.cognizant.samples.ai.ApplicationException;
import lombok.Getter;
import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "${faultString.ObjectNotFoundException}")
public class ObjectNotFoundException extends ApplicationException {

    @Getter
    private final String type;
    @Getter
    private final String id;

    public ObjectNotFoundException(String type, String id) {
        super(String.format("No [%s] found with id [%s]", type, id));
        this.type = type;
        this.id = id;
    }

    @Override
    public String code() {
        return type + ".not-found";
    }
}
