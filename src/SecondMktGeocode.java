import geo.google.GeoAddressStandardizer;
import geo.google.datamodel.GeoUsAddress;
import geo.google.GeoException;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

public class SecondMktGeocode {
 static CSVWriter writer;

 public static void main( String[] args ) {
   try {
     CSVReader reader = new CSVReader( new FileReader("/root/Desktop/Morningstar/SecondMktFirmInfo.csv" ) );
     String[] nextLine;
     writer = new CSVWriter( new FileWriter("/root/Desktop/Morningstar/SecondMktGeocodeAddresses.csv") );

     while ( (nextLine = reader.readNext()) != null ) {
        String companyName = nextLine[0].trim();
	String addyString = nextLine[1].trim();
        String secondMktUrl = nextLine[2].trim();
        String companyUrl = nextLine[3].trim();
        String lastFundingDate = nextLine[4].trim();
        String lastFundingAmount = nextLine[5].trim();

        getUsAddress( companyName, addyString );
        Thread.sleep( 250 );      
     }
   }catch ( IOException ioe ) { ioe.printStackTrace(); }
   catch ( InterruptedException ie ) { ie.printStackTrace(); }
 }

 public static void getUsAddress( String _companyName, String _input ) {
  try {
     GeoAddressStandardizer st = new GeoAddressStandardizer( "ABQIAAAAI1oIsi6Dv7MlmxUm1lRR_xTmarcuMJj81CoryY3grjEx5dFcyxQoeQTublWNe-B1iLVnHNrRuJD6_w" );

     List<GeoUsAddress> geoAddyList = null;
     try {
        geoAddyList = st.standardizeToGeoUsAddresses(  _input );
     } catch ( GeoException ge ) { ge.printStackTrace(); }   
     String city = geoAddyList.get(0).getCity();
     String state = geoAddyList.get(0).getState();

     String[] out = new String[3];
     out[0] = _companyName;
     out[1] = state;
     out[2] = city;

     try {   
	 writer.writeNext( out );
         System.out.print("\"" + _companyName + "\",\"" + "\",\"" + city.trim() + "\",\""  + state.trim() + "\"\n" );
     } catch ( Exception ge ) { ge.printStackTrace(); }    
  } catch ( Exception ge ) { ge.printStackTrace(); }   
 }
}