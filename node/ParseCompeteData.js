var fs = require('fs');

fs.unlink('./CleanCompeteData.csv',function(){
 fs.readFile('./CompeteData.csv','utf8',function(err,data){
   if ( err ) throw err;

   var lines = data.split('\n');
   for ( var i=0; i<lines.length; i++ ) {
      var cols = lines[i].split('"');
      for ( var j=1; j<cols.length; j++ ) {
//          console.log( cols[j] );
          while ( cols[j].indexOf(',') > -1 ) {
             cols[j] = cols[j].replace(',','');
          }
          if ( cols[j].indexOf('/') > -1 ) {
             var dateCols = cols[j].split('/');
             cols[j] = dateCols[1] + '-' + dateCols[0];  
          }
          fs.appendFileSync('./CleanCompeteData.csv',cols[j]+',','utf8');
      }
      fs.appendFileSync('./CleanCompeteData.csv','\n','utf8');
   }
 });
});
