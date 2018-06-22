package com.cognizant.samples.ai.dao;

import com.cognizant.samples.ai.plan.Fund;
import com.cognizant.samples.ai.plan.Plan;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;

@Component
public class PlanDAO {

    static final String GET_PLAN_WITH_ID = "SELECT * FROM plan WHERE id = ?";

    static final String GET_FUNDS_FOR_PLAN = "SELECT f.id,f.name FROM plan_fund pf\n" +
            "INNER JOIN fund f ON pf.fund_id=f.id\n" +
            "WHERE pf.plan_id= ?";

    private final JdbcOperations jdbcOperations;

    PlanDAO(JdbcOperations o) {
        this.jdbcOperations = o;
    }

    public Plan getPlan(String planId) {
        List<Plan> plans = jdbcOperations.query(GET_PLAN_WITH_ID, new BeanPropertyRowMapper<>(Plan.class), planId);
        Plan plan = (plans.isEmpty()) ? null : plans.get(0);
        if (plan != null) {
            plan.setFunds(new LinkedHashSet<>(getFunds(planId)));
        }
        return plan;
    }

    private List<Fund> getFunds(String planId) {
        return jdbcOperations.query(GET_FUNDS_FOR_PLAN, new BeanPropertyRowMapper<>(Fund.class), planId);
    }


}
