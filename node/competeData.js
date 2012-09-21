window.onload = function(){
   var jsonReq = new XMLHttpRequest();
   jsonReq.open('GET','./compete_sharpe_data',true);
   jsonReq.onreadystatechange = function(resp){
     if ( jsonReq.readyState == 4 ) {
       var data = JSON.parse(jsonReq.responseText);

       var firstRow = document.createElement('tr');
       var domainTd = document.createElement('td');
       domainTd.align = 'center';
       domainTd.appendChild(document.createTextNode('Domain'));
       firstRow.appendChild( domainTd );
       var sharpeTd = document.createElement('td');
       sharpeTd.align = 'center';
       sharpeTd.appendChild( document.createTextNode('Sharpe Ratio of Growth') );
       firstRow.appendChild( sharpeTd );
       var maxTd = document.createElement('td');
       maxTd.align = 'center';
       maxTd.appendChild( document.createTextNode('Unique Visitors') );
       firstRow.appendChild( maxTd );
       var cumTd = document.createElement('td');
       cumTd.align = 'center';
       cumTd.appendChild( document.createTextNode('Cumulative Growth') );
       firstRow.appendChild( cumTd );
  
       firstRow.setAttribute('style','background-color:#EEEEEE;');
       document.getElementById("dataTable").appendChild( firstRow );
       document.getElementById("headerText").appendChild(document.createTextNode('Top 3000 Alexa Domains Sorted by Sharpe of Unique Visitor Growth'));
       
       for ( var i=0; i<data.rows.length; i++ ) {
           var row = data.rows[i];
           var myRow = document.createElement('tr');
           var domainTd = document.createElement('td');
           var domainA = document.createElement('a');
           domainA.href = 'http://' + row.domain;
           domainA.target = '_blank';
           domainA.appendChild(document.createTextNode(row.domain));
           domainTd.appendChild( domainA );
           myRow.appendChild( domainTd );

           var sharpeGrowthTd = document.createElement('td');
           sharpeGrowthTd.appendChild(document.createTextNode(row.sharpe_growth));
           myRow.appendChild( sharpeGrowthTd );

           var maxUniqueVisitorsTd = document.createElement('td');
           maxUniqueVisitorsTd.setAttribute('align','right');
           maxUniqueVisitorsTd.appendChild(document.createTextNode(row.max_unique_visitors));
           myRow.appendChild( maxUniqueVisitorsTd );

           var cumGrowthTd = document.createElement('td');
           cumGrowthTd.setAttribute('align','right');
           cumGrowthTd.appendChild(document.createTextNode(row.cum_growth));
           myRow.appendChild( cumGrowthTd );

           if ( i % 2 == 0 ) {
             myRow.setAttribute('style','background-color:#C6DEFF;');
           } else {
             myRow.setAttribute('style','background-color:#EEEEEE;');
           }
           
           document.getElementById('dataTable').appendChild( myRow );
       }
 
       var trList = document.getElementsByTagName('tr');
       for ( var i=0; i<trList.length; i++ ) {
          trList[i].setAttribute('class','row');
       }
     }
   }

   jsonReq.send(null);
}