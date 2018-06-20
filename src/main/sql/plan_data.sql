INSERT INTO  plan (id,name) VALUES ('P00001', 'Plan 1: Single Fund Plan F00001');
INSERT INTO  plan (id,name) VALUES ('P00002', 'Plan 2: Plan with F00001 & F00002');
INSERT INTO  plan (id,name) VALUES ('P00003', 'Plan 3: Plan with F00003 & F00004');

INSERT INTO  fund (id,name) VALUES ('F00001', 'Fund 1');
INSERT INTO  fund (id,name) VALUES ('F00002', 'Fund 2');
INSERT INTO  fund (id,name) VALUES ('F00003', 'Fund 3');
INSERT INTO  fund (id,name) VALUES ('F00004', 'Fund 4');
INSERT INTO  fund (id,name) VALUES ('F00005', 'Fund 5');

INSERT INTO  plan_fund (plan_id,fund_id) VALUES ('P00001', 'F00001');
INSERT INTO  plan_fund (plan_id,fund_id) VALUES ('P00002', 'F00001');
INSERT INTO  plan_fund (plan_id,fund_id) VALUES ('P00002', 'F00002');
INSERT INTO  plan_fund (plan_id,fund_id) VALUES ('P00003', 'F00003');
INSERT INTO  plan_fund (plan_id,fund_id) VALUES ('P00003', 'F00004');
