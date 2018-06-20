CREATE TABLE IF NOT EXISTS plan (
    id VARCHAR(10) NOT NULL,
    name VARCHAR(1024) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS fund (
    id VARCHAR(10) NOT NULL,
    name VARCHAR(1024) NOT NULL,
    PRIMARY KEY (id),
);

CREATE TABLE IF NOT EXISTS plan_fund (
    plan_id VARCHAR(10) NOT NULL,
    fund_id VARCHAR(10) NOT NULL,
    PRIMARY KEY (plan_id, fund_id),
    FOREIGN KEY (plan_id) REFERENCES plan(id),
    FOREIGN KEY (fund_id) REFERENCES fund(id)
);