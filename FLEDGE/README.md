

Es folgt eine detailliertere Anleitung als bei https://github.com/fledge-iot/fledge

Getestet mit:

Oracle Virtual Box 6.1
VM Ubuntu x64 1.5 GB Hauptspeicher, 10GB Festplattenspeicher
Ubuntu 18.04



# 1. Bei Ubuntu Python 2.7 (default) auf python mindestens 3.6.9 switchen (wegen aiohttp library):

```bash
sudo apt update 
```

Open your .bashrc file nano ~/.bashrc. 
Type alias python=python3 on to a new line at the top of the file then save the file with ctrl+o and close the file with ctrl+x. 
Then, back at your command line type source ~/.bashrc. Now your alias should be permanent.
```bash
sudo apt update
```

# 2. pip3 für python3 installieren 
```bash
sudo apt update
sudo apt install python3-pip
sudo pip3 install --upgrade pip
```
 
 
# 3. Dependencies installieren
Aus gihub + weitere dependencies:

```bash

pip3 install aiohttp
sudo apt-get install pkg-config 
sudo apt-get install curl
sudo apt-get install python-pip
sudo apt-get install libcurl4-openssl-dev
sudo apt-get install avahi-daemon curlsudo 
sudo apt-get install cmake g++ make build-essential autoconf automake uuid-dev
sudo apt-get install libtool libboost-dev libboost-system-dev libboost-thread-dev libpq-dev libssl-dev libz-dev
sudo apt-get install python-dev python3-dev python3-pip
sudo apt-get install postgresql
sudo apt-get install sqlite3 libsqlite3-dev
sudo apt-get install git
```

# 4. Sources abholen

Ordner für repo erstellen 
z.B. ~/Downloads/Fledge (home/ubuntu/Downloads/Fledge)
 
```bash
git init
git pull https://github.com/fledge-iot/fledge
``` 
 

# 5. Build

To build Fledge run the command make in the top level directory. 
This will compile all the components that need to be compiled and will also create a runable structure of the Python code components of Fledge.

# 6. Optional: Laufzeit-Test
  
  6.1  export FLEDGE_ROOT=~/Downloads/Fledge
  6.2  $FLEDGE_ROOT/scripts/fledge start 

# 7. Persistente Installation 
 Build im Fledge-Ordner erzeugen mit:   
```bash
   sudo make install
```
  Anschließend OS-Passwort eingeben.
 
    
# 8. Umgebungsvariable anlegen
```bash   
export FLEDGE_ROOT=/usr/local/fledge 
```
(Bei Fehlern: Falls Pfad später überschrieben wird - siehe Hinweis auf github. Bis jetzt keine Probleme)

# 9. Ausführen
```bash
$FLEDGE_ROOT/bin/fledge start
$FLEDGE_ROOT/bin/fledge status
```
# 10. Stoppen
```bash
$FLEDGE_ROOT/bin/fledge stop
```
