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

CREATE TABLE IF NOT EXISTS account(
    id  INTEGER IDENTITY,
    participant_id  VARCHAR(10) NOT NULL,
    name  VARCHAR(256) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS plan_instruction (
    id INTEGER IDENTITY,
    plan_id VARCHAR(10) NOT NULL,
    account_id INTEGER  NOT NULL,
    percentage NUMERIC(3,0) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (plan_id) REFERENCES plan(id)
);

CREATE TABLE IF NOT EXISTS fund_instruction (
    id INTEGER IDENTITY,
    plan_ins_id INTEGER NOT NULL,
    fund_id VARCHAR(10) NOT NULL,
    percentage NUMERIC(3,0) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (plan_ins_id) REFERENCES plan_instruction(id),
    FOREIGN KEY (fund_id) REFERENCES fund(id),
);

