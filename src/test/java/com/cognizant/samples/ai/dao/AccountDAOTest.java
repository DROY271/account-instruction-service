package com.cognizant.samples.ai.dao;

import com.cognizant.samples.ai.MockFactory;
import com.cognizant.samples.ai.instructions.Account;
import com.cognizant.samples.ai.plan.Plan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AccountDAOTest {


    private Connection conn = mock(Connection.class);

    private PreparedStatement stmt = mock(PreparedStatement.class);

    private AccountDAO dao;

    @Before
    public void setUp() throws Exception {
        DataSource ds = Mockito.mock(DataSource.class);
        when(ds.getConnection()).thenReturn(conn);
        dao = new AccountDAO(new JdbcTemplate(ds));
    }


    @Test
    public void createTest() throws SQLException {
        doReturn(stmt).when(conn).prepareStatement(eq(AccountDAO.INSERT_ACCOUNT));
        when(stmt.executeUpdate()).thenReturn(1);

        Account account = new Account();
        account.setName("Test");
        account.setParticipantId("12345");

        dao.create(account);

        verify(stmt).setString(1, "Test");
        verify(stmt).setString(2, "12345");
    }
}
