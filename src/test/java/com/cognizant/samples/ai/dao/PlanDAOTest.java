package com.cognizant.samples.ai.dao;

import com.cognizant.samples.ai.MockFactory;
import com.cognizant.samples.ai.RowDescriptor;
import com.cognizant.samples.ai.plan.Fund;
import com.cognizant.samples.ai.plan.Plan;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlanDAOTest {

    private RowDescriptor planRowDescriptor = new RowDescriptor()
            .column("id", Types.VARCHAR)
            .column("name", Types.VARCHAR);

    private RowDescriptor fundRowDescriptor = new RowDescriptor()
            .column("id", Types.VARCHAR)
            .column("name", Types.VARCHAR);

    private Connection conn = mock(Connection.class);

    private PreparedStatement planStmt = mock(PreparedStatement.class);

    private PreparedStatement fundStmt = mock(PreparedStatement.class);

    private PlanDAO dao;

    @Before
    public void setUp() throws Exception {
        DataSource ds = Mockito.mock(DataSource.class);
        when(ds.getConnection()).thenReturn(conn);
        dao = new PlanDAO(new JdbcTemplate(ds));
    }

    @Test
    public void returnNullWhenNotFound() throws Exception {
        PreparedStatement planStmt = mock(PreparedStatement.class);
        doReturn(planStmt).when(conn).prepareStatement(eq(PlanDAO.GET_PLAN_WITH_ID));

        ResultSet rs = MockFactory.resultSet(planRowDescriptor)
                .build();
        doReturn(rs).when(planStmt).executeQuery();

        Plan p = dao.getPlan("P00001");
        assertThat(p).isNull();
    }

    @Test
    public void returnPlanAlongWithFunds() throws Exception {
        String planId = "P00001";
        String planName = "Plan name";
        String [][] fundRows = {
                {"F00001", "Fund 1"},
                {"F00002", "Fund 2"},
                {"F00003", "Fund 3"},
        };

        mockPlanData(planId, planName);
        mockFundData(fundRows);

        Plan p = dao.getPlan(planId);

        // Verify that the statements are set with the expected values.
        verify(planStmt, times(1)).setString(1, planId);
        verify(fundStmt, times(1)).setString(1, planId);

        // Assert plan information
        assertThat(p).isNotNull();
        assertThat(p.getId()).isEqualTo(planId);
        assertThat(p.getName()).isEqualTo(planName);

        // Assert fund information
        assertThat(p.getFunds()).isNotNull();
        assertThat(p.getFunds()).containsExactlyElementsOf(toFunds(fundRows));

    }

    private List<Fund> toFunds(String[][] fundRows) {
        return Arrays.stream(fundRows).map(fr -> {
                Fund f = new Fund();
                f.setId(fr[0]);
                f.setName(fr[1]);
                return f;
            }).collect(Collectors.toList());
    }

    private void mockPlanData(String planId, String planName) throws SQLException {
        doReturn(planStmt).when(conn).prepareStatement(eq(PlanDAO.GET_PLAN_WITH_ID));
        ResultSet rs = MockFactory.resultSet(planRowDescriptor)
                .add(planId, planName)
                .build();
        doReturn(rs).when(planStmt).executeQuery();
    }

    private void mockFundData(String[][] fundRows) throws SQLException {
        MockFactory.ResultSetMocker fundMock = MockFactory.resultSet(fundRowDescriptor);

        for (String[] fundRow: fundRows) {
            fundMock.add(fundRow[0], fundRow[1]);
        }
        doReturn(fundStmt).when(conn).prepareStatement(eq(PlanDAO.GET_FUNDS_FOR_PLAN));
        ResultSet fundRs = fundMock.build();
        doReturn(fundRs).when(fundStmt).executeQuery();
    }

}
