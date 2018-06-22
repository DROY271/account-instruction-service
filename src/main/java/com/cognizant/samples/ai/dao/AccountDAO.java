package com.cognizant.samples.ai.dao;

import com.cognizant.samples.ai.instructions.Account;
import com.cognizant.samples.ai.instructions.AccountAlreadyExistsException;
import com.cognizant.samples.ai.instructions.FundInstruction;
import com.cognizant.samples.ai.instructions.PlanInstruction;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class AccountDAO {

    static final class Queries {

        static final String INSERT_ACCOUNT = "INSERT INTO account (name, participant_id) VALUES (?, ?)";

        static final String GET_ACCOUNT_BY_PARTICIPANT_ID = "SELECT id, name FROM account WHERE participant_id=?";

        static final String INSERT_PARTICIPANT_INSTRUCTION = "INSERT INTO plan_instruction (account_id, percentage, plan_id) VALUES (?, ?, ?)";

        static final String INSERT_FUND_INSTRUCTION = "INSERT INTO fund_instruction (plan_ins_id, percentage, fund_id) VALUES (?, ?, ?)";
    }

    private final JdbcOperations ops;


    public AccountDAO(JdbcOperations jdbcTemplate) {
        this.ops = jdbcTemplate;
    }

    public Account create(Account account) throws AccountAlreadyExistsException {
        if (getAccountByParticipantId(account.getParticipantId()) != null) {
            throw new AccountAlreadyExistsException(account.getParticipantId());
        }
        GeneratedKeyHolder kh = new GeneratedKeyHolder();
        ops.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement st = con.prepareStatement(Queries.INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
                st.setString(1, account.getName());
                st.setString(2, account.getParticipantId());
                return st;
            }
        }, kh);
        account.setId((Integer)kh.getKey());
        return account;
    }

    public Account getAccountByParticipantId(String participantId) {
        List<Account> accounts = ops.query(Queries.GET_ACCOUNT_BY_PARTICIPANT_ID, new BeanPropertyRowMapper<>(Account.class), participantId);
        if (accounts.isEmpty()) {
            return null;
        }
        return accounts.get(0);
    }

    public PlanInstruction createPlanInstruction(PlanInstruction pi) {

        GeneratedKeyHolder kh = new GeneratedKeyHolder();
        ops.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement st = con.prepareStatement(Queries.INSERT_PARTICIPANT_INSTRUCTION, Statement.RETURN_GENERATED_KEYS);
                st.setInt(1, pi.getAccountId());
                st.setInt(2, pi.getPercentage());
                st.setString(3, pi.getPlanId());
                return st;
            }
        }, kh);
        pi.setId((Integer)kh.getKey());
        return pi;
    }

    public void createFundInstruction(List<FundInstruction> fundInstructions) {
        ops.batchUpdate(Queries.INSERT_FUND_INSTRUCTION, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                FundInstruction fi = fundInstructions.get(i);
                ps.setInt(1, fi.getPlanInsId());
                ps.setInt(2, fi.getPercentage());
                ps.setString(3, fi.getFundId());
            }

            @Override
            public int getBatchSize() {
                return fundInstructions.size();
            }
        });
    }

}
