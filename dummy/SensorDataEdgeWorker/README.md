Die Komponente SensorDataEdgeWorker simuliert einen Edge-Node, der Daten von einem Edge-Device empfängt, vorverarbeitet und in die Cloud sendet. Außerdem fragt der Worker Node alle 1000ms den Status aller anderer Edge-Nodes. 
 
<h2> Konfiguration: </h2>
  Programmagumente können in der application.properties oder als Java-Argumente übergeben werden.
  Alle Argumente sind obligatrisch.
  
  <h3>Programmargumente</h3>
  <p><b>targetNode.host</b> - Host des zugehörigen Cloud-Nodes</p>
  <p><b>targetNode.port</b> - Port des zugehörigen Cloud-Nodes</p> 
  <p><b>targetNode.url</b>  - URL des zugehörigen Cloud-Nodes (default / )</p>
  <p><b>sensorEdgeWorker.intervall</b> - Aggregationsintervall (Alle n Einträge wird ein Median berechnet und in die Cloud gesendet)</p>
  <p><b>kubernetes.api.endpoints</b> - Eine Liste von Endpoints anderer Edge Worker Nodes für Edge-to-Edge-Kommunikation. Der Parameter wird als eine Liste von Strings (CSV,Delimiter , ) übergeben. Die Endpoint Strings müssen die Protokoll-Angabe beinhalten. (http:// beginnen)</p>
  
  <h3>REST-API</h3>
  
  GET /alert Returns: Integer -1 <br>
  POST /sensorData Parameter: json - "measurement":<double_value>
  
