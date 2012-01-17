import java.io.FileReader;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

CSVReader csvReader = new CSVReader(new FileReader("./SecondMarketFirmList.csv"));
CSVWriter csvWriter = new CSVWriter( new FileWriter("./SecondMktFirmInfo.csv") );

WebClient wc = new WebClient( BrowserVersion.FIREFOX_3_6 ); 
wc.setJavaScriptEnabled( false );

String[] nextLine;
while ((nextLine = csvReader.readNext())!=null) {
  String companyName = nextLine[0].trim();
  String location = nextLine[1].trim();
  String url = nextLine[2].trim();
  HtmlPage page = wc.getPage( url );
  def divList = page.getElementsByTagName("div");
 
  String homepageUrl = "";
  String lastFundingDate = "";
  String lastFundingAmount = "";

  divList.each { div ->
    if ( div.getAttribute("class").equals("span-12 last") ) {
      def anchorList = div.getElementsByTagName("a");
      anchorList.each { a ->
       if ( a.getAttribute("target").equals("_blank") ) {
         homePageUrl = a.asText().trim(); 
       }
      }
    }
    def goodDivCount = 0;
    if ( div.getAttribute("class").equals("span-8 append-1 sm-table-style sm-data") ) {
      def trList = div.getElementsByTagName("tr");
      trList.each { tr ->
        if ( tr.getAttribute("class").equals("sm-more-item") ) {
          def spanList = tr.getElementsByTagName("span");
          spanList.each { span ->
            if ( span.getAttribute("class").equals("small") ) {
             if ( goodDivCount == 0 ) {
                lastFundingDate = span.asText().trim();
             }
            }
          }
          def tdList = tr.getElementsByTagName("td");
          tdList.each { td ->
            if ( td.getAttribute("class").equals("sm-r sm-b") ) {
             if ( goodDivCount == 0 ) {
               lastFundingAmount = td.asText().trim();
             }
             goodDivCount++; 
            }
          }
        }
      }
    }
  }
 
  String[] entries = new String[6];
  entries[0] = companyName;
  entries[1] = location;
  entries[2] = url;
  entries[3] = homePageUrl;
  entries[4] = lastFundingDate;
  entries[5] = lastFundingAmount;
  csvWriter.writeNext( entries );
 
  println "${companyName},\"${location}\",${homePageUrl},${lastFundingDate},${lastFundingAmount}"
}

csvWriter.close();
csvReader.close();
