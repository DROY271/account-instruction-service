package com.cognizant.samples.integrationtests;

import com.cognizant.samples.account_instructions.AccountInstructionsPortType;
import com.cognizant.samples.account_instructions.AccountInstructionsService;
import com.cognizant.samples.account_instructions.AddParticipantRequestType;
import com.cognizant.samples.account_instructions.AddParticipantResponseFault;
import com.cognizant.samples.ai.AccountinstructionsApplication;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountinstructionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:reset_tables.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql")
public class JaxWsTest {

    @LocalServerPort
    private int localPort;

    private String url;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        url = String.format("http://localhost:%d/services", localPort);
    }



    @Test
    public void validJaxWsException () throws AddParticipantResponseFault {
        exception.expect(AddParticipantResponseFault.class);
        exception.expect(new AddParticipantResponseFaultMatcher("account.already-exists", "Account already exists for participant[123]"));
        AccountInstructionsPortType port = getAccountInstructionsPort();
        AddParticipantRequestType req = new AddParticipantRequestType();
        req.setName("Test");
        req.setParticipantId("123");
        req.setPlanId("P00001");
        port.addParticipant(req);
        port.addParticipant(req); // Should fail here
    }

    private AccountInstructionsPortType getAccountInstructionsPort() {
        AccountInstructionsService svc = new AccountInstructionsService();
        AccountInstructionsPortType port = svc.getAccountInstructionsServicePort();
        BindingProvider bp = (BindingProvider) port;
        bp.getBinding().setHandlerChain(Collections.singletonList(new SOAPLoggingHandler()));
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        return port;
    }

    static class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

        // change this to redirect output if desired
        private static PrintStream out = System.out;

        public Set<QName> getHeaders() {
            return null;
        }

        public boolean handleMessage(SOAPMessageContext smc) {
            logToSystemOut(smc);
            return true;
        }

        public boolean handleFault(SOAPMessageContext smc) {
            logToSystemOut(smc);
            return true;
        }

        // nothing to clean up
        public void close(MessageContext messageContext) {
        }

        /*
         * Check the MESSAGE_OUTBOUND_PROPERTY in the context
         * to see if this is an outgoing or incoming message.
         * Write a brief message to the print stream and
         * output the message. The writeTo() method can throw
         * SOAPException or IOException
         */
        private void logToSystemOut(SOAPMessageContext smc) {
            Boolean outboundProperty = (Boolean)
                    smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (outboundProperty) {
                out.println("\nOutbound message:");
            } else {
                out.println("\nInbound message:");
            }

            SOAPMessage message = smc.getMessage();
            try {
                message.writeTo(out);
                out.println();   // just to add a newline
            } catch (Exception e) {
                out.println("Exception in handler: " + e);
            }
        }
    }

    private static class AddParticipantResponseFaultMatcher extends BaseMatcher<AddParticipantResponseFault> {

        private String code;
        private String message;

        AddParticipantResponseFaultMatcher(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Code:").appendValue(code).appendText(" Message:").appendValue(message);
        }

        @Override
        public boolean matches(Object item) {
            AddParticipantResponseFault fault = (AddParticipantResponseFault) item;
            String message = fault.getFaultInfo().getMessage();
            String code = fault.getFaultInfo().getCode();
            System.out.println("message:" + message + " code:" + code);
            return code.equals(this.code) && message.equals(this.message);
        }
    }
}
