SELECT load_extension('./libsqlitefunctions.so');

DROP TABLE IF EXISTS ordered_data;
DROP TABLE IF EXISTS visitor_growth;
DROP TABLE IF EXISTS sharpe_growth;

CREATE TABLE ordered_data ( domain TEXT, year_month TEXT, unique_visitors INTEGER );

INSERT INTO ordered_data SELECT domain, year_month, unique_visitors FROM compete_data ORDER BY domain ASC, year_month ASC;

CREATE TABLE visitor_growth ( domain TEXT, year_month TEXT, unique_visitors INTEGER, visitor_growth REAL );

INSERT INTO visitor_growth SELECT t1.domain, t1.year_month, t1.unique_visitors, CAST((t1.unique_visitors-t2.unique_visitors) AS REAL)/CAST(t2.unique_visitors AS REAL) FROM ordered_data AS t1 INNER JOIN ordered_data AS t2 ON (t2.domain=t1.domain AND t2.rowid=(t1.rowid-1)) ORDER BY t1.rowid ASC;

--CREATE TEMPORARY TABLE cumulative_visitor_growth ( domain TEXT, cum_growth REAL );

--INSERT INTO cumulative_visitor_growth SELECT domain, EXP(SUM(LOG(1.0+visitor_growth)))-1.0 FROM visitor_growth WHERE visitor_growth > -1.0 GROUP by domain;

CREATE TABLE sharpe_growth ( domain TEXT, max_unique_visitors, cum_growth REAL, sharpe_growth REAL );

INSERT INTO sharpe_growth SELECT domain, MAX(unique_visitors), EXP(SUM(LOG(1.0+visitor_growth)))-1.0, AVG(visitor_growth)/STDEV(visitor_growth) FROM visitor_growth WHERE visitor_growth>-1.0 GROUP BY domain;
