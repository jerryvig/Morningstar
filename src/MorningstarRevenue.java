import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

public class MorningstarRevenue {
   private static BufferedWriter writer;
   private static BufferedReader reader;

   public static void main( String[] args ) {
       ArrayList<String> tickers = new ArrayList();
       try {
	   writer = new BufferedWriter( new FileWriter("./MorningstarRevenues.csv") );
           writer.write( "\"Ticker Symbol\",\"Period\",\"Revenue\"\n" );
           reader = new BufferedReader( new FileReader("./CBOE-WEEKLIES.csv") );
           String s;
           while ( (s = reader.readLine()) != null ) {
	       tickers.add(s.trim());
           }
           reader.close();
       } catch ( IOException ioe ) { ioe.printStackTrace(); }

       for ( String tickerSymbol : tickers ) {
	   try {
            System.out.println( tickerSymbol );
            URL url = new URL( "http://financials.morningstar.com/ajax/ReportProcess4HtmlAjax.html?t="+tickerSymbol+"&region=USA&culture=en_us&reportType=is&period=12&dataType=A&order=asc&columnYear=5&rounding=3&view=raw&productCode=usa&r=154068&callback=jsonp1317998875821&_=1317999001909" );
            BufferedReader respReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String response = "";
            String s;
            while ((s = respReader.readLine())!=null) {
		response += s;
            }
            respReader.close();

            parseResponse( tickerSymbol, response );

           } catch ( MalformedURLException urle ) { urle.printStackTrace(); }
           catch ( IOException ioe ) { ioe.printStackTrace(); }
           try {
	       Thread.sleep( 250 );
           } catch ( InterruptedException ie ) { ie.printStackTrace(); }
       }
       try {
	   writer.close();
       } catch ( IOException ioe ) {  ioe.printStackTrace(); }
   }

   public static void parseResponse( String _tickerSymbol, String _response ) {
       int startIdx = _response.indexOf("<div id=\\\"baseline\\\"");
       int endIdx = _response.indexOf("<div class=\\\"left \\\">");
       if ( startIdx > 0 && startIdx < endIdx ) {
	  String block = _response.substring(startIdx, endIdx);
          String[] pieces = block.split("<div>");

          ArrayList<String> revenues = new ArrayList<String>();
          for ( int i=1; i<7; i++ ) {
	      revenues.add( pieces[i].split("<")[0].trim() );
          }  

          int nextStart = _response.indexOf("<div class=\\\"year\\\"");
          int nextEnd =  _response.indexOf("<div id=\\\"data_i1\\\"");
          if ( nextStart > 0 && nextStart < nextEnd ) {
             ArrayList<String> revenuePeriods = new ArrayList<String>();
             String[] piecesII = _response.substring( nextStart, nextEnd ).split( Pattern.quote("<br\\/>") );
             for ( int i=1; i<7; i++ ) {
		 revenuePeriods.add( piecesII[i].split(Pattern.quote("<\\/div>"))[0] );
             }
             for ( int i=0; i<6; i++ ) {
	       try {
		 writer.write( "\"" + _tickerSymbol + "\",\"" + revenuePeriods.get(i) + "\",\"" + revenues.get(i) + "\"\n" ); 
               } catch ( IOException ioe ) { ioe.printStackTrace(); }
             }             
          }
       }
   }
}