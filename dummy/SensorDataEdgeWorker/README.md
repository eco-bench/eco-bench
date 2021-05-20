Die Komponente SensorDataEdgeWorker simuliert einen Edge-Node, der Daten von einem Edge-Device empfängt, vorverarbeitet und in die Cloud sendet. Außerdem fragt der Worker Node alle 1000ms den Status aller anderer Edge-Nodes. 
 
<h2> Konfiguration: </h2>
  Programmagumente können in der application.properties oder als Java-Argumente übergeben werden.
  Alle Argumente sind obligatrisch.
  
  <h3>Programmargumente</h3>
  targetNode.host - Host des zugehörigen Cloud-Nodes
  targetNode.port - Port des zugehörigen Cloud-Nodes 
  targetNode.url  - URL des zugehörigen Cloud-Nodes (default / )
  sensorEdgeWorker.intervall - Aggregationsintervall (Alle n Einträge wird ein Median berechnet und in die Cloud gesendet)
  kubernetes.api.endpoints - Eine Liste von Endpoints anderer Edge Worker Nodes für Edge-to-Edge-Kommunikation. Der Parameter wird als eine Liste von Strings (CSV,Delimiter , ) übergeben.
  Die Endpoint Strings müssen die Protokoll-Angabe beinhalten. (http:// beginnen)
  
