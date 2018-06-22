package com.cognizant.samples.ai.instructions;

import com.cognizant.samples.ai.dao.AccountDAO;
import com.cognizant.samples.ai.dao.PlanDAO;
import com.cognizant.samples.ai.plan.Fund;
import com.cognizant.samples.ai.plan.Plan;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AccountServiceTests {

    private PlanDAO plans = mock(PlanDAO.class);
    private AccountDAO accounts = mock(AccountDAO.class);
    private AccountService service;

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() {
        service = new AccountService(plans, accounts);
    }

    @Test
    public void createAccountTest() throws AccountAlreadyExistsException, ObjectNotFoundException {
        // Mock plan data
        Plan plan =new Plan();
        plan.setId("Plan001");
        plan.setName("Plan Name1");
        Fund fund=new Fund();
        fund.setId("F00001");
        fund.setName("Fund Name1");
        Set<Fund> funds=new HashSet<>();
        funds.add(fund);
        plan.setFunds(funds);
        doReturn(plan).when(plans).getPlan(plan.getId());


        // Mocking account data

        doAnswer(invocation -> {
            Account account = (Account) invocation.getArguments()[0];
            account.setId(5000);
            return account;
        }).when(accounts).create(any(Account.class));

        // Mock plan instructions
        // Mock fund instructions
        // Invoke service


        service.createAccount("P00001","Test","Plan001");

        // Verify Account
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accounts).create(accountCaptor.capture());
        Account actual = accountCaptor.getValue();
        assertThat(actual.getParticipantId()).isEqualTo("P00001");
        assertThat(actual.getName()).isEqualTo("Test");
        assertThat(actual.getId()).isEqualTo(5000);

        // Verify Plan Instructions
        ArgumentCaptor<PlanInstruction> piCaptor = ArgumentCaptor.forClass(PlanInstruction.class);
        verify(accounts).createPlanInstruction(piCaptor.capture());
        PlanInstruction pi = piCaptor.getValue();
        assertThat(pi.getPlanId()).isEqualTo("Plan001");
        assertThat(pi.getPercentage()).isEqualTo(100);
        assertThat(pi.getAccountId()).isEqualTo(5000);

        // Verify Fund Instructions

    }



}
