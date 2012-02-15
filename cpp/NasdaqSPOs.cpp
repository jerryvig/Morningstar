#include <string>
#include <sstream>
#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <stdlib.h>

#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <htmlcxx/html/ParserDom.h>

using namespace std;
using namespace htmlcxx;

int main() {
  vector<string> tabStrings;
  tabStrings.push_back( "pricings" );
  tabStrings.push_back( "filings" );

  vector<string> monthStrings;
  for ( int i=1; i<=12; i++ ) {
    stringstream out;
    out << i;
    if ( i<10 ) { 
      monthStrings.push_back("2011-0"+out.str());
    }
    else {
      monthStrings.push_back("2011-"+out.str());
    }
  }
  monthStrings.push_back("2012-01");
  monthStrings.push_back("2012-02");

  fstream outFile( "/tmp/NasdaqSPOs.csv", fstream::out );

  for ( vector<string>::iterator iter=tabStrings.begin(); iter!=tabStrings.end(); iter++ ) {
      for ( vector<string>::iterator monthIter=monthStrings.begin(); monthIter!=monthStrings.end(); monthIter++ ) {
	cout << "Scraping " << *iter << " for month = " << *monthIter << endl;
        try {
          curlpp::Easy myRequest;
          myRequest.setOpt(curlpp::options::Url((std::string( "http://www.nasdaq.com/markets/spos/activity.aspx?tab="+*iter+"&month="+*monthIter))));
          ostringstream os;
          os << myRequest;
          string content = os.str();
          
          HTML::ParserDom parser;

          if ( *iter == "pricings" ) {
            string type = "PRICING";

	    int startIdx = content.find("<div class=\"genTable\">");          
            int endIdx = content.find("<!-- end tabpane");
                                  
            if ( startIdx > 0 && endIdx > startIdx ) {
              string htmlContent = content.substr(startIdx,(endIdx-startIdx));               
              tree<HTML::Node> dom = parser.parseTree( htmlContent );

	      for ( tree<HTML::Node>::iterator treeIter=dom.begin(); treeIter!=dom.end(); treeIter++ ) {
		if ( treeIter->tagName() == "tr" ) {
		  string trHtml = treeIter->content( htmlContent );
                  tree<HTML::Node> trDom = parser.parseTree( trHtml );
                  int tdCount = 0;
                  string name = "";
                  string nasdaqUrl = "";
                  string ticker = "";
                  string market = "";
                  string price = "";
                  string shares = "";
                  string offerAmount = "";
                  string datePriced = "";

                  for ( tree<HTML::Node>::iterator trIter=trDom.begin(); trIter!=trDom.end(); trIter++ ) {
                    if ( trIter->tagName() == "td" ) {
                      if ( tdCount == 0 ) {
			string tdHtml = trIter->content( trHtml );
                        int startIndex = tdHtml.find("\">");
                        int endIndex = tdHtml.find("</a>");
                        name = tdHtml.substr(startIndex+2,(endIndex-startIndex-2));
                        startIndex = tdHtml.find("href=\"");
                        endIndex = tdHtml.find("\">");
                        nasdaqUrl = tdHtml.substr(startIndex+6,(endIndex-startIndex-6));
                      }
                      else if ( tdCount == 1 ) {
                        string tdHtml = trIter->content( trHtml );
                        int startIndex = tdHtml.find("\">");
                        int endIndex = tdHtml.find("</a>");
                        ticker = tdHtml.substr(startIndex+2,(endIndex-startIndex-2));
                      }
                      else if ( tdCount == 2 ) {
                        string tdHtml = trIter->content( trHtml );
                        int startIndex = tdHtml.find("\">");
                        int endIndex = tdHtml.find("</a>");
                        market = tdHtml.substr(startIndex+2,(endIndex-startIndex-2));
                      }
                      else if ( tdCount == 3 ) {
                        string tdHtml = trIter->content( trHtml );
                        price = tdHtml;
                      }
                      else if ( tdCount == 4 ) {
                        string tdHtml = trIter->content( trHtml );
                        shares = tdHtml;
                      }
                      else if ( tdCount == 5 ) {
                        string tdHtml = trIter->content( trHtml );
                        offerAmount = tdHtml;
                      }
                      else if ( tdCount == 6 ) {
                        string tdHtml = trIter->content( trHtml );
                        datePriced = tdHtml;
                      }
                      tdCount++;
                    }
                  }
                  if ( name != "" ) {
                     outFile << "\"" + type + "\",\"" + name + "\",\"" + ticker + "\",\"" + market + "\",\"" + price + "\",\"" + shares + "\",\"" + offerAmount + "\",\"" + datePriced + "\",\"" + nasdaqUrl + "\"" << endl;
                  }                   
                }            
              }
            } 
          }
          else if ( *iter == "filings" ) {
	    string type = "FILING";
            int startIdx = content.find("<div class=\"genTable\">");        
            int endIdx = content.find("<!-- end tabpane");
            
            if ( startIdx > 0 && endIdx > startIdx ) {
              string htmlContent = content.substr(startIdx,(endIdx-startIdx));               
              tree<HTML::Node> dom = parser.parseTree( htmlContent );
             
              for ( tree<HTML::Node>::iterator treeIter=dom.begin(); treeIter!=dom.end(); treeIter++ ) {
		if ( treeIter->tagName() == "tr" ) {
		  string trHtml = treeIter->content( htmlContent );
                  tree<HTML::Node> trDom = parser.parseTree( trHtml );
                  int tdCount = 0;
                  string name = "";
                  string nasdaqUrl = "";
                  string ticker = "";
                  string offerAmount = "";
                  string dateFiled = "";
                  string market = "";
                  string price = "";
                  string shares = "";
          
                  for ( tree<HTML::Node>::iterator trIter=trDom.begin(); trIter!=trDom.end(); trIter++ ) {
                    if ( trIter->tagName() == "td" ) {
                      if ( tdCount == 0 ) {
			string tdHtml = trIter->content( trHtml );
                        int startIndex = tdHtml.find("\">");
                        int endIndex = tdHtml.find("</a>");
                        name = tdHtml.substr(startIndex+2,(endIndex-startIndex-2));
                        startIndex = tdHtml.find("href=\"");
                        endIndex = tdHtml.find("\">");
                        nasdaqUrl = tdHtml.substr(startIndex+6,(endIndex-startIndex-6));
                      }
                      else if ( tdCount == 1 ) {
                        string tdHtml = trIter->content( trHtml );
                        ticker = tdHtml;
                        try {
                          if ( ticker.find("</a>") != string::npos ) {
                            int startIndex = ticker.find("\">");
                            int endIndex = ticker.find("</a>");
                            ticker = ticker.substr(startIndex+2,(endIndex-startIndex-2));
                          }
                        } catch ( std::out_of_range &e ) {}
                      }
                      else if ( tdCount == 2 ) {
                        string tdHtml = trIter->content( trHtml );
                        offerAmount = tdHtml;
                      }
                      else if ( tdCount == 3 ) {
                        string tdHtml = trIter->content( trHtml );
                        dateFiled = tdHtml;
                      }
                      tdCount++;
                    }
                  }
                  if ( name != "" ) {
                     outFile << "\"" + type + "\",\"" + name + "\",\"" + ticker + "\",\"" + market + "\",\"" + price + "\",\"" + shares + "\",\"" + offerAmount + "\",\"" + dateFiled + "\",\"" + nasdaqUrl + "\"" << endl;
                  }                   
                }            
              }
            }             
             
          }
        } catch( curlpp::RuntimeError &e ) { std::cout << e.what() << std::endl; }
        catch( curlpp::LogicError &e ) { std::cout << e.what() << std::endl; }
        catch( std::out_of_range &e ) { cout << e.what() << endl; }
        usleep( 125000 );        
      }
      } 
  outFile.close();
  return 0;
}
