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
import org.springframework.core.ParameterizedTypeReference;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
        // Mock data setup
        configureMockedPlan(planWithSuffix(1,funds(1)));
        int accountId = 5000;
        int planInstructionId = 101;
        configureMockedAccount(accountId);
        configureMockedPlanInstruction(planInstructionId);
        // Invoke service
        service.createAccount("Pa00001","Test","P00001");

        // Verify Account
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accounts).create(accountCaptor.capture());
        Account actual = accountCaptor.getValue();
        assertThat(actual.getParticipantId()).isEqualTo("Pa00001");
        assertThat(actual.getName()).isEqualTo("Test");
        assertThat(actual.getId()).isEqualTo(5000);

        // Verify Plan Instructions
        ArgumentCaptor<PlanInstruction> piCaptor = ArgumentCaptor.forClass(PlanInstruction.class);
        verify(accounts).createPlanInstruction(piCaptor.capture());
        PlanInstruction pi = piCaptor.getValue();
        assertThat(pi.getPlanId()).isEqualTo("P00001");
        assertThat(pi.getPercentage()).describedAs("Default %age of 100%").isEqualTo(100);
        assertThat(pi.getAccountId()).isEqualTo(accountId);

        // Verify Fund Instructions
        ArgumentCaptor<List<FundInstruction>> fiCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(accounts).createFundInstruction( fiCaptor.capture());
        List<FundInstruction> fi=fiCaptor.getValue();
        assertThat(fi.get(0).getFundId()).isEqualTo("F00001");
        assertThat(fi.get(0).getPercentage()).describedAs("Default %age of 100%").isEqualTo(100);
        assertThat(fi.get(0).getPlanInsId()).isEqualTo(planInstructionId);
    }

    @Test
    public void createAcountFailsWhenAccountAlreadyExists() throws AccountAlreadyExistsException, ObjectNotFoundException {
        configureMockedPlan(planWithSuffix(1,funds(1)));
        doThrow(new AccountAlreadyExistsException("Pa00001")).when(accounts).create(any(Account.class));
        exception.expect(AccountAlreadyExistsException.class);
        exception.expectMessage(contains("Pa00001"));
        service.createAccount("Pa00001","Test","P00001");
    }

    @Test
    public void failsWhenPlanIsNotAvailable() throws AccountAlreadyExistsException, ObjectNotFoundException {
        exception.expect(ObjectNotFoundException.class);
        exception.expectMessage(contains("P00001"));
        service.createAccount("Pa00001","Test","P00001");
    }
    @Test
    public void twoFundPlanMustGetEvenSplit() throws AccountAlreadyExistsException, ObjectNotFoundException {
        configureMockedPlan(planWithSuffix(1,funds(2)));

        configureMockedAccount(5000);
        configureMockedPlanInstruction(101);
        // Invoke service
        service.createAccount("Pa00001","Test","P00001");

        Map<String, Integer> fundPercentages = getFundCalculatedPercentage();
        //lookup the fundid for percentage to assert the expected percentage
        assertThat(fundPercentages).containsOnly(
                entry("F00001", 50),
                entry("F00002", 50));
    }
    @Test
    public void threeFundPlanMustGetEvenOddSplit() throws AccountAlreadyExistsException, ObjectNotFoundException {
        configureMockedPlan(planWithSuffix(1,funds(3)));

        configureMockedAccount(5000);
        configureMockedPlanInstruction(101);
        // Invoke service
        service.createAccount("Pa00001","Test","P00001");

        Map<String, Integer> fundPercentages = getFundCalculatedPercentage();
        //lookup the fundid for percentage to assert the expected percentage
        assertThat(fundPercentages).containsOnly(
                entry("F00001", 34),
                entry("F00002", 33),
                entry("F00003", 33));
    }
    private Map<String, Integer> getFundCalculatedPercentage() {
        ArgumentCaptor<List<FundInstruction>> fiCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(accounts).createFundInstruction( fiCaptor.capture());
        List<FundInstruction> fi = fiCaptor.getValue();
        //create map for fundid and fund percentage
        Map<String,Integer> fundPercentages = new HashMap<>();
        for(FundInstruction f:fi) {
            fundPercentages.put(f.getFundId(),f.getPercentage());
        }
        return fundPercentages;
    }


    private void configureMockedAccount(final int accountId) throws AccountAlreadyExistsException {
        doAnswer(invocation -> {
            Account account = (Account) invocation.getArguments()[0];
            account.setId(accountId);
            return account;
        }).when(accounts).create(any(Account.class));
    }

    private void configureMockedPlanInstruction(final int planInstructionId) throws AccountAlreadyExistsException {
        doAnswer(invocation -> {
            PlanInstruction planInstruction = (PlanInstruction) invocation.getArguments()[0];
            planInstruction.setId(planInstructionId);
            return planInstruction;
        }).when(accounts).createPlanInstruction(any(PlanInstruction.class));
    }


    private void configureMockedPlan(Plan plan) {
        doReturn(plan).when(plans).getPlan(plan.getId());
    }


    private Plan planWithSuffix(int index, Set<Fund> funds) {
        Plan p = new Plan();
        p.setId(String.format("P%05d", index));
        p.setName(String.format("Plan Name P%05d", index));
        p.setFunds(funds);
        return p;
    }

    private Set<Fund> funds(int fundCount) {
        Set<Fund> funds = new LinkedHashSet<>();
        for (int i = 0; i < fundCount; i++) {
            funds.add(fund(i + 1));
        }
        return funds;
    }

    private Fund fund(int index) {
        Fund f = new Fund();
        f.setId(String.format("F%05d", index));
        f.setName(String.format("Fund Name F%05d", index));
        return f;
    }

}
