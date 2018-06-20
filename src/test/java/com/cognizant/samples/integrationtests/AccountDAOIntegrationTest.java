package com.cognizant.samples.integrationtests;

import com.cognizant.samples.ai.AccountinstructionsApplication;
import com.cognizant.samples.ai.dao.AccountDAO;
import com.cognizant.samples.ai.instructions.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = AccountinstructionsApplication.class)
@RunWith(SpringRunner.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reset.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:plan_data.sql")
public class AccountDAOIntegrationTest {
    @Autowired
    AccountDAO dao;

    @Autowired
    JdbcTemplate template;

    @Test
    public void testCreate() {
        assertThat(JdbcTestUtils.countRowsInTable(template, "account")).isEqualTo(0);
        Account acct = new Account();
        acct.setParticipantId("12345");
        acct.setName("Test");
        dao.create(acct);
        assertThat(JdbcTestUtils.countRowsInTable(template, "account")).isEqualTo(1);
    }


}
