import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;

public class JobsSalary {
 public static void main( String[] args ) {
   try {
     CSVReader reader = new CSVReader( new FileReader("/root/Desktop/Morningstar/morningstarFact.csv" ) );
     BufferedWriter writer = new BufferedWriter( new FileWriter("/root/Desktop/Morningstar/JobsSalaryData.csv") );
     writer.write( "\"Ticker Symbol\",\"Company Name\",\"City\",\"State\",\"Salary Average\",\"Salary Range\"\n" );

     String[] nextLine;
     HtmlUnitDriver driver = new HtmlUnitDriver( BrowserVersion.FIREFOX_3_6 );
     driver.setJavascriptEnabled( false );
  
     int rowCount = 0;
     while ( (nextLine = reader.readNext()) != null ) {
      if ( rowCount > 0 ) {      
       String ticker = nextLine[0].trim();
       String inputCompanyName = nextLine[15].trim();
       String companyName = inputCompanyName;
       String inputCity = nextLine[25].trim();
       String city = inputCity;
       String state = nextLine[26].trim();
       String sAverage = "";
       String sRange = "";      
 
       companyName = companyName.toLowerCase().trim();
       companyName = companyName.replace(".com","");
       companyName = companyName.replace(".","").trim();
       companyName = companyName.replace("!","").trim();
       companyName = companyName.replaceAll("corporation","").trim();
       companyName = companyName.replaceAll("incorporated","").trim();
       companyName = companyName.replaceAll("holdings","").trim();
       companyName = companyName.replaceAll(",","").trim();
       companyName = companyName.replaceAll("ltd","").trim();
       companyName = companyName.replaceAll(" inc","").trim();
       companyName = companyName.replaceAll("corp","").trim();
       companyName = companyName.replaceAll("lp","").trim();
       companyName = companyName.replaceAll(" plc","").trim();
       companyName = companyName.replaceAll(" nv","").trim();
       companyName = companyName.replaceAll(" ","+");
       city = city.replaceAll(" ","+").trim();

       String baseUrl = "http://www.jobs-salary.com/salaries.php?q=";
       String fetchUrl = baseUrl + companyName + "&lc=" + city + "+" + state;        
       System.out.println( fetchUrl );

       driver.get( fetchUrl );     
       try {
         WebElement srangeDiv = driver.findElementByXPath( "//div[@class='srange']" );
         String srangeText = srangeDiv.getText();
         String[] pieces = srangeText.split(":");
         sAverage = pieces[1].trim().replace("Salary Range","");
         sRange = pieces[2].trim();
         sRange = sRange.replaceAll("\n"," ");
       } catch ( NoSuchElementException nsee ) { nsee.printStackTrace(); }
 
       try {
         writer.write( "\"" + ticker + "\",\"" + inputCompanyName + "\",\"" + inputCity + "\",\"" + state + "\",\"" + sAverage + "\",\"" + sRange + "\"\n" );
         System.out.print( "\"" + ticker + "\",\"" + inputCompanyName + "\",\"" + inputCity + "\",\"" + state + "\",\"" + sAverage + "\",\"" + sRange + "\"\n" );
       } catch ( IOException ioe ) { ioe.printStackTrace(); }
       try { Thread.sleep( 400 ); } catch ( InterruptedException ie ) { ie.printStackTrace(); }     
      }
      rowCount++;
     }
     reader.close();
     writer.close();
   }catch ( IOException ioe ) { ioe.printStackTrace(); }
 }
}