import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ParseSecondMktFirmInfo {
 static CSVWriter writer;

 public static void main( String[] args ) {
   try {
     CSVReader reader = new CSVReader( new FileReader("/root/Desktop/Morningstar/SecondMktFirmInfo.csv" ) );
     String[] nextLine;
     writer = new CSVWriter( new FileWriter("/root/Desktop/Morningstar/SecondMktFirmInfoParsed.csv") );

     SimpleDateFormat formatter = new SimpleDateFormat( "MMM yyyy", java.util.Locale.US );
     SimpleDateFormat outFmt = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US );

     while ( (nextLine = reader.readNext()) != null ) {
       try {
         String companyName = nextLine[0].trim();
	 String addyString = nextLine[1].trim();
         String secondMktUrl = nextLine[2].trim();
         String companyUrl = nextLine[3].trim();
         String lastFundingDate = nextLine[4].trim();
         String lastFundingAmount = nextLine[5].trim();
         lastFundingAmount = lastFundingAmount.replace("$","");

         String[] addressArray = addyString.split(",");
         String city = addressArray[0].trim();
         String state = addressArray[1].trim();
 
         Date lastFDate = formatter.parse( lastFundingDate );
         try {
          if ( lastFundingAmount.contains("EUR") || lastFundingAmount.contains("GBP") || lastFundingAmount.contains("AUD") || lastFundingAmount.contains("CAD") || lastFundingAmount.contains("CHF") || lastFundingAmount.contains("JPY") || lastFundingAmount.contains("MXN") || lastFundingAmount.contains("BRL") || lastFundingAmount.contains("SEK") || lastFundingAmount.contains("NOK") ) {
	    lastFundingAmount = "0.0";
          }
          else if ( lastFundingAmount.endsWith("M") ) {
	    lastFundingAmount = lastFundingAmount.replace("M","");
	    lastFundingAmount = Double.toString( Double.parseDouble( lastFundingAmount )*1000000.0 );
          }
          else if ( lastFundingAmount.endsWith("B") ) {
            lastFundingAmount = lastFundingAmount.replace("B","");
	    lastFundingAmount = Double.toString( Double.parseDouble( lastFundingAmount )*1000000000.0 );
          }
          else if ( lastFundingAmount.endsWith("K") ) {
            lastFundingAmount = lastFundingAmount.replace("K","");
	    lastFundingAmount = Double.toString( Double.parseDouble( lastFundingAmount )*1000.0 );
          }
          else if ( lastFundingAmount.contains("Unknown") ) {
	    lastFundingAmount = "0.0";
          }
          else { 
	    lastFundingAmount = "0.0";
          }
         } catch ( NumberFormatException nfe ) { nfe.printStackTrace(); }

         String[] outArray = new String[7];
         outArray[0] = companyName;
         outArray[1] = city;
         outArray[2] = state;
         outArray[3] = secondMktUrl;
         outArray[4] = companyUrl;
         outArray[5] = outFmt.format(lastFDate);
         outArray[6] = lastFundingAmount;
         writer.writeNext( outArray );

         System.out.println( city + " " + state + outFmt.format(lastFDate) + " " + lastFundingAmount );
      } catch ( ArrayIndexOutOfBoundsException aie ) { aie.printStackTrace(); }
      catch ( ParseException pe ) { pe.printStackTrace(); }
     }

     reader.close();
     writer.close();
   }catch ( IOException ioe ) { ioe.printStackTrace(); }
 }
}