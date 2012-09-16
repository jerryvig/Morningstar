var fileSystem = require('fs');
var exec = require('child_process').exec;
var DELAY_TIME = 350;

fileSystem.unlink('./CompeteData.csv',function(){
  fileSystem.readFile('./AlexaTop3000.csv','utf8',function(err,data){
    if ( err ) throw err;
    var lines = data.split('\n');
    processNextLine( lines, 0 ); 
  });
});

function processNextLine( lines, idx ) {
   var line = lines[idx];
   var cols = line.split(',');
   var url = 'http://siteanalytics.compete.com/'+cols[1];
   console.log( url );  
   
   exec( '/usr/bin/wget -O index.html '+url, function(err, stdout, stderr) {
       fileSystem.readFile('./index.html','utf8',function(err,data){
          if ( err ) throw err;
 
           
           var dataLines = data.split('\n');
           var ajax_keys = '';
           for ( var i=0; i<dataLines.length; i++ ) {
             if ( dataLines[i].indexOf("ajax_keys") > 0 ) {
                ajax_keys = dataLines[i];
		break;
             }
           }

           var keys = ajax_keys.split('"');
           var csvUrl = 'http://siteanalytics.compete.com/export_csv/'+cols[1]+'/'+keys[3];
           exec('/usr/bin/wget -O index.csv '+csvUrl, function(err,stdout,stderr) {
               fileSystem.readFile('./index.csv','utf8',function(err,data){
                   if ( err ) throw err;
                   
                   var dataToAppend = '';
                   var csvLines = data.split('\n');
                   for ( var i=4; i<csvLines.length; i++ ) {
                      var csvCols = csvLines[i].split(','); 
                      if ( csvCols.length > 1 ) {
                         var dataToWrite = '"' + cols[1] + '",' + csvLines[i];
                         dataToAppend += dataToWrite + '\n';
                      }  
                   }

                   fileSystem.appendFile('./CompeteData.csv',dataToAppend,'utf8',function(){     
                      idx++;
                      if ( idx < lines.length ) 
                        setTimeout( function(){processNextLine(lines,idx);}, DELAY_TIME );
                   });
	       });
           });
       });   
   });
}