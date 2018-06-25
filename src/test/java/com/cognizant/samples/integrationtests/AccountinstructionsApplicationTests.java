package com.cognizant.samples.integrationtests;

import com.cognizant.samples.ai.AccountinstructionsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountinstructionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reset_tables.sql")
public class AccountinstructionsApplicationTests {

	@SuppressWarnings("EmptyMethod")
    @Test
	public void contextLoads() {
	}

}
