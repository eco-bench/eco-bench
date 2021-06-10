 

<h3><b>Application Properties</b></h3>
<br></br>
<ul>
  <li><b>benchmarkEndPointHost=</b> Host/IP des Endpoints für Benchmark-Logs-Verarbeiteung </li>
 <li><b>benchmarkEndpointPort=</b> Port des Endpoints für Benchmark-Logs-Verarbeiteung </li>
 <li><b>benchmarkEndpointURL=</b>  URL des Endpoints für Benchmark-Logs-Verarbeiteung, z.B.: "/" </li>
  <li><b>logStorageLimit=</b>  Puffergröße der Log-Einträge. z.B. Alle 100 Einträge werden die Logs an den Endpoint geschickt. Default 100</li>
</ul>

<br></br>
<h3>HTTP-Pfade</h3>
<p><b>POST /water</b> Neue Bewässerungskonfiguration als JSON übermitteln (siehe unten)</p>
<p><b>GET /water</b> Für Testzwecke. Alle gebufferten Konfigurationen abrufen.</p>
<p><b>POST /config</b> Properties zur Laufzeit Anpassen als JSON.z.B. für logStorageLimit {"logStorageLimit":123} </p>
<p><b>GET /config</b> Properties als JSON anzeigen. </p>
<br></br>

<h3><b>HTTP-Input/Output</b></h3>
 <p>Format: JSON-Datein</p>
<ul>
  <li><b>waterPressure</b> <br></br>Wasserdruck in Bar.  </li><br></br>
 <li><b>timeDelta</b> <br></br> Timestamp-Differenz für Latency-Messung. Als Input zum Bewässerungssystem (POST) 
 lediglich Time-Stamp des Sensor-Workers beim Absenden der neuen Bewässerungsconfig. Als Output bei Logs die Differenz zwischen den Timestamps beim Senden vom Sensor Processing Worker und Empfangen auf dem Bewässerungssystem-Worker  </li> <br></br>
  <li><b>workerID</b> <br></br>  ID des Sensor Processing Workers </li>
</ul>
<p>Beispiel: </p>

```JSON
{
"waterPressure":12,
 "timeDelta": 23453424523,
 "workerID": "Test-Sensor-Worker01"
}
```
