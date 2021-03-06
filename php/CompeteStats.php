<?php
  date_default_timezone_set('America/Los_Angeles');

  $fh = fopen( './AlexaTop3000.csv', 'r' );
  $out = fopen( './CompeteData'. date("Y-m-d") .'.csv', 'w' );

  while (($line = fgets($fh, 4096))!=false) {
    $pieces = explode( ',', trim($line) );
    $domain = $pieces[1];    

    $url = "http://siteanalytics.compete.com/export_csv/". $domain ."/";
    system( '/usr/bin/wget -O file.csv "' . $url . '"' );
 
    $csv_handle = fopen( './file.csv', 'r' );
    $line_count = 0;
    while ( ($cols = fgets($csv_handle,4096))!== false ) {
      if ( $line_count > 3 ) {
        fputs( $out, '"' . $domain . '",' . trim($cols) . "\n" );
      }
      $line_count++;
    }
    fclose( $csv_handle );
    usleep( 800000 );
  }
  fclose( $out );
  fclose($fh);
?>