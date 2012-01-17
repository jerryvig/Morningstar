import java.util.ArrayList
import java.net.URL
import java.util.regex.Pattern
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebRequestSettings
import com.gargoylesoftware.htmlunit.HttpMethod
import com.gargoylesoftware.htmlunit.util.NameValuePair
import com.gargoylesoftware.htmlunit.util.Cookie
import com.gargoylesoftware.htmlunit.StringWebResponse
import com.gargoylesoftware.htmlunit.html.HTMLParser
import com.gargoylesoftware.htmlunit.TopLevelWindow
import org.json.JSONObject

def wc = new WebClient( BrowserVersion.FIREFOX_3_6 )
wc.setJavaScriptEnabled( false )
def page = wc.getPage( "https://www.secondmarket.com/cas/login" )
def inputList = page.getElementsByTagName("input")

def key = ""
inputList.each{
  if ( it.getAttribute("name").equals("lt") ) {
    key = it.getAttribute("value").trim()
  }
}

println "key = ${key}"
def requestSettings = new WebRequestSettings( new URL("https://www.secondmarket.com/cas/login"), HttpMethod.POST )
requestSettings.setRequestParameters(new ArrayList());
requestSettings.getRequestParameters().add(new NameValuePair("username","AGENTQ314@YAHOO.COM"))
requestSettings.getRequestParameters().add(new NameValuePair("password","dk87nup4841"))
requestSettings.getRequestParameters().add(new NameValuePair("_rememberMe","on"))
requestSettings.getRequestParameters().add(new NameValuePair("lt",key.trim()))
requestSettings.getRequestParameters().add(new NameValuePair("_eventId","submit"))

page = wc.getPage( requestSettings )
println page.asXml()

def cookie = new Cookie( "www.secondmarket.com", "AWSELB", "55C167F906B8F581532A6CCE18090860166AC065A91AC136FE64CEFDCA80C475708B4FCAB2527CA6A32059C90BD4DDC0BF8878DAC793E59C46CB9C32F7625E1C84F0D0276A; _jsuid=1517801581; unpoco=1; A387C61B-E253-4DDF-BF85-E9E6D73D0032_CCTInformation=2|%3Bexpires%3DWed%2C%2015%20Feb%202012%2011%3A02%3A14%20GMT||75bbbea6-b570-44f6-91f9-ad1538810181||61ff7ed7-1943-492d-bd23-318d7a7e38b6|||||1326711734124||||||||||||||||||; JSESSIONID=9ggbn3na9tb7o137y617te6c; __utma=163932477.255830135.1326792689.1326792689.1326792689.1; __utmb=163932477.16.10.1326792689; __utmc=163932477; __utmz=163932477.1326792689.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=163932477.|1=SMID=AU0000017030=1,3=RepType=INDIVIDUAL=1; _chartbeat2=0kc222gub6hogwfv.1326711661325" )
wc.getCookieManager().addCookie( cookie )
page = wc.getPage( "https://www.secondmarket.com/" )

def fh = new File("/root/Desktop/Morningstar/groovy/SecondMarketFirmList.csv");
fh.delete()

for ( i in 1..928 ) {
  def url =  "https://www.secondmarket.com/m/companies?page=${i}"
  page = wc.getPage( url )
  def jsonString = page.getWebResponse().getContentAsString()
  def jsonObj = new JSONObject( jsonString )
  def htmlFromJson = jsonObj.get("html")
  def htmlPageContent = "<html><body>"+htmlFromJson.getString(0)+"</body></html>"
  def swr = new StringWebResponse( htmlPageContent, new URL("http://www.secondmarket.com") )
  page = HTMLParser.parseHtml(swr, new TopLevelWindow("top", new WebClient()))
  
  def divList = page.getElementsByTagName("div") 
  divList.each {
    if ( it.getAttribute("class").equals("sm-card sm-span-16 sm-unhide last sm-mb") ) {
     def companyName = "";
     def companyUrl = "";
     def location = "";

      def anchorList = it.getElementsByTagName("a")
      anchorList.each { anchor ->
        if ( anchor.getAttribute("class").equals("sm-card-link") ) {
          companyName = anchor.asText().trim();
          companyUrl = "https://www.secondmarket.com" + anchor.getHrefAttribute().trim();
        }
      }
      def tdList = it.getElementsByTagName("td");
      tdList.each { td ->
        if ( td.getWidthAttribute().equals("300") ) {
          location = td.asText().trim();
        }
      }
      fh.append( "\"" + companyName + "\",\"" + location + "\",\"" + companyUrl + "\"\n" );
    }
  }
}
