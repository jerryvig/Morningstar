import geo.google.GeoAddressStandardizer;
import geo.google.datamodel.GeoUsAddress;
import geo.google.GeoException;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class GoogleGeocode {
 static BufferedWriter writer;

 public static void main( String[] args ) {
   try {
     CSVReader reader = new CSVReader( new FileReader("/root/Desktop/Morningstar/YahooProfiles.csv" ) );
     String[] nextLine;
     writer = new BufferedWriter( new FileWriter("/root/Desktop/Morningstar/YahooProfileGeocodeAddresses.csv") );
     writer.write( "\"Ticker Symbol\",\"Street Address\",\"City\",\"State\",\"Zip Code\"\n" );

     while ( (nextLine = reader.readNext()) != null ) {
        String ticker = nextLine[0].trim();
	String addyString = nextLine[4].trim();
        getUsAddress( ticker, addyString );
        Thread.sleep( 250 );      
     }
   }catch ( IOException ioe ) { ioe.printStackTrace(); }
   catch ( InterruptedException ie ) { ie.printStackTrace(); }
 }

 public static void getUsAddress( String _ticker, String _input ) {
  try {
     GeoAddressStandardizer st = new GeoAddressStandardizer( "ABQIAAAAI1oIsi6Dv7MlmxUm1lRR_xTmarcuMJj81CoryY3grjEx5dFcyxQoeQTublWNe-B1iLVnHNrRuJD6_w" );
     GeoUsAddress geoAddy = st.standardizeToGeoUsAddress( _input );
     String streetAddress = geoAddy.getAddressLine1() + " " + geoAddy.getAddressLine2();
     String state = geoAddy.getState();
     String city = geoAddy.getCity();
     String postalCode = geoAddy.getPostalCode();
    
     try {   
       writer.write( "\"" + _ticker + "\",\"" + streetAddress.trim() + "\",\"" + city.trim() + "\",\""  + state.trim() + "\",\"" + postalCode.trim() + "\"\n" );
       System.out.print("\"" + _ticker + "\",\"" + streetAddress.trim() + "\",\"" + city.trim() + "\",\""  + state.trim() + "\",\"" + postalCode.trim() + "\"\n" );
     } catch ( IOException ge ) { ge.printStackTrace(); }   
 
  } catch ( GeoException ge ) { ge.printStackTrace(); }   
 }
}