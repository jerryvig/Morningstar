import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.BrowserVersion

def domainList = (new File("/root/Desktop/Morningstar/AlexaTop3000.txt")).readLines()
def outFile = new File("/root/Desktop/Morningstar/CompeteStats3000.csv")
outFile.delete()
def wc = new WebClient( BrowserVersion.FIREFOX_3_6 )

domainList.each {
  def domainName = it.trim()
  println domainName
  def url = "http://siteanalytics.compete.com/export_csv/${domainName}/"
  def page = wc.getPage( url )
  def pageLines = page.getContent().split("\n")

  def lineCount = 0
  pageLines.each { line ->
   if ( lineCount > 3 ) {
     outFile.append( "\"${domainName}\",${line}\n" )
   }
   lineCount++ 
  }
  sleep( 400 )
}
