package com.cognizant.samples.integrationtests;

import com.cognizant.samples.ai.AccountinstructionsApplication;
import com.cognizant.samples.ai.dao.AccountDAO;
import com.cognizant.samples.ai.instructions.Account;
import com.cognizant.samples.ai.instructions.AccountAlreadyExistsException;
import com.cognizant.samples.ai.instructions.FundInstruction;
import com.cognizant.samples.ai.instructions.PlanInstruction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AccountinstructionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(SpringRunner.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reset_tables.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
public class AccountDAOIntegrationTest {
    @Autowired
    private
    AccountDAO dao;

    @Autowired
    private
    JdbcTemplate template;

    @Test
    public void testCreate() throws Exception {
        // Plan Id must exist in DB.
        final String planId = "P00001";
        // Create Account row.
        String participantId = "12345";
        String name = "Test";
        Account acct = createAccount(participantId, name);
        // Create Plan Instruction row
        PlanInstruction pi = createPlanInstruction(acct.getId(), planId, 100);
        int piId = pi.getId();

        createFundInstructions(
                new FundInstruction(piId, "F00001", 50),
                new FundInstruction(piId, "F00002", 50));


    }

    private void createFundInstructions(FundInstruction...fi) {
        int fiRowCount = JdbcTestUtils.countRowsInTable(template, "fund_instruction");
        dao.createFundInstruction(Arrays.asList(fi));
        assertThat(JdbcTestUtils.countRowsInTable(template, "fund_instruction")).isEqualTo(fiRowCount + fi.length);
    }

    private PlanInstruction createPlanInstruction(int acctId, String planId, int percentage) {
        PlanInstruction pi = new PlanInstruction();
        pi.setAccountId(acctId);
        pi.setPercentage(percentage);
        pi.setPlanId(planId);

        int piRowCount = JdbcTestUtils.countRowsInTable(template, "plan_instruction");
        pi = dao.createPlanInstruction(pi);
        assertThat(JdbcTestUtils.countRowsInTable(template, "plan_instruction")).isEqualTo(piRowCount + 1);
        assertThat(pi.getId()).isNotEqualTo(0);
        return pi;
    }

    private Account createAccount(String participantId, String name) throws AccountAlreadyExistsException {
        int acctRowCount = JdbcTestUtils.countRowsInTable(template, "account");
        Account acct = new Account();
        acct.setParticipantId(participantId);
        acct.setName(name);
        acct = dao.create(acct);
        assertThat(JdbcTestUtils.countRowsInTable(template, "account")).isEqualTo(acctRowCount + 1);
        assertThat(acct.getId()).isNotEqualTo(0);
        return acct;
    }


}
