package com.cognizant.samples.integrationtests;

import com.cognizant.samples.ai.AccountinstructionsApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=AccountinstructionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reset_tables.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
public class CreateAccountSoapEndpoint {

    @Autowired
    private ApplicationContext applicationContext;

    private MockWebServiceClient mockClient;

    private Resource xsdSchema = new ClassPathResource("wsdl/AccountInstructionsSchema.xsd");


    @Before
    public void init(){
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    public void validCreationShouldWork() throws Exception {
        String xml = "<AddParticipantRequest xmlns=\"http://samples.cognizant.com/account-instructions\" participant-id=\"Pa123456\" name=\"My Name\" plan-id=\"P00001\">\n" +
                "\t\t</AddParticipantRequest>";
        Source requestPayload = new StringSource(xml);

        mockClient
                .sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(validPayload(xsdSchema));
    }

    @Test
    public void failWhenPartiticipantAlreadyExists() throws Exception {
        String xml = "<AddParticipantRequest xmlns=\"http://samples.cognizant.com/account-instructions\" participant-id=\"Pa123456\" name=\"My Name\" plan-id=\"P00001\">\n" +
                "\t\t</AddParticipantRequest>";
        Source requestPayload = new StringSource(xml);

        mockClient
                .sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(validPayload(xsdSchema));

        mockClient
                .sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(validPayload(xsdSchema));

    }


}
