package com.cognizant.samples.ai.dao;

import com.cognizant.samples.ai.MockFactory;
import com.cognizant.samples.ai.RowDescriptor;
import com.cognizant.samples.ai.instructions.Account;
import com.cognizant.samples.ai.instructions.AccountAlreadyExistsException;
import com.cognizant.samples.ai.instructions.FundInstruction;
import com.cognizant.samples.ai.instructions.PlanInstruction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AccountDAOTest {

    private final RowDescriptor accountDescriptor = new RowDescriptor()
            .column("name", Types.VARCHAR)
            .column("participant_id", Types.VARCHAR);

    private final Connection conn = mock(Connection.class);

    private final PreparedStatement stmt = mock(PreparedStatement.class);

    private AccountDAO dao;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        DataSource ds = Mockito.mock(DataSource.class);
        when(ds.getConnection()).thenReturn(conn);

        DatabaseMetaData dbmd= mock(DatabaseMetaData.class);
        doReturn(true).when(dbmd).supportsBatchUpdates();
        doReturn(dbmd).when(conn).getMetaData();
        doReturn(conn).when(stmt).getConnection();
        dao = new AccountDAO(new JdbcTemplate(ds));
    }


    @Test
    public void createTest() throws Exception {
        PreparedStatement accountQueryStmt = mock(PreparedStatement.class);
        doReturn(stmt).when(conn).prepareStatement(eq(AccountDAO.Queries.INSERT_ACCOUNT), anyInt());
        doReturn(accountQueryStmt).when(conn).prepareStatement(eq(AccountDAO.Queries.GET_ACCOUNT_BY_PARTICIPANT_ID));
        ResultSet rs = MockFactory.resultSet(accountDescriptor)
                .build();
        doReturn(rs).when(accountQueryStmt).executeQuery();

        when(stmt.executeUpdate()).thenReturn(1);

        ResultSet keys = MockFactory.resultSet(
                new RowDescriptor().column("id", Types.INTEGER)
        ).add(2000)
                .build();
        when(stmt.getGeneratedKeys()).thenReturn(keys);

        Account account = new Account();
        account.setName("Test");
        account.setParticipantId("12345");

        account = dao.create(account);
        assertThat(account.getId()).isEqualTo(2000);
        verify(stmt).setString(1, "Test");
        verify(stmt).setString(2, "12345");
    }

    @Test
    public void failIfParticipantAlreadyExists() throws Exception {
        exception.expect(AccountAlreadyExistsException.class);
        exception.expectMessage("Account already exists for participant[12345]");
        PreparedStatement accountQueryStmt = mock(PreparedStatement.class);
        doReturn(accountQueryStmt).when(conn).prepareStatement(eq(AccountDAO.Queries.GET_ACCOUNT_BY_PARTICIPANT_ID));
        ResultSet rs = MockFactory.resultSet(accountDescriptor)
                .add("Different Name", "12345")
                .build();
        doReturn(rs).when(accountQueryStmt).executeQuery();

        Account account = new Account();
        account.setName("Test");
        account.setParticipantId("12345");

        dao.create(account);

    }

    @Test
    public void createPlanInstructionTest() throws Exception {
        PreparedStatement planQueryStmt = mock(PreparedStatement.class);
        doReturn(stmt).when(conn).prepareStatement(eq(AccountDAO.Queries.INSERT_PARTICIPANT_INSTRUCTION), anyInt());
        PlanInstruction planInstruction = new PlanInstruction();
        planInstruction.setAccountId(1001);
        planInstruction.setPercentage(100);
        planInstruction.setPlanId("P001");
        when(stmt.executeUpdate()).thenReturn(1);

        ResultSet keys = MockFactory.resultSet(
                new RowDescriptor().column("id", Types.INTEGER)
        ).add(2000)
                .build();
        when(stmt.getGeneratedKeys()).thenReturn(keys);


        PlanInstruction pi = dao.createPlanInstruction(planInstruction);
        verify(stmt).setInt(1, 1001);
        verify(stmt).setInt(2, 100);
        verify(stmt).setString(3, "P001");

        assertThat(pi.getId()).isEqualTo(2000);
    }



    @Test
    public void createFundInstructionTest() throws Exception {
        doReturn(stmt).when(conn).prepareStatement(eq(AccountDAO.Queries.INSERT_FUND_INSTRUCTION));
        FundInstruction fundInstruction = new FundInstruction();
        fundInstruction.setPlanInsId(1001);
        fundInstruction.setPercentage(100);
        fundInstruction.setFundId("F0001");
        when(stmt.executeUpdate()).thenReturn(1);

        dao.createFundInstruction(Collections.singletonList(fundInstruction));
        verify(stmt).setInt(1, 1001);
        verify(stmt).setInt(2, 100);
        verify(stmt).setString(3, "F0001");
    }
}
