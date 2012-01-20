CREATE OR REPLACE PROCEDURE fill_morningstar_rev_fact_tbl
IS
BEGIN

INSERT INTO ordered_morningstar_revenue SELECT order_seq.nextval, iview.* FROM ( SELECT ticker_symbol, SUBSTR(period,1,4), revenue FROM morningstar_revenue WHERE ( SUBSTR(period,1,4)='2006' OR  SUBSTR(period,1,4)='2007' OR SUBSTR(period,1,4)='2008' OR SUBSTR(period,1,4)='2009' OR SUBSTR(period,1,4)='2010' OR SUBSTR(period,1,4)='TTM' ) ORDER BY ticker_symbol ASC, period ASC ) iview;

INSERT INTO revenue_growth SELECT t1.idx, t1.ticker_symbol, t1.period, t1.revenue, CASE WHEN t2.revenue>0 THEN (t1.revenue-t2.revenue)/t2.revenue ELSE 0.0 END FROM ordered_morningstar_revenue t1, ordered_morningstar_revenue t2 WHERE ( t2.ticker_symbol=t1.ticker_symbol AND t2.idx=(t1.idx-1) ) ORDER BY t1.idx ASC;

INSERT INTO revenue_growth_aggregates SELECT ticker_symbol, COUNT(*), AVG(revenue_growth), CASE WHEN STDDEV(revenue_growth)>0 THEN AVG(revenue_growth)/STDDEV(revenue_growth) ELSE 0.0 END FROM revenue_growth GROUP BY ticker_symbol ORDER BY ticker_symbol ASC;

INSERT INTO rev_gr_agg_profile_info SELECT t1.*, t2.company_name, t2.phone, t2.fax, t2.address, t2.website, t2.index_membership, t2.sector, t2.industry, t2.full_time_employees FROM revenue_growth_aggregates t1, yahoo_profile_info t2 WHERE ( t1.ticker_symbol=t2.ticker_symbol ) ORDER BY sharpe_revenue_growth DESC;
                   
INSERT INTO revenues_rev_growth_by_year SELECT ticker_symbol, SUM(CASE WHEN period='2006' THEN revenue ELSE 0.0 END) AS revenue_2006,  SUM(CASE WHEN period='2007' THEN revenue ELSE 0.0 END) AS revenue_2007,  SUM(CASE WHEN period='2008' THEN revenue ELSE 0.0 END) AS revenue_2008,  SUM(CASE WHEN period='2009' THEN revenue ELSE 0.0 END) AS revenue_2009,  SUM(CASE WHEN period='2010' THEN revenue ELSE 0.0 END) AS revenue_2010, SUM(CASE WHEN period='TTM' THEN revenue ELSE 0.0 END) AS revenue_ttm, SUM(CASE WHEN period='2007' THEN revenue_growth ELSE 0.0 END) AS rev_gr_2007,  SUM(CASE WHEN period='2008' THEN revenue_growth ELSE 0.0 END) AS rev_gr_2008,  SUM(CASE WHEN period='2009' THEN revenue_growth ELSE 0.0 END) AS rev_gr_2009,  SUM(CASE WHEN period='2010' THEN revenue_growth ELSE 0.0 END) AS rev_gr_2010, SUM(CASE WHEN period='TTM' THEN revenue_growth ELSE 0.0 END) AS rev_gr_ttm FROM revenue_growth GROUP BY ticker_symbol ORDER BY ticker_symbol ASC;

INSERT INTO pre_morningstar_rev_fact_table SELECT t1.*, t2.count_changes, t2.avg_revenue_growth, t2.sharpe_revenue_growth, t2.company_name, t2.phone, t2.fax, t2.address, t2.website, t2.index_membership, t2.sector, t2.industry, t2.full_time_employees FROM revenues_rev_growth_by_year t1, rev_gr_agg_profile_info t2 WHERE ( t1.ticker_symbol=t2.ticker_symbol ) ORDER BY t1.ticker_symbol ASC;

DELETE FROM morningstar_rev_fact_table;

INSERT INTO morningstar_rev_fact_table SELECT t1.*, t2.street_address, t2.city, t2.state, t2.zip_code FROM pre_morningstar_rev_fact_table t1, geocode_addresses t2 WHERE ( t2.ticker_symbol=t1.ticker_symbol ) ORDER BY t1.ticker_symbol ASC;

END;
