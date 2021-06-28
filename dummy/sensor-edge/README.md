Die Komponente sensor-edge simuliert einen Edge-Node, der Daten von einem Edge-Device empfängt, vorverarbeitet und in die Cloud sendet. Außerdem wird beim Über-/Unterschreiten kritischer Messwerte die Wetterlage banchbarter Plantagen (andere sensor-edge-nodes) abgefragt und die Bewässerung ggf. angepasst. <br></br>
Die Komponente Arbeitet mit Daten im int-Format und führt ggf. Casts durch. 
Die Namen der Sensoren, Typen der Sensoren und die Worker-ID werden automatisch generiert. <br></br>

Es werden vier Sensor-Typen unterstützt: TEMPERATURE, HUMIDITY, SOILMOISTURE, PRECIPITATION.
 
<h2> Konfiguration: </h2>
<p>  Programmagumente können in der application.properties, manche über HTTP-Post oder als Java-Argumente übergeben werden. </p>
   
  <h3><b>Cloud-Argumente</b></h3>
  <p><b>targetNode.host</b> - Host des zugehörigen Cloud-Nodes</p>
  <p><b>targetNode.port</b> - Port des zugehörigen Cloud-Nodes</p> 
  <p><b>targetNode.url</b>  - URL des zugehörigen Cloud-Nodes (default / )</p>
  <p><b>intervall</b> - Aggregationsintervall (Alle n Einträge wird ein Median berechnet und in die Cloud gesendet)</p>
  <h3><b>Edge2-Edge-Bewässerung-Argumente</b></h3>
  <p><b>edge.endpoints</b> - Eine Liste von Endpoints anderer Edge Worker Nodes für Edge-to-Edge-Kommunikation. Der Parameter wird als eine Liste von Strings (CSV,Delimiter , ) übergeben. Die Endpoint Strings müssen die Protokoll-Angabe beinhalten. (http:// beginnen)</p>
   <p><b>irrigation.host </b> - Host der Bewässerungsanlage. Url "/water" ist hard gecoded. </p>
  <p><b>irrigation.port</b> - Port der Bewässerungsanlage. Url "/water" ist hard gecoded.</p>
    <h3><b>Kritische Trigger-Werte für Bewässerung (default 50)</b></h3>
  <p><b>criticalTemp</b> - Kritische Temperatur, die beim Überschreiten eine Anpassung der Bewässerung auslöst.</p>
     <p><b>criticalHumidity</b> - Kritische Luftfeuchtigkeit, die beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
  <p><b>criticalsoilMoisture</b> - Kritische Bodenfeuchtigkeit, die beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
  <p><b>criticalPrecipitation</b> - Kritischer Niederschlag, der beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
    <h3><b>Benchmark Endpoint</b></h3>

   <p><b>benchmarkEndPointHost</b> - Kritische Luftfeuchtigkeit, die beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
  <p><b>benchmarkEndpointPort</b> - Kritische Bodenfeuchtigkeit, die beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
  <p><b>benchmarkEndpointURL</b> - Kritischer Niederschlag, der beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
    <p><b>logStorageLimit</b> - Kritischer Niederschlag, der beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
 
  <h3><b>HTTP-Pfade</b></h3>
  
  <b>GET /sensorData </b> Returns: json mit allen zwischengespeicherten SensorData-Objekten <br></br>
  <b> POST /sensorData </b> Parameter: json - "measurement":<double_value> <br></br>
  
  
 <b> GET /config </b> Json mit zur Laufzeit anpassbaren Konfigurationen zur Skalierung beim Benchmarking. Alle anpassbaren Felder s.u. 
  ```JSON
  {
    "criticalTemp": 1111,
    "criticalsoilMoisture": 1111,
    "criticalHumidity": 1111,
    "intervall": "2222",
    "criticalPrecipitation": 1111
}
  ```
  <br></br>
  <b> POST /config </b> Parameter können über ENV-Variablen angepasst werden oder über HTTP-Requests.<br></br>
  Parameter: JSON mit Konfiguration, die zur Laufzeit angepasst werden soll. Ein oder Mehrere Felder möglich (s.u.)
  ```JSON
  {
    "intervall": "2222"
  }
  ```
  <br></br>
  <h3><b>JSON-Format der Benchmarkdaten</b></h3>
    <ul>
 <li>timestamp - Startzeitpunkt</li>
 <li>timeDelta - Zeitdifferenz</li>
 <li>workerID - Id des Workers, der die Daten erzeugt hat</li>
 <li>3: Computation</li>
 </ul>
   <h4>SensorTypen: </h4>
  <br></br>
  <ul>
 <li>0: Edge2Edge - Bewässerung</li>
 <li>1: Edge2Edge - Worker-Abfrage</li>
 <li>2: Edge2Cloud - Übertragung</li>
 <li>3: Computation</li>
 </ul>
  
 ```JSON
  {
    "type": 0-3,
    "timestamp": 123456,
    "timeDelta": 123,
    "workerID": "Worker-X123",
 }
  ```
  
  
