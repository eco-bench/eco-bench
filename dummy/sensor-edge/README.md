Die Komponente sensor-edge simuliert einen Edge-Node, der Daten von einem Edge-Device empfängt, vorverarbeitet und in die Cloud sendet. Außerdem wird beim Über-/Unterschreiten kritischer Messwerte die Wetterlage banchbarter Plantagen (andere sensor-edge-nodes) abgefragt und die Bewässerung ggf. angepasst.
Die Komponente Arbeitet mit Daten im int-Format und führt ggf. Casts durch. 
Die Namen der Sensoren, Typen der Sensoren und die Worker-ID werden automatisch generiert.
Es werden vier Sensor-Typen unterstützt: TEMPERATURE, HUMIDITY, SOILMOISTURE, PRECIPITATION.
 
<h2> Konfiguration: </h2>
  Programmagumente können in der application.properties oder als Java-Argumente übergeben werden.
  Alle Argumente sind obligatrisch.
  
  <h3>Konfiguration</h3>
  <p><b>targetNode.host</b> - Host des zugehörigen Cloud-Nodes</p>
  <p><b>targetNode.port</b> - Port des zugehörigen Cloud-Nodes</p> 
  <p><b>targetNode.url</b>  - URL des zugehörigen Cloud-Nodes (default / )</p>
  <p><b>intervall</b> - Aggregationsintervall (Alle n Einträge wird ein Median berechnet und in die Cloud gesendet)</p>
  <p><b>edge.endpoints</b> - Eine Liste von Endpoints anderer Edge Worker Nodes für Edge-to-Edge-Kommunikation. Der Parameter wird als eine Liste von Strings (CSV,Delimiter , ) übergeben. Die Endpoint Strings müssen die Protokoll-Angabe beinhalten. (http:// beginnen)</p>
   <p><b>intervall</b> - Aggregationsintervall (Alle n Einträge wird ein Median berechnet und in die Cloud gesendet)</p>
  <p><b>irrigation.host </b> - Host der Bewässerungsanlage.</p>
  <p><b>irrigation.port</b> - Port der Bewässerungsanlage.</p>
  <p><b>criticalTemp</b> - Kritische Temperatur, die beim Überschreiten eine Anpassung der Bewässerung auslöst.</p>
     <p><b>criticalHumidity</b> - Kritische Luftfeuchtigkeit, die beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
  <p><b>criticalsoilMoisture</b> - Kritische Bodenfeuchtigkeit, die beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>
  <p><b>criticalPrecipitation</b> - Kritischer Niederschlag, der beim Unterschreiten eine Anpassung der Bewässerung auslöst.</p>

    <br></br>
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
  <b> POST /config </b> Parameter: JSON mit Konfiguration, die zur Laufzeit angepasst werden soll. Ein oder Mehrere Felder möglich (s.u.)
  ```JSON
  {
    "intervall": "2222"
  }
  ```
  <br></br>
