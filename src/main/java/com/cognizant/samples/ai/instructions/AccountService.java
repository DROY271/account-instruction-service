package com.cognizant.samples.ai.instructions;

import com.cognizant.samples.ai.dao.AccountDAO;
import com.cognizant.samples.ai.dao.PlanDAO;
import com.cognizant.samples.ai.plan.Plan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private PlanDAO plans;
    private AccountDAO accounts;

    public AccountService(PlanDAO plans, AccountDAO accounts) {
        this.plans = plans;
        this.accounts = accounts;
    }

    public void createAccount(String participantId, String name, String planId) throws ObjectNotFoundException, AccountAlreadyExistsException {
        Plan plan = plans.getPlan(planId);
        if (plan == null) {
            throw new ObjectNotFoundException("plan", planId);
        }
        // Create Account
        Account account = new Account();
        account.setName(name);
        account.setParticipantId(participantId);
        account = accounts.create(account);
        // Create PlanInstruction for plan with 100%
        final PlanInstruction p = new PlanInstruction();
        p.setAccountId(account.getId());
        p.setPlanId(plan.getId());
        p.setPercentage(100);
        accounts.createPlanInstruction(p);
        // Create FundInstruction for funds evenly distributed.
        final int eachFund = 100 / plan.getFunds().size();
        List<FundInstruction> fi = plan.getFunds()
                .stream()
                .map(f -> new FundInstruction(p.getId(), f.getId(), eachFund))
                .collect(Collectors.toList());
        FundInstruction f = fi.get(0);
        f.setPercentage(eachFund + (100 - (eachFund * fi.size())));
        accounts.createFundInstruction(fi);
    }

}
