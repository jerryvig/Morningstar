CREATE OR REPLACE PROCEDURE fill_second_mkt_visitor_growth
IS
BEGIN

INSERT INTO max_months SELECT domain, MAX(month) FROM second_market_compete_data GROUP BY domain;

INSERT INTO min_months SELECT domain, MIN(month) FROM second_market_compete_data GROUP BY domain;

INSERT INTO max_month_visitors SELECT t1.domain, t1.month, t1.unique_visitors FROM second_market_compete_data t1, max_months t2 WHERE ( t2.domain=t1.domain AND t1.month=t2.max_month );

INSERT INTO min_month_visitors SELECT t1.domain, t1.month, t1.unique_visitors FROM second_market_compete_data t1, min_months t2  WHERE ( t2.domain=t1.domain AND t1.month=t2.min_month );

DELETE FROM second_market_visitor_growth;

INSERT INTO second_market_visitor_growth SELECT t1.domain, t1.month, t2.month, t1.unique_visitors, (t1.unique_visitors-t2.unique_visitors)/t2.unique_visitors FROM max_month_visitors t1, min_month_visitors t2 WHERE ( t2.domain=t1.domain AND t2.unique_visitors>0 ) ORDER BY domain ASC;

END;
