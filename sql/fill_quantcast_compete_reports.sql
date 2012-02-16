CREATE OR REPLACE PROCEDURE fill_quantcast_compete_reports
IS
   max_month DATE;
   max_month_ii DATE;
   max_month_iii DATE;
   max_month_iv DATE;
BEGIN

SELECT MAX(month) INTO max_month FROM quantcast_compete_data WHERE month<=SYSDATE;
SELECT MAX(month) INTO max_month_ii FROM quantcast_compete_data WHERE month<max_month;
SELECT MAX(month) INTO max_month_iii FROM quantcast_compete_data WHERE month<max_month_ii;
SELECT MAX(month) INTO max_month_iv FROM quantcast_compete_data WHERE month<max_month_iii;

DELETE FROM three_month_change;

INSERT INTO three_month_change SELECT t1.domain, t1.month, t2.month, t1.unique_visitors, CASE WHEN t2.unique_visitors=0 THEN 0 ELSE (t1.unique_visitors-t2.unique_visitors)/t2.unique_visitors END FROM quantcast_compete_data t1, quantcast_compete_data t2 WHERE ( t2.domain=t1.domain AND t1.month=max_month AND t2.month=max_month_iv ) ORDER BY domain ASC;

DELETE FROM quantcast_cum_growth;

INSERT INTO quantcast_cum_growth SELECT domain, EXP(SUM(LN(1.0+visitor_growth)))-1, AVG(visitor_growth)/STDDEV(visitor_growth) FROM quantcast_compete_data GROUP BY domain HAVING STDDEV(visitor_growth)<>0;

DELETE FROM quantcast_report;

INSERT INTO quantcast_report SELECT t1.domain, t1.last_month, t1.unique_visitors, t1.visitor_change, t2.cumulative_growth, t2.sharpe_growth FROM three_month_change t1, quantcast_cum_growth t2 WHERE ( t2.domain=t1.domain ) ORDER BY t1.visitor_change DESC;

END;

//
------------

CREATE TABLE three_month_change ( domain VARCHAR(1024), last_month DATE, four_months_ago DATE, unique_visitors INTEGER, visitor_change DOUBLE PRECISION );


CREATE TABLE quantcast_cum_growth ( domain VARCHAR(1024), cumulative_growth DOUBLE PRECISION, sharpe_growth DOUBLE PRECISION );
