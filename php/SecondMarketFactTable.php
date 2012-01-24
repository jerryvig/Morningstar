<?php
$dbh = oci_connect('morningstar','uptime5','localhost/xe');
$s = oci_parse($dbh,'SELECT * FROM second_market_company_info');
oci_execute( $s );

$second_market_company_info = array();
while ( $row = oci_fetch_array( $s, OCI_ASSOC ) ) {
  array_push( $second_market_company_info, $row );
}

oci_free_statement( $s );
$fh = fopen( "/tmp/second_market_facts.csv", "w" );

foreach ( $second_market_company_info as &$row ) {
  $url = trim($row['COMPANY_URL']);
  $pieces = parse_url( $url );
  $domain = "";
  if ( isset($pieces['host']) ) {
    $host_pieces = explode( ".", trim($pieces['host']) );
    $domain = $host_pieces[count($host_pieces)-2] . '.' . $host_pieces[count($host_pieces)-1];
  }

  $max_month = '';
  $min_month = '';
  $unique_visitors = 0;
  $visitor_growth = 0.0;

  $query_string = "SELECT * FROM second_market_visitor_growth WHERE domain='" . $domain . "'";
  $s2 = oci_parse( $dbh, $query_string );
  oci_execute( $s2 );
  while ( $vRow = oci_fetch_array( $s2, OCI_ASSOC ) ) {
    $max_month = trim($vRow['MAX_MONTH']);
    $min_month = trim($vRow['MIN_MONTH']);
    $unique_visitors = trim($vRow['UNIQUE_VISITORS']);
    $visitor_growth = trim($vRow['VISITOR_GROWTH']);
  }
   
  fwrite( $fh, '"' . $row['COMPANY_NAME'] . '","' . $row['CITY'] . '","' . $row['STATE'] . '","' . $row['SECOND_MARKET_URL'] . '","' . $row['COMPANY_URL'] . '","' . $row['LAST_FUNDING_DATE'] . '","' . $row['LAST_FUNDING_AMOUNT'] . '","' . $max_month . '","' . $min_month . '","' . $unique_visitors . '","' . $visitor_growth . '"' . "\n" );
  oci_free_statement( $s2 );
}

fclose( $fh );
oci_close( $dbh );
?>
