package com.cognizant.samples.ai.dao;

import com.cognizant.samples.ai.instructions.Account;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountDAO {

    public static final String INSERT_ACCOUNT = "INSERT INTO account (name, participant_id) VALUES (?, ?)";

    private JdbcOperations ops;

    public AccountDAO(JdbcOperations jdbcTemplate) {
        this.ops = jdbcTemplate;
    }

    public void create(Account account) {
        ops.update(INSERT_ACCOUNT, account.getName(), account.getParticipantId());
    }
}
