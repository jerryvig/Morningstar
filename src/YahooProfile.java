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
import org.htmlparser.Parser;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class YahooProfile {
   public static void main( String[] args ) {
       BufferedWriter writer = null;
       BufferedReader reader = null;

       ArrayList<String> tickers = new ArrayList<String>();
       try {
	   writer = new BufferedWriter( new FileWriter("./YahooProfiles.csv") );
           writer.write( "\"Ticker Symbol\",\"Company Name\",\"Phone Number\",\"Fax Number\",\"Address\",\"Website\",\"Index Membership\",\"Sector\",\"Industry\",\"Full Time Employees\"\n" );
           reader = new BufferedReader( new FileReader("./R3K-CBOE-List.csv") );
           String s;
           while ( (s = reader.readLine()) != null ) {
	       tickers.add(s.trim());
           }
           reader.close();
       } catch ( IOException ioe ) { ioe.printStackTrace(); }

       for ( String tickerSymbol : tickers ) {
        System.out.println( tickerSymbol );
        String companyName = "";
        String phoneNumber = "";
        String faxNumber = "";      
        String addyText = "";
        String website = "";
        String indexMembership = "";
        String sector = "";
        String industry = "";
        String fullTimeEmployees = "";

        try {
          URL url = new URL( "http://finance.yahoo.com/q/pr?s="+tickerSymbol );
          BufferedReader respReader = new BufferedReader(new InputStreamReader(url.openStream()));
          String responseText = "";
          String s;
          while( (s = respReader.readLine()) != null ) {
	      responseText += s;
          }
          
          //System.out.println( responseText );
          int startIdx = responseText.indexOf("Get Profile for:");
          int endIdx = responseText.indexOf("Business Summary");
          if ( startIdx < endIdx && startIdx > 0 ) {
	      String blockText = responseText.substring( startIdx, endIdx );
              
              int titleIdx = blockText.indexOf("yfnc_modtitlew1");
              int mapIdx = blockText.indexOf("Map");
              Parser htmlParser = new Parser();
              int phoneIdx = blockText.indexOf("Phone:");
              try {
		 htmlParser.setInputHTML( blockText.substring(phoneIdx,phoneIdx+30) );
                 NodeList nodes = htmlParser.parse(null);
                 String[] array = nodes.elementAt(0).toPlainTextString().split(":");
                 phoneNumber = array[1].trim();
              } catch ( ParserException pe ) { pe.printStackTrace(); }
              catch ( ArrayIndexOutOfBoundsException ae ) { ae.printStackTrace(); }
              try {
                  htmlParser.setInputHTML( blockText.substring(titleIdx+20,mapIdx) );
                  NodeList nodes = htmlParser.parse(null);
                  companyName = nodes.elementAt(0).toPlainTextString().trim();
                  addyText += nodes.elementAt(3).toPlainTextString().trim();
                  addyText += " " + nodes.elementAt(5).toPlainTextString().trim();
                  addyText += " " + nodes.elementAt(7).toPlainTextString().trim();
              } catch ( ParserException pe ) { pe.printStackTrace(); }
              catch ( StringIndexOutOfBoundsException e ) {}
              int faxIdx = blockText.indexOf("Fax:");
              try {
		 htmlParser.setInputHTML( blockText.substring(faxIdx,faxIdx+30) );
                 NodeList nodes = htmlParser.parse(null);
                 String[] array = nodes.elementAt(0).toPlainTextString().split(":");
                 faxNumber = array[1].trim();
              } catch ( ParserException pe ) { pe.printStackTrace(); }
              catch ( ArrayIndexOutOfBoundsException ae ) { ae.printStackTrace(); }
              int webIdx = blockText.indexOf("Website:");
              try {
		 htmlParser.setInputHTML( blockText.substring(webIdx,webIdx+50) );
                 NodeList nodes = htmlParser.parse(null);
                 String[] array = nodes.elementAt(0).toPlainTextString().split(":");
                 website = array[1].trim();
              } catch ( ParserException pe ) { pe.printStackTrace(); }
              catch ( ArrayIndexOutOfBoundsException ae ) { ae.printStackTrace(); }
              int tableIdx = blockText.indexOf("<table width=\"100%\" class=\"yfnc_mod_table_title1\"");
	      String tableText = blockText.substring(tableIdx,blockText.length()-1);
              try {
		 htmlParser.setInputHTML( tableText );
                 NodeList nodes = htmlParser.parse(null);
                 String sectorInfo = nodes.elementAt(1).toPlainTextString().trim();
                 String[] pieces = sectorInfo.split(":");
                 indexMembership = pieces[1].replace("Sector","");
                 sector = pieces[2].replace("Industry","");
                 industry = pieces[3].replace("Full Time Employees","");
                 industry = industry.replace("&amp;","&");
                 fullTimeEmployees = pieces[4].trim();
              }  catch ( ParserException pe ) { pe.printStackTrace(); }
              catch ( ArrayIndexOutOfBoundsException ae ) { ae.printStackTrace(); }
          }          
        } catch ( IOException ioe ) { ioe.printStackTrace(); }
        catch ( StringIndexOutOfBoundsException e ) { e.printStackTrace(); }
        catch ( NullPointerException npe ) { npe.printStackTrace(); }         

         try {
           writer.write( "\"" + tickerSymbol + "\",\"" + companyName + "\",\"" + phoneNumber + "\",\"" + faxNumber + "\",\"" + addyText + "\",\"" + website + "\",\"" + indexMembership + "\",\"" + sector + "\",\"" + industry + "\",\"" + fullTimeEmployees + "\"\n" );
         } catch ( IOException ioe ) { ioe.printStackTrace(); }
        
       }
       try {
	   writer.close();
       } catch ( IOException ioe ) {  ioe.printStackTrace(); }
   }
}