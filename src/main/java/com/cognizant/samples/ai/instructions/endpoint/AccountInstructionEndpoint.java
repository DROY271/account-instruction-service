package com.cognizant.samples.ai.instructions.endpoint;

import com.cognizant.samples.account_instructions.AddParticipantRequestType;
import com.cognizant.samples.account_instructions.AddParticipantResponseFault;
import com.cognizant.samples.account_instructions.ObjectFactory;
import com.cognizant.samples.account_instructions.StandardResponseType;
import com.cognizant.samples.ai.instructions.AccountAlreadyExistsException;
import com.cognizant.samples.ai.instructions.AccountService;
import com.cognizant.samples.ai.instructions.ObjectNotFoundException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

@Endpoint
public class AccountInstructionEndpoint {

    public static final String NAMESPACE = "http://samples.cognizant.com/account-instructions";

    private final ObjectFactory factory = new ObjectFactory();
    private final AccountService service;

    public AccountInstructionEndpoint(AccountService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "AddParticipantRequest")
    @ResponsePayload
    public JAXBElement<StandardResponseType> addParticipant(@RequestPayload JAXBElement<AddParticipantRequestType> requestElement) throws AccountAlreadyExistsException, ObjectNotFoundException {
        AddParticipantRequestType request = requestElement.getValue();
        service.createAccount(request.getParticipantId(), request.getName(), request.getPlanId());
        return factory.createAddParticipantResponse(factory.createStandardResponseType());
    }



}
