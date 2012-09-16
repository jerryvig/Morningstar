var fs = require('fs');

fs.unlink('./CleanCompeteData2.csv',function(){
fs.unlink('./CleanCompeteData.csv',function(){
 fs.readFile('./CompeteData.csv','utf8',function(err,data){
   if ( err ) throw err;

   var lines = data.split('\n');
   for ( var i=0; i<lines.length; i++ ) {
      var cols = lines[i].split('"');
      for ( var j=1; j<cols.length; j+=2 ) {
          while ( cols[j].indexOf(',') > -1 ) {
             cols[j] = cols[j].replace(',','');
          }
      }
      for ( var j=1; j<cols.length; j++ ) {
         fs.appendFileSync('./CleanCompeteData.csv',cols[j],'utf8'); 
      }
      fs.appendFileSync('./CleanCompeteData.csv','\n','utf8');
   }

   fs.readFile('./CleanCompeteData.csv','utf8',function(err,data){
       var lines = data.split('\n');
       for ( var i=0; i<lines.length; i++ ) {
          var cols = lines[i].split(',');
          if ( cols.length > 1 ) {
           var dateCols = cols[1].split('/');
           cols[1] = dateCols[1] + '-' + dateCols[0];
          } 
          for ( var j=0; j<cols.length; j++ ) {
              fs.appendFileSync('./CleanCompeteData2.csv',cols[j]+',','utf8');
          }
          fs.appendFileSync('./CleanCompeteData2.csv','\n','utf8');
       }
   });  
 });
});
});