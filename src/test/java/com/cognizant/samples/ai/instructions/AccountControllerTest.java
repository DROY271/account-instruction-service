package com.cognizant.samples.ai.instructions;

import com.cognizant.samples.account_instructions.AddParticipantRequestType;
import com.cognizant.samples.account_instructions.AddParticipantResponseFault;
import com.cognizant.samples.ai.ApplicationException;
import com.cognizant.samples.ai.instructions.endpoint.AccountController;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AccountControllerTest {
    //we need to have instance of restcontroller
    //participant object we need to create
    //we need to call the controller
    private AccountController controller;

    private AccountService service = mock(AccountService.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        controller = new AccountController(service);
    }

    @Test
    public void validCreateAccount() throws AccountAlreadyExistsException, ObjectNotFoundException {
        AddParticipantRequestType request = new AddParticipantRequestType();
        request.setParticipantId("PR000001");
        request.setName("Test");
        request.setPlanId("P000001");
        //
        controller.createAccount(request);

        ArgumentCaptor<String> participantCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> planCaptor = ArgumentCaptor.forClass(String.class);
        verify(service).createAccount(participantCaptor.capture(),nameCaptor.capture(),planCaptor.capture());
        assertThat(participantCaptor.getValue()).isEqualTo("PR000001");
        assertThat(nameCaptor.getValue()).isEqualTo("Test");
        assertThat(planCaptor.getValue()).isEqualTo("P000001");
    }


    @Test
    public void mustThrowExceptionIfAlreadyExists() throws AccountAlreadyExistsException, ObjectNotFoundException {
        exception.expect(AccountAlreadyExistsException.class);
        exception.expect(new ApplicationExceptionMatcher("account.already-exists", "Account already exists for participant[PR000001]"));
        doThrow(new AccountAlreadyExistsException("PR000001")).when(service).createAccount("PR000001", "Test", "P000001");

        AddParticipantRequestType request = new AddParticipantRequestType();
        request.setParticipantId("PR000001");
        request.setName("Test");
        request.setPlanId("P000001");

        controller.createAccount(request);
    }


    private static class ApplicationExceptionMatcher extends BaseMatcher<ApplicationException> {

        private String code;
        private String message;

        ApplicationExceptionMatcher(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Code:").appendValue(code).appendText(" Message:").appendValue(message);
        }

        @Override
        public boolean matches(Object item) {
            ApplicationException fault = (ApplicationException) item;
            String message = fault.description();
            String code = fault.code();
            System.out.println("message:" + message + " code:" + code);
            return code.equals(this.code) && message.equals(this.message);
        }
    }

}
