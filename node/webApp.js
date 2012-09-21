var express = require('express');
var app = express();
var sqlite3 = require('sqlite3').verbose();
var fs = require('fs');
var PORT = 80;

app.get('/',function(req, res) {
   res.set('Content-Type','text/html');
   fs.readFile('./competeData.html',function(err,data){
       res.send( data );
   });  
});

app.get('/compete_sharpe_data',function(req, res) {
   var db = new sqlite3.Database('./compete_data.sqlite');
   res.set('Content-Type', 'application/json');
   var jsonResp = new Object();

   db.all('SELECT * FROM sharpe_growth ORDER BY sharpe_growth DESC LIMIT 500',function(err,rows){
       if ( err ) throw err;
       jsonResp.rows = rows;
       res.json( jsonResp );
       db.close();
   });
});

app.listen( PORT );
console.log('app listening on port '+PORT);
