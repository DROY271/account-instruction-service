package com.cognizant.samples.ai.instructions.endpoint;

import com.cognizant.samples.account_instructions.AddParticipantRequestType;
import com.cognizant.samples.ai.instructions.AccountAlreadyExistsException;
import com.cognizant.samples.ai.instructions.AccountService;
import com.cognizant.samples.ai.instructions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/accounts")
    public void createAccount(@RequestBody AddParticipantRequestType request) throws AccountAlreadyExistsException, ObjectNotFoundException {
        service.createAccount(request.getParticipantId(), request.getName(), request.getPlanId());
    }
}
