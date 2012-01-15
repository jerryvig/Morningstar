<?php
  require_once("./simple_html_dom.php");
  $dom = new simple_html_dom(); 
  $fh = fopen( "./AlexaUSList.txt", "w" );

  for ( $i=0; $i<21; $i++ ) {
   $url = "http://www.alexa.com/topsites/countries;$i/US";
   $cmd = '/usr/bin/wget -O file.html "' . $url . '"';
   system( $cmd );   

   $dom->load_file('./file.html');
   $span_list = $dom->find("span[class='small topsites-label']");

   echo "span list = " . count($span_list);
  
   foreach ( $span_list as $my_span ) {
     fputs( $fh, trim($my_span->plaintext) . "\n" );
   }

   sleep( 1 ); 
   echo $url . "\n";
  }

  fclose( $fh );
?>