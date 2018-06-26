package com.cognizant.samples.integrationtests;

import com.cognizant.samples.ai.AccountinstructionsApplication;
import com.cognizant.samples.ai.dao.AccountDAO;
import com.cognizant.samples.ai.instructions.Account;
import com.cognizant.samples.ai.instructions.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(classes = AccountinstructionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reset_tables.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")

public class CreateAccountRestEndpointTest {

    private static final ParameterizedTypeReference<Map<String, String>> STRING_MAP = new ParameterizedTypeReference<Map<String, String>>() {
    };

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${errors.general}")
    private String generalErrorMessage;

    @SpyBean
    AccountDAO dao;

    private Map<String, String> createAddParticipantRequest(String participantId, String name, String planId) {
        Map<String, String> request = new HashMap<>();
        request.put("participantId", participantId);
        request.put("planId", planId);
        request.put("name", name);
        return request;
    }

    @Test
    public void createAccountTest(){
        Map<String, String> request = createAddParticipantRequest("Pa12345", "TestName", "P00001");
        ResponseEntity<String> response = testRestTemplate.postForEntity("/accounts", request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void failsWhenPlanNotFound(){
        Map<String, String> request = createAddParticipantRequest("Pa12345", "TestName", "P00009");

        ResponseEntity<Map<String, String>> response = testRestTemplate.exchange(new RequestEntity<Map<String,String>>(request, HttpMethod.POST, URI.create("/accounts")), STRING_MAP);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Map<String, String> body = response.getBody();
        assertThat(body.get("code")).isEqualTo("plan.not-found");
    }


    @Test
    public void failsWhenAccountAlreadyExists(){
        Map<String, String> request = createAddParticipantRequest("Pa12345", "TestName", "P00001");
        ResponseEntity<String> firstResponse = testRestTemplate.postForEntity("/accounts", request, String.class);

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String,String>> response = testRestTemplate.exchange(new RequestEntity<Map<String,String>>(request, HttpMethod.POST, URI.create("/accounts")), STRING_MAP);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = response.getBody();
        assertThat(body.get("code")).isEqualTo("account.already-exists");
    }


    @Test
    public void failIfDbException() throws Exception {
        doThrow(new DataSourceLookupFailureException("Test error")).when(dao).create(any(Account.class));
        Map<String, String> request = createAddParticipantRequest("Pa12345", "TestName", "P00001");

        ResponseEntity<Map<String,String>> response = testRestTemplate.exchange(new RequestEntity<Map<String,String>>(request, HttpMethod.POST, URI.create("/accounts")), STRING_MAP);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, String> body = response.getBody();
        assertThat(body.get("code")).isEqualTo("general");
        assertThat(body.get("message")).isEqualTo(generalErrorMessage);
    }



}
